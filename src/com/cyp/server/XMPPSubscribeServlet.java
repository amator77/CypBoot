package com.cyp.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyp.room.RoomsManager;
import com.google.appengine.api.xmpp.Subscription;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

public class XMPPSubscribeServlet extends HttpServlet {

	private static final long serialVersionUID = -5050471796866460566L;

	private Logger logger = Logger.getLogger(XMPPReceiverServlet.class
			.toString());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		XMPPService xmppService = XMPPServiceFactory.getXMPPService();
		Subscription sub = xmppService.parseSubscription(req);

		logger.info("New xmpp subscribe message :" + sub.getStanza());
				
		switch (sub.getSubscriptionType()) {
		case SUBSCRIBE :			
			break;
		case SUBSCRIBED :
			RoomsManager.getManager().registerContact(sub.getFromJid().getId().split("/")[0]);
			break;
		case UNSUBSCRIBE :
			break;
		case UNSUBSCRIBED :
			RoomsManager.getManager().removeContact(sub.getFromJid().getId().split("/")[0]);
			break;
		default:
			break;
		}
	}		
		
	
}
