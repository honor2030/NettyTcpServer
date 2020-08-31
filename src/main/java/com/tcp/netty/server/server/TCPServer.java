package com.tcp.netty.server.server;

import com.tcp.netty.server.constants.CommonConstants;
import com.tcp.netty.server.util.LogUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.ArrayList;
import java.util.List;

public class TCPServer implements Runnable {

    int serverPort;

    public TCPServer(int serverPort)
    {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {

        LogUtil.log("Starting Netty TCP Server");

        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ChannelPipeline channelPipeline = socketChannel.pipeline();

                            TCPServerHandler tcpServerHandler = new TCPServerHandler(
                                    socketChannel.remoteAddress().getAddress().getHostAddress(),
                                    socketChannel.localAddress().getPort());

                            //  클라이어트 60초 응답없을시 끊어지도록 한다
                            channelPipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0));
                            channelPipeline.addLast(tcpServerHandler);
                        }
                    });

                //  다중 포트 연결일 경우 채널을 여러개 생성하여 바인딩 한다
                Channel channel = serverBootstrap.bind(this.serverPort).sync().channel();
                channel.closeFuture().sync();


        } catch(Exception e) {
            e.printStackTrace();

        } finally{

            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}

