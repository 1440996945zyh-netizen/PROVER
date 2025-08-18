package com.yy.ppm.aisino.client;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.DecimalFormat;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;

public class ClientTest {
    public static void main(String[] args) throws ServiceException, RemoteException {
        WbServiceImplServiceLocator locator = new WbServiceImplServiceLocator();
        locator.setWbServiceImplPortEndpointAddress("http://172.19.4.146:8088/FPGLXT/CXF/WbService");
        WbServiceI helloWebservice = locator.getWbServiceImplPort();

        JSONObject driver = new JSONObject();
        JSONObject driverObj = new JSONObject();
        driverObj.put("Number","0000000001");
        driverObj.put("BusNo","0000000001");
        driverObj.put("Organ","02");
        driverObj.put("ClientName","个人");
        driverObj.put("ClientTaxCode","");
        driverObj.put("ClientBankAccount","");
        driverObj.put("ClientAddressPhone","");
        driverObj.put("ClientPhone","");
        driverObj.put("ClientMail","");
        driverObj.put("BillType","1");
        driverObj.put("InfoKind","51");
        driverObj.put("Notes","51");
        driverObj.put("InvoiceCode","51");
        driverObj.put("InvoiceNo","51");
        driverObj.put("Invoicer","51");
        driverObj.put("Checker","51");

        driverObj.put("Checker","51");
        driverObj.put("Cashier","51");
        driverObj.put("AllMoney","0");
        driverObj.put("SumMoney","10");
        driverObj.put("SpecialInvoice","");

        JSONObject driverObjSub = new JSONObject();
        driverObjSub.put("GoodsGroup","01");
        driverObjSub.put("GoodsName","01");
        driverObjSub.put("Standard","01");
        driverObjSub.put("Unit","01");
        driverObjSub.put("Num","2");
        driverObjSub.put("Price","117");
        driverObjSub.put("Amount","234");
        driverObjSub.put("TaxAmount","34");
        driverObjSub.put("TaxRate","0.17");
        driverObjSub.put("Aigo","");
        driverObjSub.put("AigoTax","");
        driverObjSub.put("GoodsNoVer","12");
        driverObjSub.put("GoodsTaxNo","01");
        driverObjSub.put("TaxPre","0");
        driverObjSub.put("TaxPreCon","01");
        driverObjSub.put("ZeroTax","01");
        driverObjSub.put("GoodsTaxName","01");
        JSONArray array= new JSONArray();
        array.add(driverObjSub);
        driverObj.put("InfoDetail",array);//子表数据

        JSONArray Array= new JSONArray();
        Array.add(driverObj);
        driver.put("InfoMaster",driverObj);


        String msg = helloWebservice.sendData(getString(driverObj,array));
        System.out.println("后面是返回数据"+msg);
    }

