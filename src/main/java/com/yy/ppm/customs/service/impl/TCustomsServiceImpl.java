package com.yy.ppm.customs.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jakarta.annotation.Resource;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.yy.common.util.FtpUtils;
import com.yy.framework.config.CustomsZgConfig;
import com.yy.ppm.customs.bean.TCustomsDTO;
import com.yy.ppm.customs.bean.TDriverDTO;
import com.yy.ppm.customs.mapper.TCustomsMapper;
import com.yy.ppm.customs.service.TCustomsService;
import com.yy.ppm.produce.mapper.TPoundMapper;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.extra.ftp.Ftp;

/**
 * @ClassName 海关相关业务
 * @author ningjp
 * @version 1.0.0
 * @Description
 * @createTime 2024年01月02日 08:21:00
 */
@Service
public class TCustomsServiceImpl implements TCustomsService {

	private String serverUrl = "172.18.4.9";

	private String port = "21";

	private String username = "sendhg";

	private String password = "Qmsjy;;";

	private String basePath = "/";

    @Resource
    private CustomsZgConfig customsZgConfig;

    @Autowired
    private TCustomsMapper tCustomsMapper;

    @Autowired
    private Snowflake snowflake;

    @Override
    public void plan2customs() {
        try {
            //查询上次上传时间
            String lastTime = tCustomsMapper.getLastTime();
            //获取当前时间
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String currentTime = dateFormat.format(date);

            //查询需要上传海关的通知单
            List<TCustomsDTO> list = tCustomsMapper.getList(lastTime, currentTime);
            //组织数据
            if (!CollectionUtils.isEmpty(list)) {
                for (TCustomsDTO tCustomsDTO : list) {
                    List<TDriverDTO> driverList = null;
                    //查询派车信息
                    if ("疏港".equals(tCustomsDTO.getBillType())) {
                        driverList = tCustomsMapper.getDriverList(tCustomsDTO.getBusinessNo());
                    }else {
                        driverList = tCustomsMapper.getDyDriverList(tCustomsDTO.getId());
                    }
                    //放车辆信息
                    tCustomsDTO.setDriverList(driverList);

                    //生成报文
                    createSendXML(tCustomsDTO);
                }
            }
            // 更新时间

        }catch (Exception e){
            System.out.println("异常了:"+e.getMessage());
        }
    }

