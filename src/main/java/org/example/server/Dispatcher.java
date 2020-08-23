package org.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.UserMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class Dispatcher extends SimpleChannelInboundHandler<String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    private Map<String, Channel> channels = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.put(channel.id().toString(),channel);
        log.info(channel.id().toString()+" online");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.remove(channel.id().toString(),channel);
        log.info(channel.id().toString()+" offline");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        if("pong".equals(message)){
            channelHandlerContext.writeAndFlush("pong");
            return;
        }

        UserMessage userMessage = mapper.readValue(message, UserMessage.class);
        //持久化
        channelHandlerContext.writeAndFlush("消息["+userMessage.getId()+"]发送成功");


        String userId = userMessage.getUserId();
        Channel channel = channels.get(userId);
        if(channel != null){
            String fromId = channelHandlerContext.channel().id().toString();
            userMessage.setUserId(fromId);
            channel.writeAndFlush(userMessage.toString());
            log.info("dispatch a message,from id:{},target id:{},content:{}",fromId,userId,userMessage.getContent());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
