/*
 * 天虹商场股份有限公司版权.
 */
package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.client.handler.Reciver;
import org.example.client.handler.StringCodec;
import org.example.client.handler.TerminalHandler;

/**
 * @author 严文泽
 * @version 1.0.0
 * @date 2020/8/18
 */
public class Client {

    public static void main(String[] args) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(600,5,0));
                    ch.pipeline().addLast(new LineBasedFrameDecoder(4096));
                    ch.pipeline().addLast(new StringCodec());
                    ch.pipeline().addLast(new Reciver());
                    ch.pipeline().addLast(new TerminalHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect("localhost", 8080).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }


    }
}
