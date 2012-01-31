package com.org.test.myproject.server;

import com.org.test.myproject.client.TestForServer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class TestForServerImpl extends RemoteServiceServlet implements
		TestForServer {

	@Override
	public boolean checkUser(String username, String passwrd)
			throws IllegalArgumentException {
		if (username.equals("jason") && passwrd.equals("testrpc")) {
			return true;
		} else {
			return false;
		}	}
}
