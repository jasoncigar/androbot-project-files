package com.org.video.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class JEmailServlet extends HttpServlet {
	
	public static final Logger log = Logger.getLogger(JEmailServlet.class.getName());
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String strCallResult = "";
		
		try {
			String strTo = req.getParameter("email_to");
			String strSubject = req.getParameter("email_subject");
			String strBody = req.getParameter("body");
			URL url = new URL("http://jasoncigarcellbot.appspot.com/jemail?email_to="
					+ strTo + "&email_subject=" + strSubject + "&body="
					+ strBody);
			InputStream is = url.openStream();
			is.close();
			log.info(strTo);
			log.info(strSubject);
			log.info(strBody);
			
			if (strTo == null)
				throw new Exception("To field cannot be empty.");
			strTo = strTo.trim();

			if (strTo.length() == 0)
				throw new Exception("To field cannot be empty.");

			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("jason.cigar@gmail.com"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					strTo));
			msg.setSubject(strSubject);
			msg.setText(strBody);
			Transport.send(msg);
			strCallResult = "Success: " + "Email has been delivered.";
			resp.getWriter().println(strCallResult);
		} catch (Exception ex) {
			ex.printStackTrace();
			strCallResult = "Fail: " + ex.getMessage();
			resp.getWriter().println(strCallResult);
		}
	}
}
