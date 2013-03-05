package com.cyp.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.google.appengine.labs.repackaged.com.google.common.io.ByteStreams;

public class XMPPErrorServlet extends HttpServlet {
		
	private static final long serialVersionUID = 3574307085147368592L;
	
	private Logger logger = Logger.getLogger(XMPPErrorServlet.class
			.toString());

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		XMPPService xmppService = XMPPServiceFactory.getXMPPService();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ServletInputStream inputStream = req.getInputStream();
		ByteStreams.copy(inputStream, baos);

		logger.warning("Error stanza received: " + baos.toString());
	}		
}