    private void createSendXML(TCustomsDTO tCustomsDTO) throws Exception {
        //从配置文件中读取海关报文信息

        String localTempPath = customsZgConfig.getLocalTempPath();

        //将磅单数据生成xml文件
        SAXReader sax=new SAXReader();//创建一个SAXReader对象
        InputStream in = TCustomsServiceImpl.class.getClassLoader().getResourceAsStream("xmltemplates/customs.xml");

        Document document=sax.read(in);//获取document对象,如果文档无节点，则会抛出Exception提前结束
        Element root=document.getRootElement();//获取根节点

        List<Element> firstList =  root.elements();
        Element MessageHead = firstList.get(0);
        Element MessageBody = firstList.get(1);


        List<Element> headList =  MessageHead.elements();
        Element MessageType =  headList.get(0);
        Element MessageId =  headList.get(1);
        Element MessageTime =  headList.get(2);
        Element SenderId =  headList.get(3);
        Element SenderAddress =  headList.get(4);
        Element ReceiverId =  headList.get(5);
        Element ReceiverAddress =  headList.get(6);
        Element PlatFormNo =  headList.get(7);
        Element CustomCode =  headList.get(8);
        Element SeqNo =  headList.get(9);
        Element Note =  headList.get(10);


        String randomNum =  generateNumber();
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmm");

        localTempPath = localTempPath+formatter1.format(currentTime) + "/" ;

        String messageId = formatter2.format(currentTime)+randomNum;
        MessageId.setText(messageId);
        MessageTime.setText(formatter.format(currentTime));


        List<Element> bodyList =  MessageBody.elements();
        Element WFG_DBXX =  (Element) bodyList.get(0).elements().get(0);
        List<Element> contentList = WFG_DBXX.elements();

        contentList.get(0).setText(tCustomsDTO.getBusinessNo() == null?"":tCustomsDTO.getBusinessNo());
        contentList.get(1).setText(tCustomsDTO.getCreateTime() == null?"":tCustomsDTO.getCreateTime());
        contentList.get(2).setText(tCustomsDTO.getBillType() == null?"":tCustomsDTO.getBillType());
        contentList.get(3).setText(tCustomsDTO.getCargoInfoNo() == null?"":tCustomsDTO.getCargoInfoNo());
        contentList.get(4).setText("00");
        contentList.get(5).setText("*");
        contentList.get(6).setText(tCustomsDTO.getStartTime() == null?"":tCustomsDTO.getStartTime());
        contentList.get(7).setText(tCustomsDTO.getEndTime() == null?"":tCustomsDTO.getEndTime());
        contentList.get(8).setText(tCustomsDTO.getScn() == null?"":tCustomsDTO.getScn());
        contentList.get(9).setText(tCustomsDTO.getShipName() == null?"":tCustomsDTO.getShipName());
        contentList.get(10).setText(tCustomsDTO.getVoyage() == null?"":tCustomsDTO.getVoyage());
        contentList.get(11).setText(tCustomsDTO.getTradeType() == null?"":tCustomsDTO.getTradeType());
        contentList.get(12).setText(tCustomsDTO.getCargoAgentName() == null?"":tCustomsDTO.getCargoAgentName());
        contentList.get(13).setText(tCustomsDTO.getCargoOwnerName() == null?"":tCustomsDTO.getCargoOwnerName());
        contentList.get(14).setText(tCustomsDTO.getCargoName() == null?"":tCustomsDTO.getCargoName());
        contentList.get(15).setText("*");
        contentList.get(16).setText(tCustomsDTO.getPackingName() == null?"":tCustomsDTO.getPackingName());
        contentList.get(17).setText("*");
        contentList.get(18).setText("*");
        contentList.get(19).setText("null");
        contentList.get(20).setText("null");
        contentList.get(21).setText("0");
        contentList.get(22).setText(tCustomsDTO.getQuantity() == null?"0":tCustomsDTO.getQuantity());
        contentList.get(23).setText(tCustomsDTO.getTon() == null?"0":tCustomsDTO.getTon());
        contentList.get(24).setText("null");
        contentList.get(25).setText("汽车");
        contentList.get(26).setText("单车回皮");
        contentList.get(27).setText("0");
        contentList.get(28).setText("null");
        String storehouseName = tCustomsDTO.getStorehouseName() == null?"":tCustomsDTO.getStorehouseName();
        String regionName = tCustomsDTO.getRegionName()== null?"":tCustomsDTO.getRegionName();
        contentList.get(29).setText(storehouseName + "/" + regionName);
        contentList.get(30).setText("null");
        contentList.get(31).setText("49");
        contentList.get(32).setText("A");
        contentList.get(33).setText("null");

        if (!CollectionUtils.isEmpty(tCustomsDTO.getDriverList())) {
            for (TDriverDTO dto : tCustomsDTO.getDriverList()) {
                Element TRUCK_DETAIL = WFG_DBXX.addElement("TRUCK_DETAIL");
                TRUCK_DETAIL.addElement("ID").setText(dto.getId());
                TRUCK_DETAIL.addElement("INFORM_NO").setText(tCustomsDTO.getBusinessNo() == null?"":tCustomsDTO.getBusinessNo());
                TRUCK_DETAIL.addElement("TRANSPORT_NO").setText(dto.getTruckNo() == null?"":dto.getTruckNo());
                TRUCK_DETAIL.addElement("TRUCKER_NAM").setText(dto.getDriver() == null?"":dto.getDriver());
                TRUCK_DETAIL.addElement("LINK_TEL").setText(dto.getTel() == null?"":dto.getTel());
                TRUCK_DETAIL.addElement("ID_NUMBER").setText(dto.getIdNumber() == null?"":dto.getIdNumber());
                TRUCK_DETAIL.addElement("BEG_TIM").setText(dto.getStartTime() == null?"":dto.getStartTime());
                TRUCK_DETAIL.addElement("END_TIM").setText(dto.getEndTime() == null?"":dto.getEndTime());
            }
        }
        File folder = new File(localTempPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File xmlFile = new File(localTempPath+messageId+".xml");
        FileOutputStream outStream = new FileOutputStream(xmlFile);
        Writer wr = new OutputStreamWriter(outStream, "UTF-8");
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");//设置编码格式

        XMLWriter xmlWriter = new XMLWriter(wr,format);

        xmlWriter.write(document);
        xmlWriter.close();

        //通过ftp发送报文给海关
//        sendXML(localTempPath+messageId+".xml",messageId+".xml",tPoundPO);
    }

    public void sendXML(String xmlFilePath,String xmlFileName) throws Exception{

        String ftp_host = customsZgConfig.getHost();
        String ftp_port = customsZgConfig.getPort();
        String ftp_username = customsZgConfig.getUsername();
        String ftp_password = customsZgConfig.getPassword();
        String ftp_saveFilePath = customsZgConfig.getSaveFilePath();

        File file = new File(xmlFilePath);
        InputStream inputStream =  new FileInputStream(file);

        FtpUtils ftp = new FtpUtils();
        ftp.uploadFile(ftp_saveFilePath,ftp_host,ftp_port,ftp_username,ftp_password, xmlFileName, inputStream);
        //现在由于我们无法登陆到服务器上看文件，提供以下方法down下来核对上传文件是否成功
//        ftp.downloadFile(ftp_saveFilePath, xmlFileName, "E://");
        System.out.println("上传ok");

//        uploadFileByFtp(ftp_host, ftp_port, ftp_username, ftp_password, ftp_saveFilePath, xmlFileName, in);
    }

    /**
     * 生成随机数
     * @return
     */
    public static String generateNumber() {
        String no="";
        int num[]=new int[8];
        int c=0;
        for (int i = 0; i < 8; i++) {
            num[i] = new Random().nextInt(10);
            c = num[i];
            for (int j = 0; j < i; j++) {
                if (num[j] == c) {
                    i--;
                    break;
                }
            }
        }
        if (num.length>0) {
            for (int i = 0; i < num.length; i++) {
                no+=num[i];
            }
        }
        return no;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9006);
        System.out.println("-----正在监听9006端口---");
        Socket socket = server.accept();
        InputStream is = socket.getInputStream();
        // 包头
        byte[] bHender = new byte[4];
        is.read(bHender);

        // 总长
        byte[] bTotal = new byte[4];
        is.read(bTotal);
        int len = FtpUtils.toInt(bTotal);

        // 总长
        byte[] bContent = new byte[26];
        is.read(bContent);

        // XML长度
        byte[] bXmlFlow = new byte[4];
        is.read(bXmlFlow);
        int lenXml = FtpUtils.toInt(bXmlFlow);
        System.out.println(lenXml);

        byte[] bXml = new byte[lenXml];
        is.read(bXml);

        // 关闭资源
        is.close();
        socket.close();
        server.close();
        String result = new String(bXml, StandardCharsets.UTF_8);
        System.out.println(result);

    }

