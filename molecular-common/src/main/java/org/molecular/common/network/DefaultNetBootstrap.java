/*
 * This file ("DefaultNetBootstrap.java") is part of the molecular-project by Louis.
 * Copyright © 2017 Louis
 *
 * The molecular-project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The molecular-project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with molecular-project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.molecular.common.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.molecular.api.MolecularMaterial;
import org.molecular.api.MolecularSettings;
import org.molecular.api.network.ConstNetwork;
import org.molecular.api.network.NetworkBootstrap;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.platform.Platform;
import org.molecular.api.platform.PlatformApplication;
import org.molecular.common.internal.DaemonThreadFactory;
import org.molecular.common.network.codecs.decoder.PacketContentDecoder;
import org.molecular.common.network.codecs.decoder.PacketFrameDecoder;
import org.molecular.common.network.codecs.encoder.PacketContentEncoder;
import org.molecular.common.network.codecs.encoder.PacketFrameEncoder;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Louis
 */
public class DefaultNetBootstrap implements NetworkBootstrap {

    private final Logger logger;
    private final PlatformApplication application;

    private final ChannelGroup serverChannels;
    private final ChannelGroup clientChannels;

    private final Class<? extends Channel> clientChannel;
    private final Class<? extends ServerChannel> serverChannel;
    private final EventLoopGroup group;

    public DefaultNetBootstrap(PlatformApplication application) {
        this.logger = application.getLogger();
        this.application = application;

        this.serverChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        this.clientChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        ThreadFactory factory = new DaemonThreadFactory(DaemonThreadFactory.NETTY_IO);
        if (Epoll.isAvailable() && MolecularSettings.native_transport) {
            this.clientChannel = EpollSocketChannel.class;
            this.serverChannel = EpollServerSocketChannel.class;
            this.group = new EpollEventLoopGroup(0, factory);
            logger.info("Molecular network is using <EPOLL> channel implementation");
        } else {
            this.clientChannel = NioSocketChannel.class;
            this.serverChannel = NioServerSocketChannel.class;
            this.group = new NioEventLoopGroup(0, factory);
            logger.info("Molecular network is using <NIO> channel implementation");
        }
    }

    @Override
    public Optional<NetworkChannel> connect(SocketAddress address) throws IOException {
        Bootstrap bootstrap = new Bootstrap().group(this.group).channel(this.clientChannel);

        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        bootstrap.attr(NetworkChannel.PROTOCOL, MolecularMaterial.PROTOCOL_HANDSHAKE);

        DefaultNetChannel defChannel = new DefaultNetChannel(Platform.CLIENT);

        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                pipeline.addLast(ConstNetwork.TIMEOUT, new ReadTimeoutHandler(MolecularSettings.connection_timeout));

                pipeline.addLast(ConstNetwork.SPLITTER, new PacketFrameDecoder());
                pipeline.addLast(ConstNetwork.DECODER, new PacketContentDecoder(Platform.CLIENT));

                pipeline.addLast(ConstNetwork.PREPENDER, new PacketFrameEncoder());
                pipeline.addLast(ConstNetwork.ENCODER, new PacketContentEncoder(Platform.SERVER));

                pipeline.addLast(ConstNetwork.HANDLER, defChannel);

                DefaultNetBootstrap.this.clientChannels.add(channel);
            }
        });

        ChannelFuture connect = bootstrap.remoteAddress(address).connect();
        connect.awaitUninterruptibly(); /*BLOCKING*/

        if (connect.isCancelled()) {
            logger.warn("Connection attempt cancelled by account, tried to connect to {}", address);
            return Optional.empty();
        }

        if (!connect.isSuccess()) {
            logger.error("Connection attempt failed, cause unknown", connect.cause());
            return Optional.empty();
        }

        return Optional.of(defChannel);
    }

    @Override
    public Optional<Channel> bind(SocketAddress address) throws IOException {
        ServerBootstrap bootstrap = new ServerBootstrap().group(this.group).channel(this.serverChannel);

        bootstrap.option(ChannelOption.SO_BACKLOG, 200);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);

        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);

        bootstrap.childAttr(NetworkChannel.PROTOCOL, MolecularMaterial.PROTOCOL_HANDSHAKE);

        bootstrap.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();

                pipeline.addLast(ConstNetwork.TIMEOUT, new ReadTimeoutHandler(MolecularSettings.connection_timeout));

                pipeline.addLast(ConstNetwork.SPLITTER, new PacketFrameDecoder());
                pipeline.addLast(ConstNetwork.DECODER, new PacketContentDecoder(Platform.SERVER));

                pipeline.addLast(ConstNetwork.PREPENDER, new PacketFrameEncoder());
                pipeline.addLast(ConstNetwork.ENCODER, new PacketContentEncoder(Platform.CLIENT));

                pipeline.addLast(ConstNetwork.HANDLER, new DefaultNetChannel(Platform.SERVER));

                DefaultNetBootstrap.this.clientChannels.add(channel);
            }
        });

        ChannelFuture bind = bootstrap.localAddress(address).bind();
        bind.awaitUninterruptibly(); /*BLOCKING*/

        if (bind.isCancelled()) {
            logger.warn("Binding attempt cancelled by account, tried to bind to {}", address);
            return Optional.empty();
        }

        if (!bind.isSuccess()) {
            logger.error("Binding attempt failed, cause unknown", bind.cause());
            return Optional.empty();
        }

        Channel channel = bind.channel();

        this.serverChannels.add(channel);
        return Optional.of(channel);
    }

    @Override
    public ChannelGroup channelServer() {
        return this.serverChannels;
    }

    @Override
    public ChannelGroup channelClient() {
        return this.clientChannels;
    }

    @Override
    public void close() throws IOException {
        this.serverChannels.close().awaitUninterruptibly(5, TimeUnit.SECONDS);
        this.clientChannels.close().awaitUninterruptibly(5, TimeUnit.SECONDS);
    }
}
