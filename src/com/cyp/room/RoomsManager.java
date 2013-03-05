package com.cyp.room;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class RoomsManager {

	private static final RoomsManager manager = new RoomsManager();

	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	private Logger logger = Logger.getLogger(RoomsManager.class.toString());

	private RoomsManager() {
	}

	public static RoomsManager getManager() {
		return RoomsManager.manager;
	}

	public List<Room> listRooms() {
		List<Room> rooms = new ArrayList<Room>();
		Query q = new Query("Room");
		PreparedQuery pq = datastore.prepare(q);

		if (pq.countEntities(FetchOptions.Builder.withDefaults()) > 0) {

			for (Entity room : pq.asIterable()) {
				Room r = new Room();
				r.setName(room.getKey().getName());
				r.setSize( Integer.parseInt(room.getProperty("size").toString()));
				r.setFreeSlots(this.countRoomEmptySlots(room));
				rooms.add(r);
			}
		} else {
			Key roomKey = KeyFactory.createKey("Room", "Main Room");
			Entity roomEntity = new Entity(roomKey);
			roomEntity.setProperty("size", 100);
			datastore.put(roomEntity);
			Room r = new Room();
			r.setName("Main Room");
			r.setSize(100);
			r.setFreeSlots(100);
			rooms.add(r);
		}

		return rooms;
	}

	public List<Participant> listParticipants(String roomName) {
		List<Participant> participants = new ArrayList<Participant>();

		try {
			Entity room = datastore.get(KeyFactory.createKey("Room", roomName));

			for (int i = 0; i < (Long) room.getProperty("size"); i++) {
				String jid = (String) room.getProperty(String.valueOf(i));

				if (jid != null) {
					participants.add(new Participant(jid, i));
				}
			}
		} catch (EntityNotFoundException e) {
			logger.log(Level.SEVERE, "Room not found!", e);
		}

		return participants;
	}

	public int join(String roomName, String jid) {
		logger.log(Level.INFO, "Contact " + jid + " is joining on room :"
				+ roomName + " ");

		try {
			Entity room = datastore.get(KeyFactory.createKey("Room", roomName));
//			Entity contact = datastore.get(KeyFactory.createKey("Contact",jid.split("/")[0]));
			int roomSlot = getRoomEmptySlot(room);

			if (roomSlot >= 0) {
				room.setProperty(String.valueOf(roomSlot), jid);
				datastore.put(room);
				return roomSlot;
			} else {
				logger.log(Level.INFO, "Room is full");
				return -1;
			}
		} catch (EntityNotFoundException e) {
			logger.log(Level.SEVERE, "Room or contact not found!", e);
		}

		return -1;
	}

	public int leave(String roomName, String jid) {
		logger.log(Level.INFO, "Contact " + jid + " is leaving room :"
				+ roomName + " ");

		try {
			Entity room = datastore.get(KeyFactory.createKey("Room", roomName));

			for (int i = 0; i < (Long) room.getProperty("size"); i++) {
				if (jid.equals(room.getProperty(String.valueOf(i)))) {
					room.removeProperty(String.valueOf(i));
					datastore.put(room);
					return i;
				}
			}

		} catch (EntityNotFoundException e) {
			logger.log(Level.SEVERE, "Room not found!", e);
		}

		return -1;
	}

	public void leave(String jid) {
		logger.log(Level.INFO, "Contact " + jid + " is leaving");
		
		Query q = new Query("Room");
		PreparedQuery pq = datastore.prepare(q);

		for (Entity room : pq.asIterable()) {
			for (int i = 0; i < (Long) room.getProperty("size"); i++) {
				if (jid.equals(room.getProperty(String.valueOf(i)))) {
					room.removeProperty(String.valueOf(i));
					datastore.put(room);
					return;
				}
			}
		}
	}

	public boolean registerContact(String username) {
		Key key = KeyFactory.createKey("Contact", username);

		try {
			datastore.get(key);
			return false;
		} catch (EntityNotFoundException e) {
			Entity contact = new Entity(key);
			contact.setProperty("rating", 1500);
			contact.setProperty("createTime", new Date().getTime());
			contact.setProperty("updateTime", new Date().getTime());
			datastore.put(contact);
			return true;
		}
	}

	public void removeContact(String username) {
		Key key = KeyFactory.createKey("Contact", username);
		datastore.delete(key);
	}

	public Contact findContact(String username) {
		Key key = KeyFactory.createKey("Contact", username);

		try {
			Entity e = datastore.get(key);
			Contact c = new Contact(username);
			c.setRating((Integer) e.getProperty("rating"));
			return c;
		} catch (EntityNotFoundException e) {
			return null;
		}
	}

	private int getRoomEmptySlot(Entity room) {
		for (int i = 0; i < (Long) room.getProperty("size"); i++) {
			if (room.getProperty(String.valueOf(i)) == null) {
				return i;
			}
		}

		return -1;
	}

	private int countRoomEmptySlots(Entity room) {
		int count = 0;

		for (int i = 0; i < (Long) room.getProperty("size"); i++) {
			if (room.getProperty(String.valueOf(i)) == null) {
				count++;
			}
		}

		return count;
	}
}