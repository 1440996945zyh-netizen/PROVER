package com.yy.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.yy.common.log.MicroLogger;
import com.yy.common.util.BytesUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class CustomsDecoder extends ByteToMessageDecoder {

    private static final MicroLogger LOGGER = new MicroLogger(CustomsChannelInboundHandler.class);
    
    @Override
     protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {

    	LOGGER.error("收到对面发来的数据2：" + buffer.readableBytes());
    	
    	byte[] bs = new byte[buffer.readableBytes()];
        buffer.readBytes(bs);
        
    	out.add(customsPackageData(BytesUtils.toHexStringByte(bs)));
    }
    
    /**
     * 通讯协议处理
     * @param strings
     * @return
     */
	private CustomsBaseMessage customsPackageData(String[] strings) {

    	LOGGER.error("开始解析数据：" + strings);
		CustomsBaseMessage baseMessage = new CustomsBaseMessage();
//		
//		baseMessage.setMessage(StringUtils.join(BytesUtils.subByte(strings, 0, strings.length)));// 全部数据
//		baseMessage.setMessageStart(StringUtils.join(BytesUtils.subByte(strings, 0, 4)));
//		baseMessage.setMessageLength(Integer.parseInt(strings[4], 4));
//		baseMessage.setMessageType(StringUtils.join(BytesUtils.subByte(strings, 8, 1)));
//		baseMessage.setMessageChangZhanHao("");
//		baseMessage.setMessageTongDaoHao("");
//		baseMessage.setMessageJinChuKou("");
//		baseMessage.setMessageBiaoShiFu(StringUtils.join(BytesUtils.subByte(strings, 30, 4)));
//		baseMessage.setMessageXMLLength(Integer.parseInt(strings[34], 4));
//		baseMessage.setMessageXML(StringUtils.join(BytesUtils.subByte(strings, 
//				strings.length - 2 - baseMessage.getMessageXMLLength(), 
//				baseMessage.getMessageXMLLength())));
//		baseMessage.setMessageEnd(StringUtils.join(BytesUtils.subByte(strings, strings.length - 2, strings.length)));
//		
		return baseMessage;
	}
}