    /**
     * @Title: int2Bytes
     * @Description: 数据长度
     * @param: @param num
     * @param: @return
     * @return: byte[]
     */
    public static byte[] int2Bytes(int num, int len) {
        StringBuffer sb = new StringBuffer(String.valueOf(num));
        int length = len - sb.length();
        for (int i = 0; i < length; i++) {
            sb.insert(0, '0');
        }
        return sb.toString().getBytes();
    }
    //-------------------------------------------------以下为zcc新增---------------------------------------
    /**
     * 计划写入海关
     */
	@Override
	public void plan2customsXML() {

		List<Map<String, Object>> listMaps = tCustomsMapper.getTosPlanList();
		String str = "";
		String ids = "1,";
		SimpleDateFormat frt = new SimpleDateFormat("yyyyMMddHHmmssSSSSSS");
		String fileName = "";
		if (listMaps.size() != 0)
			for (Map<String, Object> map : listMaps) {
			    str = getStr2(map);
			    ids = ids + map.get("AUTO_ID") + ",";
			    fileName = frt.format(new Date()) + "_DBXX.xml";
			    boolean a = writeFileToFtp(this.basePath, fileName, new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))).booleanValue();
			    writeLocalFile(fileName, str);
			}
		if (ids.length() > 0)
			ids = ids.substring(0, ids.length() - 1);
			tCustomsMapper.deleteTosPlanList(ids.split(","));
			//String delSql = "delete from WFG_GCTOS_POUND.TOS_PLAN where auto_id in (" + ids + ")";
	}
	public Boolean writeFileToFtp(String vebackDir, String fileName, InputStream is) {
	    boolean hasDir = false;
	    try {
	      Ftp ftp = new Ftp(this.serverUrl, Integer.parseInt(this.port), this.username, this.password);
	      hasDir = ftp.upload(vebackDir, fileName, is);
	      if (!hasDir)
	    	  System.out.println("上传FTP失败!");
	      ftp.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return Boolean.valueOf(hasDir);
	  }
	public static void writeLocalFile(String fileName, String content) {
	    SimpleDateFormat frt = new SimpleDateFormat("yyyyMMdd");
	    String directory = "C:\\upFTPback\\" + frt.format(new Date());
	    File file = new File(directory);
	    FileOutputStream fos = null;
	    if (!file.exists())
	      file.mkdirs();
	    File file2 = new File(directory, fileName);
	    if (!file2.exists())
	      try {
	        file2.createNewFile();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    try {
	      fos = new FileOutputStream(directory + "\\" + fileName);
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    }
	    byte[] inC = content.getBytes();
	    try {
	      fos.write(inC);
	      fos.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	  }

	public String getStr2(Map map) {

		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \r\n<DTC_Message> \r\n<MessageHead> \r\n<MESSAGETYPE>DBXX</MESSAGETYPE> \r\n<MESSAGEID>" + ("9" + snowflake.nextIdStr()) + "</MESSAGEID> \r\n<MESSAGETIME>" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).substring(0, 19) + "</MESSAGETIME> \r\n<SENDERID>JN30</SENDERID> \r\n<SENDERADDRESS>JN30</SENDERADDRESS> \r\n<RECEIVERID>4310</RECEIVERID> \r\n<RECEIVERADDRESS>4310</RECEIVERADDRESS> \r\n<PLATFORMNO>SDGK666666</PLATFORMNO> \r\n<CUSTOMCODE>4310</CUSTOMCODE> \r\n<SEQNO /> \r\n<NOTE /> \r\n</MessageHead> \r\n<MessageBody> \r\n<DTCFlow> \r\n";
		str = str + "<WFG_DBXX > \r\n";
		str = str + "<AUTO_ID>" + (String)map.get("AUTO_ID") + "</AUTO_ID> \r\n";
		str = str + "<SCN>" + (String)map.get("SCN") + "</SCN> \r\n";
		str = str + "<BIZ_TYPE>" + (String)map.get("BIZ_TYPE") + "</BIZ_TYPE> \r\n";
		str = str + "<TRAF_NAME>" + (String)map.get("TRAF_NAME") + "</TRAF_NAME> \r\n";
		str = str + "<TRAF_CODE>" + (String)map.get("TRAF_CODE") + "</TRAF_CODE> \r\n";
		str = str + "<VOYAGE_NO>" + (String)map.get("VOYAGE_NO") + "</VOYAGE_NO> \r\n";
		str = str + "<IC_CARD>" + (String)map.get("IC_CARD") + "</IC_CARD> \r\n";
		str = str + "<TRADE_NAME>" + (String)map.get("TRADE_NAME") + "</TRADE_NAME> \r\n";
		str = str + "<GOODS_NAME>" + (String)map.get("GOODS_NAME") + "</GOODS_NAME> \r\n";
		str = str + "<CAR_NO>" + (String)map.get("CAR_NO") + "</CAR_NO> \r\n";
		str = str + "<GOODS_WT>" + map.get("GOODS_WT").toString() + "</GOODS_WT> \r\n";
		str = str + "<GROSS_WT>" + map.get("GROSS_WT").toString() + "</GROSS_WT> \r\n";
		str = str + "<TARE_WT>" + map.get("TARE_WT").toString() + "</TARE_WT> \r\n";
		str = str + "<ISINVALID>" + (String)map.get("ISINVALID") + "</ISINVALID> \r\n";
		str = str + "<AREA_CODE>" + (String)map.get("AREA_CODE") + "</AREA_CODE> \r\n";
		str = str + "<CUSTOMS_CODE>" + (String)map.get("CUSTOMS_CODE") + "</CUSTOMS_CODE> \r\n";
		str = str + "<EXTEND_FIELD_1>" + (String)map.get("INFORM_NO") + "</EXTEND_FIELD_1> \r\n";
		str = str + "<INPUT_CODE>" + (String)map.get("INPUT_CODE") + "</INPUT_CODE> \r\n";
		str = str + "<INPUT_NAME>" + (String)map.get("INPUT_NAME") + "</INPUT_NAME> \r\n";
		str = str + "<DECLARE_CODE>" + (String)map.get("DECLARE_CODE") + "</DECLARE_CODE> \r\n";
		str = str + "<DECLARE_NAME>" + (String)map.get("DECLARE_NAME") + "</DECLARE_NAME> \r\n";
		str = str + "<DECLARE_PERSON>" + (String)map.get("DECLARE_PERSON") + "</DECLARE_PERSON> \r\n";
		str = str + "<DECLARE_DATE>" + transferDate(map.get("DECLARE_DATE").toString()) + "</DECLARE_DATE> \r\n";
		str = str + "</WFG_DBXX > \r\n";
		str = str + "</DTCFlow> \r\n</MessageBody> \r\n</DTC_Message> \r\n";
		return str;
	}

	public static String transferDate(String dateStr) {
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	    DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    Date date1 = null;
	    try {
	    	date1 = df2.parse(dateStr);
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	    return df.format(date1);
	}
}
