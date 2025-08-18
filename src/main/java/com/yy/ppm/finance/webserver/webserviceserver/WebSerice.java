package com.yy.ppm.finance.webserver.webserviceserver;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface  WebSerice {

	//获取船舶申报信息
	@WebMethod
	String saveShipvoyage(@WebParam(name ="xmlStr")String xmlStr);
	
	//上传发票信息
	/*@WebMethod
	String saveInvoice(@WebParam(name ="xmlStr")String xmlStr);*/
}
