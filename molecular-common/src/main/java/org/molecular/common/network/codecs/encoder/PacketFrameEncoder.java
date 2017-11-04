/*
 * This file ("PacketFrameEncoder.java") is part of the molecular-project by Louis.
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
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBuffers;
import org.molecular.common.network.codecs.ByteToByteEncoder;

/**
 * @author Louis
 */

public class PacketFrameEncoder extends ByteToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int readableBytes = msg.readableBytes();
        int calcVarIntSize = PacketBuf.calcVarByteSize(readableBytes);

        if (calcVarIntSize <= 3) {
            PacketBuf buffer = PacketBuffers.wrappedBuffer(out);
            /*make sure there is some space for the bytes*/
            buffer.ensureWritable(calcVarIntSize + readableBytes);
            buffer.writeVarInt(readableBytes);
            buffer.writeBytes(msg, msg.readerIndex(), readableBytes);
        } else {
            throw new EncoderException("ByteBuf contains more than 2097151 bytes,21-bit int out of bounds");
        }
    }

}
