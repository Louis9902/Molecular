/*
 * This file ("PacketCompressionDecoder.java") is part of the molecular-project by Louis.
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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBuffers;

import java.util.List;
import java.util.zip.Inflater;

/**
 * @author Louis
 */

public class PacketCompressionDecoder extends ByteToMessageDecoder {

    private final Inflater inflater;
    private int threshold;

    public PacketCompressionDecoder(int threshold) {
        this.threshold = threshold;
        this.inflater = new Inflater();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            PacketBuf buffer = PacketBuffers.wrappedBuffer(in);

            /*read the size of the compressed byte array*/
            int size = buffer.readVarInt();

            if (size == 0) {
                /*no compression*/
                out.add(buffer.readBytes(buffer.readableBytes()));
            } else {
                if (size < this.threshold) {
                    throw new DecoderException("Invalid compressed packet - size of " + size + " is smaller than compression threshold of " + this.threshold);
                }

                byte[] compressed = new byte[buffer.readableBytes()];
                /*cuts the compressed byte array out of the buffer*/
                buffer.readBytes(compressed);
                this.inflater.setInput(compressed);

                byte[] uncompressed = new byte[size];
                this.inflater.inflate(uncompressed);
                out.add(Unpooled.wrappedBuffer(uncompressed));

                this.inflater.reset();
            }
        }
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