    public static String getString(JSONObject map,JSONArray invoiceItem) {
        //发票类型
        String invoiceType=map.get("InfoKind")==null?"":map.get("InfoKind").toString();
        String notes=map.get("Notes")==null?"":map.get("Notes").toString();//发票备注
        notes = notes.trim(); //去掉首尾空格
        notes = notes.replaceAll(" ", "_");//空格替换为下划线
        StringBuffer str = new StringBuffer();
        str.append("<?xml version='1.0' encoding='GBK'?>");
        str.append("<content><InfoMaster>");
        str.append("<Number>"+map.get("Number")+"</Number><ClientName>"+map.get("ClientName")+"</ClientName> "
                +" <InfoKind>"+map.get("InfoKind")+"</InfoKind> "
                +" <ClientTaxCode></ClientTaxCode> <ClientBankAccount></ClientBankAccount> "
                +" <ClientAddressPhone></ClientAddressPhone> "
                +" <Purchase>0</Purchase>  <Invoicer>"+map.get("Invoicer")+"</Invoicer>"
                +"  <Checker>"+map.get("Checker")+"</Checker> <Cashier>"+map.get("Cashier")+"</Cashier>"
                +"  <Notes>"+notes+"</Notes> ");
        for (int i = 0; i < invoiceItem.size(); i++) {
            JSONObject shipJson = invoiceItem.getJSONObject(i);//shipJson.get("rate_code")
            double mea_ton1=1;//数量1
            String meaTonStr2="0";//数量2
            String rateStr= "0.2";//费率
            String discountRateStr="0";//优惠费率
            String discAmtStr="0";//优惠金额
            String newRateStr="2";//新费率
            String jldw1=shipJson.get("jldw1")==null?"":shipJson.get("jldw1").toString();//单位1
            //String jldw2=shipJson.get("jldw2")==null?"1":shipJson.get("jldw2").toString();//单位2
            String taxRateStr=shipJson.get("tax_rate")==null?"0":shipJson.get("tax_rate").toString();//税率
            String amountStr=shipJson.get("amount")==null?"0":shipJson.get("amount").toString();
            String rateTypeId=shipJson.get("rateTypeId")==null?"":shipJson.get("rateTypeId").toString();//费率类型代码
            String rateName=shipJson.get("rate_name")==null?"":shipJson.get("rate_name").toString();
            String rateCode=shipJson.get("rate_code")==null?"":shipJson.get("rate_code").toString();
            if(newRateStr != null && !newRateStr.isEmpty() && !"0".equals(newRateStr)){
                rateStr=newRateStr;
            }
            if(meaTonStr2==null || meaTonStr2.isEmpty() || meaTonStr2==""){
                meaTonStr2="1";
            }
            double mea_ton2=Double.parseDouble(meaTonStr2);//数量2
            if(mea_ton2==0.00 || mea_ton2==0){
                mea_ton2=1;
            }
            if(taxRateStr==null || taxRateStr.isEmpty()){
                taxRateStr="0";
            }
            if(discountRateStr==null || discountRateStr.isEmpty() || discountRateStr==""){
                discountRateStr="0";
            }
            if(discAmtStr==null || discAmtStr.isEmpty() || discAmtStr==""){
                discAmtStr="0";
            }
            double tax_rate=Double.parseDouble(taxRateStr);//税率
            double discountRate=Double.parseDouble(discountRateStr);//优惠费率
            double rate=Double.parseDouble(rateStr);//费率
            double amount=Double.parseDouble(amountStr);//含税总额（加税）
            double discAmt=Double.parseDouble(discAmtStr);//优惠金额
            //rate=rate-discountRate;//实际的费率
            //amount=amount+discAmt;//未优惠的实际金额
            amount = addDouble(amount,discAmt);//未优惠的实际金额
            //double discTaxAmt=(discAmt/(1+tax_rate))*tax_rate;//优惠税额
            double discTaxAmt = multiplyDouble(divideDouble(discAmt,addDouble(1,tax_rate)),tax_rate);//优惠税额
            DecimalFormat df = new DecimalFormat("#.00");
            discTaxAmt = new BigDecimal(discTaxAmt+"").setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            /*discTaxAmt = Double.parseDouble(df.format(discTaxAmt));*/
            //amount = Double.parseDouble(df.format(amount));
            //discTaxAmt = Double.parseDouble(String.format("%.2f", discTaxAmt));
            //double tax_amt=(amount/(1+tax_rate))*tax_rate;
            //double tax_amt=amount*tax_rate;
            double mea=mea_ton1*mea_ton2;
            String meaStr=mea+"";
            rateStr=rate+"";
            String discAmtAigo=discAmt+"";
            String discTaxAmtAigo = (new BigDecimal(discTaxAmt+"").setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue())+"";
            //String discTaxAmtAigo=df.format(discTaxAmt);
            if("VI".equals(invoiceType)){
                jldw1="";
                meaStr="";
                rateStr="";
            }
            if("MiTW".equals(rateTypeId) || "报港服务费".equals(rateName) || "MS00265".equals(rateCode)
                    || "MS00266".equals(rateCode)|| "MS00319".equals(rateCode)|| "MS00320".equals(rateCode)){
                jldw1="";
                meaStr="";
                rateStr="";
            }
            if(discTaxAmt==0){
                discTaxAmtAigo="";
            }
            if(discAmt==0){
                discAmtAigo="";
            }

            if(rateName!="" && rateName.length()>3){
                String ratenameType=rateName.substring(0,3);
                if ("包干费".equals(ratenameType)) rateName="港口作业包干费";
            }

            if(rateName!="" && rateName.indexOf("（")!=-1){
                //去括号之前的数据为费率名称传给远得
                rateName=rateName.substring(0,rateName.indexOf("（"));
            }else if(rateName!="" && rateName.indexOf("(")!=-1){
                //去括号之前的数据为费率名称传给远得
                rateName=rateName.substring(0,rateName.indexOf("("));
            }
				/*if("CN".equals(invoiceType)){
					amount=-amount;
				}*/
            if(!"".equals(meaStr)&&!"".equals(rateStr)) {
                amount = multiplyDouble(Double.parseDouble(meaStr),Double.parseDouble(rateStr));//未优惠的实际金额
            }
            amount = new BigDecimal(amount+"").setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            str.append(" <InfoDetail> "
                    +"	  <GoodsGroup>01</GoodsGroup> "
                    +"	  <GoodsName>"+rateName+"</GoodsName> "
                    +"	  <Standard></Standard> "
                    +"	<Unit>"+jldw1+"</Unit> "
                    +"	<Num>"+meaStr+"</Num> "
                    //+"	  <Price>"+rateStr+"</Price> "
                    +"	  <Amount>"+amount+"</Amount> "
                    +"	  <TaxAmount></TaxAmount> "
                    +"	  <TaxRate>"+tax_rate+"</TaxRate> "
                    +"	  <Aigo>"+discAmtAigo+"</Aigo> "
                    +"	  <AigoTax>"+discTaxAmtAigo+"</AigoTax> "
                    +"</InfoDetail>");
        }		//"+shipJson.get("disc_amt")+""+discountRate+"
        str.append("</InfoMaster></content>");

        return str.toString();
    }

    public static double addDouble(double double1, double double2) {
        BigDecimal b1 = new BigDecimal(Double.toString(double1));
        BigDecimal b2 = new BigDecimal(Double.toString(double2));
        return b1.add(b2).doubleValue();
    }

    public  static double  divideDouble(double double1, double double2) {
        BigDecimal b1 = new BigDecimal(Double.toString(double1));
        BigDecimal b2 = new BigDecimal(Double.toString(double2));
        if (double2 == 0) {
            return 0.00;
        }
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double multiplyDouble(double double1,double double2) {
        BigDecimal b1 = new BigDecimal(Double.toString(double1));
        BigDecimal b2 = new BigDecimal(Double.toString(double2));
        return b1.multiply(b2).doubleValue();
    }
}
