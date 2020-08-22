/*
 * 天虹商场股份有限公司版权.
 */
package org.example.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author 严文泽
 * @version 1.0.0
 * @date 2020/8/18
 */
public class TerminalHandler extends SimpleChannelInboundHandler {

    private BufferedReader reader;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        channelHandlerContext.fireChannelRead(o);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //标准输入
        reader = new BufferedReader(new InputStreamReader(System.in));

        //利用死循环，不断读取客户端在控制台上的输入内容
        for (; ; ) {
            ctx.writeAndFlush(reader.readLine() + System.lineSeparator());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        reader.close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close().addListener((var)->System.out.println("Client Closed"));
    }
}
