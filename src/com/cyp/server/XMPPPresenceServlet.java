package com.cyp.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyp.room.RoomsManager;
import com.google.appengine.api.xmpp.Presence;
import com.google.appengine.api.xmpp.PresenceType;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

public class XMPPPresenceServlet extends HttpServlet {

	private static final long serialVersionUID = -5050471796866460566L;

	private Logger logger = Logger.getLogger(XMPPReceiverServlet.class
			.toString());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		XMPPService xmppService = XMPPServiceFactory.getXMPPService();
		Presence presence = xmppService.parsePresence(req);

		logger.info("New xmpp presence message :" + presence.getStanza());

		switch (presence.getPresenceType()) {
		case UNAVAILABLE:
			RoomsManager.getManager().leave(presence.getFromJid().getId().split("/")[0]);
			break;
		case AVAILABLE:			
			break;
		case PROBE:
			xmppService.sendPresence(presence.getFromJid(), PresenceType.AVAILABLE, presence.getPresenceShow(), presence.getStatus());
			break;
		default:
			break;
		}
	}	
}
