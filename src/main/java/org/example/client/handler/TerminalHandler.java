/*
 * 天虹商场股份有限公司版权.
 */
package org.example.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.example.client.OperateEnum;
import org.example.domain.MessageProto;

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
                            MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
                            builder.setType(MessageProto.Message.TypeEnum.MSG);
                            builder.setId(UUID.randomUUID().toString().replace("-",""));
                            builder.setUserId(aLine[1]);
                            builder.setContent(aLine[2]);
                            ctx.writeAndFlush(builder.build());
                            break;
                        case QUIT:
                            ctx.close();
                            return;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                log.error("",e);
                ctx.close();
            } finally {
                scanner.close();
            }
        }).start();

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

}
