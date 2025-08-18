package com.yy.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
 
/**
 * 通道初始化
 */
public class CustomsChannelInitializer<SocketChannel> extends ChannelInitializer<Channel> {
 
	@Override
	protected void initChannel(Channel ch) throws Exception {
 
        ch.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast("decoder", new CustomsDecoder());
        ch.pipeline().addLast(new CustomsChannelInboundHandler());
	}
}