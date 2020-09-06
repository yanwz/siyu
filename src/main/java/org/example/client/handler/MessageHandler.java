package org.example.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.MessageProto;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message message) throws Exception {
        if (message.getType() == MessageProto.Message.TypeEnum.MSG) {
            log.info(message.getUserId() + ":" + message.getContent() + "<---[MSG]");
        } else if (message.getType() == MessageProto.Message.TypeEnum.ACK) {
            log.info(message.getId() + "<---[ACK]");
        } else {
            log.error("-----------------");
            ctx.fireChannelRead(message);
        }
    }
}
