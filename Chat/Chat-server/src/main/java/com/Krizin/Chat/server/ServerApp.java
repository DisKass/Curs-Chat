package com.Krizin.Chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerApp {
    private static final int PORT = 17982;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // пул потоков для подключения пользователей
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // пул потоков для работы с пользователями

        try {
            ServerBootstrap b = new ServerBootstrap();

            // Настройка сервера
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // SocketChannel - вся инфа о подключении
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new MainHandler());
                        }
                    });
            // Запуск сервера
            ChannelFuture future = b.bind(PORT).sync(); // Установка порта и запуск
            System.out.println("Server is running...");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully(); // Закрытие пулов потоков
            workerGroup.shutdownGracefully();
        }

    }
}
