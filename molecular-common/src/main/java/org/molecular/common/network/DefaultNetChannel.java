/*
 * This file ("DefaultNetChannel.java") is part of the molecular-project by Louis.
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

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.ReferenceCountUtil;
import org.molecular.api.accounts.Account;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.NetworkProtocol;
import org.molecular.api.network.handler.NetworkHandler;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.network.util.ChannelNotifier;
import org.molecular.api.platform.Platform;
import org.molecular.api.resources.text.BaseTextPart;
import org.molecular.api.resources.text.TextPartTranslation;
import org.molecular.common.MolecularHandler;
import org.molecular.common.crypt.CipherManager;
import org.molecular.common.event.MolecularEventFactory;
import org.molecular.common.network.codecs.decoder.PacketCompressionDecoder;
import org.molecular.common.network.codecs.decoder.PacketEncryptingDecoder;
import org.molecular.common.network.codecs.encoder.PacketCompressionEncoder;
import org.molecular.common.network.codecs.encoder.PacketEncryptingEncoder;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.molecular.api.network.ConstNetwork.*;

/**
 * @author Louis
 */

public class DefaultNetChannel extends ChannelInboundHandlerAdapter implements NetworkChannel {

    private final Platform platform;
    private final UUID identifier;

    private Optional<BaseTextPart> disconnectReason = Optional.empty();
    private Optional<Channel> channel = Optional.empty();
    private NetworkHandler handler;

    public DefaultNetChannel(Platform platform) {
        this.platform = platform;
        this.identifier = UUID.randomUUID();
    }

    @Nonnull
    @Override
    public UUID identifier() {
        return this.identifier;
    }

    //<editor-fold desc="ChannelInboundHandler Overrides">

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MolecularEventFactory.instance().callChannelActiveEvent(this);
        this.channel = Optional.ofNullable(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MolecularEventFactory.instance().callChannelInactiveEvent(this);
        this.close(new TextPartTranslation("disconnect.endOfStream"));
        super.channelInactive(ctx);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = true;
        try {
            if (Packet.class.isAssignableFrom(msg.getClass())) {
                Packet.class.cast(msg).notifyHandler(this.handler, this);
            } else {
                release = false;
                super.channelRead(ctx, msg);
            }
        } finally {
            if (release) {
                ReferenceCountUtil.release(msg);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().flush();
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof TimeoutException) {
            MolecularEventFactory.instance().callChannelTimeoutEvent(this);
            this.close(new TextPartTranslation("disconnect.timeout"));
            return;
        }

        String parameter = Optional.ofNullable(cause.getMessage()).orElse(cause.getClass().getSimpleName());
        this.close(new TextPartTranslation("disconnect.error", parameter));

        Logger logger = MolecularHandler.instance().getLogger();
        logger.warn("Caught {} at device channel {}", cause.getClass().getSimpleName(), this.channel.orElse(null));
        logger.debug("StackTrace of caught exception: " + '\n' + Throwables.getStackTraceAsString(cause));
    }
    //</editor-fold>

    @Override
    public Platform platform() {
        return this.platform;
    }

    @Override
    public Optional<Channel> channel() {
        return this.channel;
    }

    @Override
    public boolean isChannelOpen() {
        return this.channel().map(Channel::isOpen).orElse(false);
    }

    @Override
    public Optional<BaseTextPart> reason() {
        return this.disconnectReason;
    }

    @Override
    public Optional<Account> account() {
        return this.channel().map(c -> c.attr(ACCOUNT).get());
    }

    @Override
    public void disconnect(@Nonnull BaseTextPart reason) {
        if (this.platform == Platform.SERVER) {
            if (this.isChannelOpen()) {
                NetworkProtocol protocol = this.channel.get().attr(PROTOCOL).get();

                protocol.createDisconnectPacket(reason).ifPresent(packet -> {
                    ChannelNotifier<DefaultNetChannel> notifier = new ChannelNotifier<>(this, c -> c.close(reason));
                    this.writeAndFlushPacket(packet, notifier);
                });
                return;
            }
        }
        this.close(reason);
    }

    @Override
    public void close(@Nonnull BaseTextPart reason) {
        if (this.isChannelOpen()) {
            this.channel.get().close().awaitUninterruptibly();
            this.channel = Optional.empty();
        }
        if (!this.reason().isPresent()) {
            this.disconnectReason = Optional.of(reason);
            this.handler.propagateDisconnect(reason);
        }
    }

    @Override
    public void writeAndFlushPacket(@Nonnull Packet<?> packet, @Nonnull ChannelFutureListener... listener) {
        this.channel.ifPresent(c -> c.writeAndFlush(packet).addListeners(listener));
    }

    @Override
    public void writePacket(@Nonnull Packet<?> packet, @Nonnull ChannelFutureListener... listener) {
        this.channel().ifPresent(c -> c.write(packet).addListeners(listener));
    }

    @Override
    public void flush() {
        this.channel.ifPresent(Channel::flush);
    }

    @Override
    public void enableCompression(int threshold) {
        if (this.channel.isPresent()) {
            ChannelPipeline pipeline = this.channel.get().pipeline();

            ChannelHandler encoder = pipeline.get(COMPRESS);
            ChannelHandler decoder = pipeline.get(DECOMPRESS);

            if (decoder instanceof PacketCompressionDecoder) {
                ((PacketCompressionDecoder) decoder).setThreshold(threshold);
            } else {
                pipeline.addBefore(DECODER, DECOMPRESS, new PacketCompressionDecoder(threshold));
            }

            if (encoder instanceof PacketCompressionEncoder) {
                ((PacketCompressionEncoder) encoder).setThreshold(threshold);
            } else {
                pipeline.addBefore(ENCODER, COMPRESS, new PacketCompressionEncoder(threshold));
            }

        } else {
            throw new IllegalStateException("Cannot enable compression without channel");
        }
    }

    @Override
    public void enableEncryption(@Nonnull SecretKey secretKey) {
        if (this.channel.isPresent()) {
            ChannelPipeline pipeline = this.channel.get().pipeline();

            Cipher decryptCipher = CipherManager.createNetworkCipherInstance(Cipher.DECRYPT_MODE, secretKey);
            Cipher encryptCipher = CipherManager.createNetworkCipherInstance(Cipher.ENCRYPT_MODE, secretKey);

            pipeline.addBefore(SPLITTER, DECRYPT, new PacketEncryptingDecoder(decryptCipher));
            pipeline.addBefore(PREPENDER, ENCRYPT, new PacketEncryptingEncoder(encryptCipher));
        } else {
            throw new IllegalStateException("Cannot enable encryption without channel");
        }
    }

    @Override
    public void switchNetworkProtocol(@Nonnull NetworkProtocol protocol) {
        this.channel().ifPresent(c -> c.attr(PROTOCOL).set(checkNotNull(protocol, "network protocol")));
    }

    @Override
    public void switchNetworkHandler(@Nonnull NetworkHandler handler) {
        this.handler = checkNotNull(handler, "network handler");
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.platform, this.identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultNetChannel that = (DefaultNetChannel) o;
        return this.matches(that);
    }
}
