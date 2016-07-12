package com.web.chat.sock;

import java.io.IOException;

public interface socket
{
	public void init();
	
	public void request( String data ) throws IOException;

	public void response() throws IOException;
}
