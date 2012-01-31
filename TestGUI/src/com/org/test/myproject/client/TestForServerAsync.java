package com.org.test.myproject.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TestForServerAsync {

	void checkUser(String username, String passwrd,
			AsyncCallback<Boolean> callback);

}
