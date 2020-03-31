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
import java.net.InetSocketAddress

import java.util.logging.Level
import java.util.logging.Logger

class HttpTestServer() {

    private val logger = LogManager.getLogger()

    private val serverContext: HttpServerContext = HttpServerContext()

    private val bootstrap: ServerBootstrap = ServerBootstrap()
    private var bootstrapChannel: Channel? = null

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

    fun start(port: Int) {
        bootstrapChannel?:let {
            bootstrapChannel = bootstrap
                    .bind(port)
                    .sync()
                    .channel()

            logger.info("HttpServer is started -- port: {}", port)
        } ?: logger.warn("Server is already running on port [{}]", (bootstrapChannel?.localAddress() as InetSocketAddress).port)
    }

    fun stop() {
        bootstrapChannel?.let {
            logger.info("HttpServer is shutting down")
            if (it.isOpen) {
                it.close().sync()
            }
        } ?: logger.info("HttpServer is not running")
    }

    fun close() {
        logger.info("HttpServer is closing")

        stop()

        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }

    fun map(method: HttpMethod, endpoint: String, response: HttpServerContext.MockResponse)
            = serverContext.map(method, endpoint, response)

}