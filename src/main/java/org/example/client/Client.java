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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.client.handler.ExceptionHandler;
import org.example.client.handler.HeartbeatHandler;
import org.example.client.handler.MessageHandler;
import org.example.client.handler.TerminalHandler;
import org.example.domain.MessageProto;

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
                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    ch.pipeline().addLast(new ProtobufEncoder());
                    ch.pipeline().addLast(new HeartbeatHandler());
                    ch.pipeline().addLast(new MessageHandler());
                    ch.pipeline().addLast(new TerminalHandler());
                    ch.pipeline().addLast(new ExceptionHandler());
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
