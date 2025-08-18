package com.yy.ppm.finance.webserver.webserviceserver;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class WebServicePublish implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		/*String address = "http://172.29.61.100:8079/webservice/WebService";
		Endpoint.publish(address, new WebServiceImpl());*/
//		javax.xml.ws.Endpoint.publish(address, new WebServiceImpl());
		System.out.println("发布成功");
	}

//	public static void main(String[] arrgs) {
//		String address = "http://192.168.23.17/webService/WebService";
//		javax.xml.ws.Endpoint.publish(address, new WebServiceImpl());
//		System.out.println("发布成功");
//	}

}
