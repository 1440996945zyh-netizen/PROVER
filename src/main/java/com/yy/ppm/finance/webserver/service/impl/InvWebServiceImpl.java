package com.yy.ppm.finance.webserver.service.impl;

import com.yy.common.log.MicroLogger;
import com.yy.ppm.finance.mapper.TFdCreditDebitBillMapper;
import com.yy.ppm.finance.mapper.TFdInvoiceMapper;
import com.yy.ppm.finance.webserver.InvWebService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jakarta.jws.WebService;
import java.util.HashMap;

@WebService(targetNamespace="http://wfg.com")
public class InvWebServiceImpl implements InvWebService {

	public InvWebServiceImpl(TFdInvoiceMapper invoiceMapper, TFdCreditDebitBillMapper tFdCreditDebitBillMapper) {
		this.invoiceMapper = invoiceMapper;
		this.tFdCreditDebitBillMapper = tFdCreditDebitBillMapper;
	}

	public InvWebServiceImpl() {}


	/**
	 * 日志组件
	 */
	private static final MicroLogger LOGGER = new MicroLogger(InvWebServiceImpl.class);

	private TFdInvoiceMapper invoiceMapper;
	private TFdCreditDebitBillMapper tFdCreditDebitBillMapper;

	/**
	 * 远得接口
	 */
	@Override
	public String getInvoiceData(String data) {
//		data =
//		"<?xml version='1.0' encoding='GBK'?>" +
//				"<RetData>" +
//				"    <Number>CN2300001</Number>" +
//				"    <InfoTypeCode>发票代码</InfoTypeCode>" +
//				"    <InfoNumber>发票号码</InfoNumber>" +
//				"    <Pdfurl>电子发票下载地址</Pdfurl>" +
//				"    <PdfMsg>生成 pdf 文件失败原因</PdfMsg>" +
//				"</RetData>";
//		System.out.println(data);
		LOGGER.info("远得接口回传发票号码:"+data);
		HashMap map=new HashMap();
		String returnData="";
		try {
			Document doc = DocumentHelper.parseText(data);
			Element rootElt = doc.getRootElement();// 获取根节点
			Element valueNumber = rootElt.element("Number");// 获取返回代码Number
			Element valueInfoNumber = rootElt.element("InfoNumber");// 获取发票号码
			Element valueInfoTypeCode = rootElt.element("InfoTypeCode");// 获取发票代码
			Element valuePdfurl = rootElt.element("Pdfurl");// 获取电子发票下载地址
			Element valuePdfMsg = rootElt.element("PdfMsg");// 获取生成 pdf 文件失败原因
			String number =valueNumber.getStringValue();
			String infoNumber =valueInfoNumber.getStringValue();
			String infoTypeCode =valueInfoTypeCode.getStringValue();
			String pdfurl =valuePdfurl.getStringValue();
			String pdfMsg =valuePdfMsg.getStringValue();
			map.put("number", number);//系统发票号码
			map.put("machineInvoiceNum", infoTypeCode);//远得发票机器代码
			map.put("infoTypeCode", infoNumber);//远得发票号码
			map.put("pdfurl", pdfurl);
			map.put("pdfmsg", pdfMsg);
			String type=number.substring(0, 2);
			int inv=0;
			//船舶开头：VI，  货物开头：CI ， 杂项开头：MI，    堆存费开头：SI
			if("VI".equals(type) || "CI".equals(type) || "MI".equals(type) || "SI".equals(type)){
				LOGGER.info("更新发票编码为："+number);
				if(number.indexOf("-")!=-1){
					String[] numberList=number.split("-");
					for (String numberStr : numberList) {
						map.put("number", numberStr);
						//更新发票
						inv=invoiceMapper.updateByInvoiceNumber(map);
					}
				}else{
					//更新发票
					inv=invoiceMapper.updateByInvoiceNumber(map);
				}
			}else if("CN".equals(type) || "DN".equals(type)){
				LOGGER.info("更新冲销发票编码为："+number);
				if(number.indexOf("-")!=-1){
					String[] numberList=number.split("-");
					for (String numberStr : numberList) {
						map.put("number", numberStr);
						//更新发票--冲销发票
						inv=tFdCreditDebitBillMapper.updateByNumber(map);
					}
				}else{
					//更新发票
					inv=tFdCreditDebitBillMapper.updateByNumber(map);
				}
			}else{
				inv=-1;
			}
			//int inv=1;//测试
			if(inv>0){
				LOGGER.info("更新发票编码成功："+number);
				returnData="<?xml version='1.0' encoding='GBK'?>"
								+"<RetData> "
								+"<Number>"+number+"</Number>"
								+" <RetCode>0</RetCode> "
								+" <RetMsg>更新成功</RetMsg> "
								+"</RetData> ";
			}else if(inv==0){
				LOGGER.error("更新发票编码失败："+number);
				returnData="<?xml version='1.0' encoding='GBK'?>"
						+"<RetData> "
						+"<Number>"+number+"</Number>"
						+" <RetCode>1</RetCode> "
						+" <RetMsg>更新发票号码失败</RetMsg> "
						+"</RetData> ";
			}else{
				LOGGER.error("不是系统发票编号"+number+"；发票号码"+infoNumber);
				returnData="<?xml version='1.0' encoding='GBK'?>"
						+"<RetData> "
						+"<Number>"+number+"</Number>"
						+" <RetCode>1</RetCode> "
						+" <RetMsg>不是系统发票编号</RetMsg> "
						+"</RetData> ";
			}
		}catch (DocumentException e) {
			e.printStackTrace();
		}
		return returnData;
	}

}
