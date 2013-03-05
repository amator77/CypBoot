package com.cyp.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyp.room.Participant;
import com.cyp.room.Room;
import com.cyp.room.RoomsManager;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.MessageType;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class XMPPReceiverServlet extends HttpServlet {

	private static final long serialVersionUID = -6507073159053273900L;

	private Logger logger = Logger.getLogger(XMPPReceiverServlet.class
			.toString());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		XMPPService xmpp = XMPPServiceFactory.getXMPPService();
		Message message = xmpp.parseMessage(req);

		logger.info("New xmpp message :" + message.getStanza());

		JID fromJid = message.getFromJid();

		if (checkSubscription(fromJid.getId())) {
			String body = message.getBody();

			if (body.startsWith("/rooms ")) {
				sendMessage(fromJid,roomsToJSONString(RoomsManager.getManager()
								.listRooms()));
			} else if (body.startsWith("/users ")) {
				String roomName = body.substring("/users ".length());
				List<Participant> participants = RoomsManager.getManager()
						.listParticipants(roomName);
				sendMessage(fromJid, participantsToJSONString(participants));
			} else if (body.startsWith("/join ")) {
				String roomName = body.substring("/join ".length());
				int slot = RoomsManager.getManager().join(roomName,
						fromJid.getId());

				if (slot >= 0) {
					List<Participant> participants = RoomsManager.getManager()
							.listParticipants(roomName);
					sendMessage(fromJid, participantsToJSONString(participants));

					for (Participant p : participants) {
						if (!p.getJid().equals(fromJid.getId())) {
							sendMessage(new JID(p.getJid()),
									joinedJSON(slot, fromJid.getId()));
						}
					}
				} else {
					sendMessage(fromJid, "403");
				}
			} else if (body.startsWith("/leave")) {
				String roomName = body.substring("/leave ".length());				
				int slot = RoomsManager.getManager().leave(roomName,
						fromJid.getId());

				if (slot >= 0) {
					List<Participant> participants = RoomsManager.getManager()
							.listParticipants(roomName);

					for (Participant p : participants) {
						sendMessage(new JID(p.getJid()),
								leavedJSON(slot, fromJid.getId()));
					}

				} else {
					sendMessage(fromJid, "404");
				}
			}
		} else {
			sendMessage(fromJid, "403");
		}
	}

	private boolean checkSubscription(String jid) {
		return true;
	}

	private void sendMessage(JID recipient, String body) {
		Message message = new MessageBuilder().withRecipientJids(recipient)
				.withMessageType(MessageType.NORMAL).withBody(body).build();

		XMPPServiceFactory.getXMPPService().sendMessage(message);
	}

	private String roomsToJSONString(List<Room> rooms) {
		try {
			JSONArray array = new JSONArray();

			for (Room room : rooms) {
				JSONObject obj = new JSONObject();
				obj.put("name", room.getName());
				obj.put("size", room.getSize());
				obj.put("freeSlots", room.getFreeSlots());
				array.put(obj.toString());
			}

			return "/rooms "+ array.toString();
		} catch (JSONException e) {
			return null;
		}
	}

	private String participantsToJSONString(List<Participant> participants) {
		try {
			JSONArray array = new JSONArray();

			for (Participant participant : participants) {
				JSONObject obj = new JSONObject();
				obj.put("jid", participant.getJid());
				obj.put("slot", participant.getSlot());
				array.put(obj.toString());
			}

			return array.toString();
		} catch (JSONException e) {
			return null;
		}
	}

	private String joinedJSON(int slot, String jid) {
		StringBuffer sb = new StringBuffer("/joined ");
		JSONObject obj = new JSONObject();
		try {
			obj.put("slot", String.valueOf(slot));
			obj.put("jid", jid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		sb.append(obj.toString());
		return sb.toString();
	}

	private String leavedJSON(int slot, String jid) {
		StringBuffer sb = new StringBuffer("/leaved ");
		JSONObject obj = new JSONObject();
		try {
			obj.put("slot", String.valueOf(slot));
			obj.put("jid", jid);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		sb.append(obj.toString());
		return sb.toString();
	}
	
	public static void main(String[] args) {
		String body = "/join Main Room";
		String roomName = body.substring("/join".length()+1);
		System.out.println("["+roomName+"]");
	}
}
