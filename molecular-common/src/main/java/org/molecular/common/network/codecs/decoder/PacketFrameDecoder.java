/*
 * This file ("PacketFrameDecoder.java") is part of the molecular-project by Louis.
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
import io.netty.handler.codec.CorruptedFrameException;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBuffers;

import java.util.List;

/**
 * @author Louis
 */

public class PacketFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
         /*mark reader index*/
        in.markReaderIndex();
        byte[] bytes = new byte[3];

        for (int i = 0; i < bytes.length; ++i) {

            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            bytes[i] = in.readByte();

            if (bytes[i] >= 0) {
                PacketBuf buffer = PacketBuffers.wrappedBuffer(bytes);

                try {
                    int contentLength = buffer.readVarInt();

                    /*we have a whole packet with all its content*/
                    if (in.readableBytes() >= contentLength) {
                        out.add(in.readBytes(contentLength));
                        return;
                    }

                    /*packet is incomplete, reset reader index and continue*/
                    in.resetReaderIndex();
                } finally {
                    /*release the 21-Bit buffer*/
                    buffer.release();
                }
                return;
            }

        }
        throw new CorruptedFrameException("PacketBuf contains more than 2097151 bytes, 21-bit int out of bounds");
    }

}
