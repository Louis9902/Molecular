/*
 * This file ("PacketContentEncoder.java") is part of the molecular-project by Louis.
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

package org.molecular.common.network.codecs.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;
import org.molecular.api.Molecular;
import org.molecular.api.network.NetworkChannel;
import org.molecular.api.network.NetworkProtocol;
import org.molecular.api.network.buffer.PacketBuffers;
import org.molecular.api.network.packet.Packet;
import org.molecular.api.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Optional;

/**
 * @author Louis
 */

public class PacketContentEncoder extends MessageToByteEncoder<Packet<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger("org.molecular.codec");
    private static final Marker MARKER = MarkerFactory.getMarker("PACKET_SEND");

    private final Platform type;

    public PacketContentEncoder(Platform type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet<?> message, ByteBuf out) throws Exception {
        NetworkProtocol protocol = ctx.channel().attr(NetworkChannel.PROTOCOL).get();

        if (Molecular.PROTOCOL_REGISTRY.containsValue(protocol)) {
            Optional<Integer> index = protocol.get(this.type, message.getClass());

            if (index.isPresent()) {
                message.writePacket(PacketBuffers.wrappedBuffer(out).writeVarInt(index.get()));
            } else {
                LOGGER.error("Try to send unregistered packet {} [protocol:{}]", message.getClass().getSimpleName(), protocol.identifier());
                throw new EncoderException("Try to send data with unregistered packet");
            }

            LOGGER.debug(MARKER, "[{} | {}] > {}", protocol.identifier(), index.orElse(-1), message.getClass().getSimpleName());

        } else {
            LOGGER.error("Try to send data with unregistered protocol {}", protocol);
            throw new EncoderException("Try to send data with unregistered protocol");
        }
    }
}
