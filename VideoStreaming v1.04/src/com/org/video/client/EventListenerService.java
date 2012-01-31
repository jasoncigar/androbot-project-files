package com.org.video.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("event")
public interface EventListenerService extends RemoteService {
	String handleMouseUp(String name,String botid) throws IllegalArgumentException;
	String handleMouseDown(String name,String botid) throws IllegalArgumentException;
	long getLag(long time,String botid) throws IllegalArgumentException;
	String sendEmail(String emailid,String botid) throws IllegalArgumentException;
	void persistData(String  BOT_ID,String streetAddress)throws IllegalArgumentException;
	
}
