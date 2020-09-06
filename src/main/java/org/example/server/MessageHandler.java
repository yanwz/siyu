package org.example.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.MessageProto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    private Map<String, Channel> channels = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.put(channel.id().toString(), channel);
        log.info(channel.id().toString() + " online");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.remove(channel.id().toString(), channel);
        log.info(channel.id().toString() + " offline");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProto.Message message) throws Exception {
        if(message.getType() == MessageProto.Message.TypeEnum.MSG){
            MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
            builder.setType(MessageProto.Message.TypeEnum.ACK);
            channelHandlerContext.writeAndFlush(builder.setId(message.getId()));
            String userId = message.getUserId();
            Channel channel = channels.get(userId);
            if (channel != null) {
                String fromId = channelHandlerContext.channel().id().toString();
                builder.clear();
                builder.setId(message.getId());
                builder.setType(MessageProto.Message.TypeEnum.MSG);
                builder.setContent(message.getContent());
                builder.setUserId(fromId);
                channel.writeAndFlush(message);
                log.info("dispatch a message,from id:{},target id:{},content:{}", fromId, userId,message.getContent());
            }
        }else {
            channelHandlerContext.fireChannelRead(message);
        }

    }

}
