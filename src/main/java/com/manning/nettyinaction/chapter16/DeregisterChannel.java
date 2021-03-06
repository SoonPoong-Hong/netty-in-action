package com.manning.nettyinaction.chapter16;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author <a href="mailto:nmaurer@redhat.com">Norman Maurer</a>
 */
public class DeregisterChannel {

	public void deregister() {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(group).channel(NioSocketChannel.class).handler(new SimpleChannelInboundHandler<ByteBuf>() {
			@Override
			protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
				ctx.deregister();
			}
		});
		ChannelFuture future = bootstrap.connect(new InetSocketAddress("www.manning.com", 80));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				if (channelFuture.isSuccess()) {
					System.out.println("Connection established");
				} else {
					System.err.println("Connection attempt failed");
					channelFuture.cause().printStackTrace();
				}
			}
		});

		// Do something which takes some amount of time

		// Register channel again on eventloop
		Channel channel = future.channel();
		group.register(channel).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					System.out.println("Channel registered");
				} else {
					System.err.println("Register Channel on EventLoop failed");
					future.cause().printStackTrace();
				}
			}
		});
	}
}
