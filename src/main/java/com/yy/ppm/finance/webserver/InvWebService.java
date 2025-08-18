package com.yy.ppm.finance.webserver;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.Servlet;


@WebService(targetNamespace="http://wfg.com",serviceName="InvWebService")
public interface InvWebService {


	 @WebMethod
	 public String getInvoiceData(@WebParam(name="data") String data);
}
