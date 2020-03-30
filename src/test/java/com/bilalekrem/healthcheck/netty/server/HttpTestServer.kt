package com.bilalekrem.healthcheck.netty.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec

import org.apache.logging.log4j.LogManager

import java.util.logging.Level
import java.util.logging.Logger

class HttpTestServer(private val port: Int) {

    private val logger = LogManager.getLogger()

    private val serverContext: HttpServerContext = HttpServerContext()

    private val bootstrap: ServerBootstrap = ServerBootstrap()
    private lateinit var bootstrapChannel: Channel

    private val bossGroup = NioEventLoopGroup(1)
    private val workerGroup = NioEventLoopGroup(10)

    init {
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(object: ChannelInitializer<SocketChannel>() {
                    override fun initChannel(socketChannel: SocketChannel?) {
                        socketChannel?.pipeline()?.apply {
                            addLast(HttpServerCodec())
                            addLast(HttpObjectAggregator(1048576));
                            addLast(HttpServerHandler(serverContext))
                        }
                    }
                })
    }

    fun start() {
        bootstrapChannel = bootstrap
                .bind(port)
                .sync()
                .channel()

        logger.info("HttpServer is started -- port: {}", port)
    }

    fun stop() {
        logger.info("HttpServer is shutting down")

        bootstrapChannel.close().sync()

        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

    fun map(method: HttpMethod, endpoint: String, response: HttpServerContext.MockResponse)
            = serverContext.map(method, endpoint, response)

}