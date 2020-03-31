package com.bilalekrem.healthcheck.netty.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpRequest
import org.apache.logging.log4j.LogManager

class HttpServerHandler (private val serverContext: HttpServerContext): SimpleChannelInboundHandler<Any>() {

    private val logger = LogManager.getLogger()

    override fun channelRead0(ctx: ChannelHandlerContext?, obj: Any?) {
        if (obj is FullHttpRequest) {

            val response = serverContext.responseAsNettyResponse(obj)

            ctx?.writeAndFlush(response);
            ctx?.close()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        logger.atError().withThrowable(cause).log("Exception caught in http server handler")
        ctx?.close();
    }
}