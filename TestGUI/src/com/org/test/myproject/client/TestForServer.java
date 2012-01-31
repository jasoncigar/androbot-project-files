package com.org.test.myproject.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("TestForServer")
public interface TestForServer extends RemoteService {

	boolean checkUser(String username, String passwrd)
			throws IllegalArgumentException;

}
