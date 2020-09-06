package org.example.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.example.client.handler.ExceptionHandler;
import org.example.client.handler.HeartbeatHandler;
import org.example.domain.MessageProto;

public class Server {

    public static void main(String[] args) {
        EventLoopGroup mainGroup = new NioEventLoopGroup(1); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(600,0,0));
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new HeartbeatHandler());
                            ch.pipeline().addLast(new MessageHandler());
                            ch.pipeline().addLast(new ExceptionHandler());
                        }
                    });         // (5)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(8080).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            mainGroup.shutdownGracefully();
        }
    }

}
