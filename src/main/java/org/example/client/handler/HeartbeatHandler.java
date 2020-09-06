package org.example.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.MessageProto;

@Slf4j
public class HeartbeatHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    private static final MessageProto.Message PING_MESSAGE ;

    private static final MessageProto.Message PONG_MESSAGE ;

    static {
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setType(MessageProto.Message.TypeEnum.PING);
        PING_MESSAGE = builder.build();
        builder.setType(MessageProto.Message.TypeEnum.PONG);
        PONG_MESSAGE = builder.build();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProto.Message o) throws Exception {
        if(o.getType() == MessageProto.Message.TypeEnum.PING){
            channelHandlerContext.writeAndFlush(PONG_MESSAGE);
        }else if(o.getType() == MessageProto.Message.TypeEnum.PONG){
            log.info("PONG<-------------");
        }else {
            channelHandlerContext.fireChannelRead(o);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent cast = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE == cast.state()) {
                log.info("read time out,close channel");
                ctx.close();
            }else if (IdleState.WRITER_IDLE == cast.state()) {
                log.info("------------->PING");
                ctx.writeAndFlush(PING_MESSAGE);
            }
        }
    }
}
