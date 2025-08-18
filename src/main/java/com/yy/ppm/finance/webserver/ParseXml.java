package com.yy.ppm.finance.webserver;

import com.yy.ppm.finance.bean.po.InvoicePO;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class ParseXml {
	
	/**
	 * 写入单据：接收返回的信息
	 * @author： czl
	 * @date： 2018-4-8 下午4:42:58
	 * @param xml
	 * @return List<InvoicePO>
	 */
	public static List<InvoicePO> sendData(String xml){
		List<InvoicePO> invoicePOList = new ArrayList<InvoicePO>();
		InvoicePO invoicePO = new InvoicePO();
		try{
			Document doc = DocumentHelper.parseText(xml);// 将字符串转为xml
			Element rootElt = doc.getRootElement();// 获取根节点
			Element valueNumber = rootElt.element("Number");// 获取业务流水号Number
			Element valueRetCode = rootElt.element("RetCode");// 获取返回代码RetCode
			Element valueRetMsg = rootElt.element("RetMsg");// 获取提示信息RetMsg
			
			String number =valueNumber.getStringValue();
			String retCode =valueRetCode.getStringValue();
			String retMsg = valueRetMsg.getStringValue();
			
			// 2. 返回信息接受
			invoicePO.setNumber(number);
			invoicePO.setRetCode(retCode);
			invoicePO.setRetMsg(retMsg);
			
			invoicePOList.add(invoicePO);
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return invoicePOList;
	}
	/**
	 * 获取单据，接收远得返回数据
	 * @author： czl
	 * @date： 2018-4-9 下午6:24:59
	 * @param xml
	 * @return List<InvoicePO>
	 */
	public static List<InvoicePO> parseTrainLoad(String xml){
		List<InvoicePO> invoicePOList = new ArrayList<InvoicePO>();
		InvoicePO invoicePO = new InvoicePO();
		try{
			Document doc = DocumentHelper.parseText(xml);// 将字符串转为xml
			Element rootElt = doc.getRootElement();// 获取根节点
			Element valueRetCode = rootElt.element("RetCode");// 获取返回代码RetCode
			Element valueRetMsg = rootElt.element("RetMsg");// 获取提示信息RetMsg
			Element valueInvoice = rootElt.element("Invoice");// 获取Invoice
			String retCode =valueRetCode.getStringValue();
			String retMsg = valueRetMsg.getStringValue();
			// 2. 返回信息接受
			invoicePO.setRetCode(retCode);
			invoicePO.setRetMsg(retMsg);
			invoicePOList.add(invoicePO);
			if("1".equals(retCode)){
				//接收失败返回
				return invoicePOList;
			}
			Element valueInfoTypeCode = valueInvoice.element("InfoTypeCode");
			Element valueInfoNumber = valueInvoice.element("InfoNumber");
			Element valueClientName = valueInvoice.element("ClientName");
			Element valueTime = valueInvoice.element("Time");
			Element valueMoney = valueInvoice.element("Money");
			Element valueTaxAmount = valueInvoice.element("TaxAmount");
			
			String infoTypeCode =valueInfoTypeCode.getStringValue();
			String infoNumber = valueInfoNumber.getStringValue();
			String time =valueTime.getStringValue();
			String clientName = valueClientName.getStringValue();
			double money =Double.parseDouble(valueMoney.getStringValue());
			double taxAmount = Double.parseDouble(valueTaxAmount.getStringValue());
			
			invoicePO.setInvoiceCode(infoTypeCode);
			invoicePO.setInvoiceNo(infoNumber);
			invoicePO.setTime(time);
			invoicePO.setClientName(clientName);
			invoicePO.setSumMoney(money*taxAmount);
			//测试
			/*invoicePO.setInvoiceCode("11111");
			invoicePO.setInvoiceNo("123456");
			invoicePO.setTime("");
			invoicePO.setClientName("寿光五福凯业");
			invoicePO.setSumMoney(1600);*/
			
			invoicePOList.add(invoicePO);
			return invoicePOList;
		
			
		} catch (Exception e){
			e.printStackTrace();
		}
		return invoicePOList;
	}
	
}
