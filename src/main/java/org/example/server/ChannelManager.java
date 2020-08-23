package org.example.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class ChannelManager extends ChannelInboundHandlerAdapter {

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

    public Map<String, Channel> getChannels() {
        return channels;
    }
}
