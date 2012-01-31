package com.org.video.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventListenerServiceAsync {

	void handleMouseUp(String name, String botid, AsyncCallback<String> callback);

	void handleMouseDown(String name, String botid,
			AsyncCallback<String> callback);

	void getLag(long time, String botid, AsyncCallback<Long> callback);

	void sendEmail(String emailid, String botid, AsyncCallback<String> callback);





}
