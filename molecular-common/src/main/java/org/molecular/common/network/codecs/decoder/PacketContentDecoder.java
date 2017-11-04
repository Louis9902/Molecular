/*
 * This file ("PacketContentDecoder.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.codecs.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.molecular.api.Molecular;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.NetworkProtocol;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBuffers;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

/**
 * @author Louis
 */

public class PacketContentDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger("org.molecular.codec");
    private static final Marker MARKER = MarkerFactory.getMarker("PACKET_RECEIVED");

    private final Platform type;

    public PacketContentDecoder(Platform type) {
        this.type = type;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        NetworkProtocol protocol = ctx.channel().attr(NetworkChannel.PROTOCOL).get();

        if (Molecular.PROTOCOL_REGISTRY.containsValue(protocol)) {
            if (in.readableBytes() != 0) {
                PacketBuf buffer = PacketBuffers.wrappedBuffer(in);
                int index = buffer.readVarInt();

                Optional<Class<? extends Packet>> clazz = protocol.get(this.type, index);
                if (clazz.isPresent()) {
                    Packet instance;

                    try {
                        Constructor<? extends Packet> constructor = clazz.get().getDeclaredConstructor(/*empty*/);
                        constructor.setAccessible(true);
                        instance = constructor.newInstance(/*empty*/);
                    } catch (Throwable throwable) {
                        LOGGER.error("Packet {}:{} cannot be instantiated by packet decoder", protocol.identifier(), index);
                        buffer.release();
                        throw new DecoderException("Packet could not be instantiated", throwable);
                    }

                    instance.readPacket(buffer);
                    if (buffer.readableBytes() <= 0) {
                        out.add(instance);
                        LOGGER.debug(MARKER, "[{} | {}] > {}", protocol.identifier(), index, instance.getClass().getSimpleName());
                    } else {
                        LOGGER.error("Packet {}:{} was larger than expected, found {} bytes extra whilst reading packet", protocol.identifier(), index, buffer.readableBytes());
                        buffer.release();
                        throw new DecoderException("Packet was larger than expected");
                    }

                } else {
                    LOGGER.debug("Received unregistered packet with index {}", index);
                }

            } else {
                LOGGER.debug("Received packet with no buffer content at channel {}", ctx.channel());
            }
        } else {
            LOGGER.error("Try to receive data with unregistered protocol {}", protocol);
            throw new EncoderException("Try to receive data with unregistered protocol");
        }
    }
}
