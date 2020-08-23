/*
 * 天虹商场股份有限公司版权.
 */
package org.example.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.example.UserMessage;
import org.example.client.OperateEnum;

import java.util.Scanner;
import java.util.UUID;

/**
 * @author 严文泽
 * @version 1.0.0
 * @date 2020/8/18
 */
@Slf4j
public class TerminalHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //标准输入
        Scanner scanner = new Scanner(System.in);
        //利用死循环，不断读取客户端在控制台上的输入内容
        new Thread(() -> {
            try {
                for (; ;) {
                    String var = scanner.nextLine();
                    if ("".equals(var.trim())) {
                        continue;
                    }
                    String[] aLine = var.split("\\s+");
                    OperateEnum operate;
                    try {
                        operate = OperateEnum.valueOf(aLine[0].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("找不到命令：" + aLine[0]);
                        continue;
                    }
                    switch (operate) {
                        case SEND:
                            if (aLine.length > 3) {
                                System.out.println("无效的命令语句：" + var);
                                break;
                            }
                            UserMessage userMessage = new UserMessage();
                            userMessage.setId(UUID.randomUUID().toString().replace("-",""));
                            userMessage.setUserId(aLine[1]);
                            userMessage.setContent(aLine[2]);
                            ctx.writeAndFlush(userMessage.toString());
                            break;
                        case QUIT:
                            ctx.close();
                            return;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                exceptionCaught(ctx, e);
            } finally {
                scanner.close();
            }
        }).start();

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}
