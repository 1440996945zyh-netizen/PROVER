package com.yy.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import com.yy.common.log.MicroLogger;

public class CustomsTcpClient {
    private static final MicroLogger LOGGER = new MicroLogger(CustomsTcpClient.class);
	private String host;
	  
  private int port;
  
  public CustomsTcpClient(String host, int port) {
    this.host = host;
    this.port = port;
  }
	  
	  public void setHost(String host) {
	    this.host = host;
	  }
	  
	  public void setPort(int port) {
	    this.port = port;
	  }
	  
	  public void sendMsg(String message) {
		    try(Socket socket = new Socket(this.host, this.port); 
		        DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
		      dos.write(message.getBytes("gb2312"));
		      dos.flush();
		      socket.shutdownOutput();
		    } catch (UnknownHostException e) {
		    	LOGGER.error("send socket message has occured UnknownHostException, host is {}, port is {}, message is {}: ");
		    } catch (IOException e) {
		    	LOGGER.error("send socket message has occured IOException, host is {}, port is {}, message is {}: ");
		    } 
		  }
}