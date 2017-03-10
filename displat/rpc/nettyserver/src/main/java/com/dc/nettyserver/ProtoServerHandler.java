package com.dc.nettyserver;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.dc.rpc.common.connection.Connection;
import com.dc.rpc.common.connection.ConnectionManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ProtoServerHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.log(Level.INFO, "channelActive");
		ConnectionManager.getInstance().createConnection(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.log(Level.INFO, "channelRead");
		com.dc.rpc.common.packages.Package pack = (com.dc.rpc.common.packages.Package) msg;
		logger.log(Level.INFO, "messageId====" + pack.getMessageId());
		Connection connection = ConnectionManager.getInstance().createConnection(ctx.channel());
		DataHandlerManager.process(pack, connection);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.log(Level.INFO, "channelReadComplete");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.log(Level.INFO, "exceptionCaught====" + cause.getMessage());
		cause.printStackTrace();
		ConnectionManager.getInstance().getConnection(ctx.channel()).close();
	}

	/**
	 * 心跳检测
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		if (evt instanceof IdleStateEvent) {

			IdleStateEvent event = (IdleStateEvent) evt;

			if (event.state().equals(IdleState.READER_IDLE)) {
				// 未进行读操作
				logger.info("client reader_idle");
				// 超时关闭channel
				ctx.close();
				ConnectionManager.getInstance().remove(ctx.channel());

			} else if (event.state().equals(IdleState.WRITER_IDLE)) {

			} else if (event.state().equals(IdleState.ALL_IDLE)) {
				// 未进行读写
				System.out.println("client ALL_IDLE");
				ctx.close();
				ConnectionManager.getInstance().remove(ctx.channel());

			}

		}
	}
}
