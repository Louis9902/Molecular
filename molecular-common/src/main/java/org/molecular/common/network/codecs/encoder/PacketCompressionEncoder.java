/*
 * This file ("PacketCompressionEncoder.java") is part of the molecular-project by Louis.
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
import org.molecular.api.network.buffer.PacketBuf;
import org.molecular.api.network.buffer.PacketBuffers;
import org.molecular.common.network.codecs.ByteToByteEncoder;

import java.util.zip.Deflater;

/**
 * @author Louis
 */

public class PacketCompressionEncoder extends ByteToByteEncoder {

    private final byte[] buffer;
    private final Deflater deflater;

    private int threshold;

    public PacketCompressionEncoder(int threshold) {
        this.threshold = threshold;
        this.deflater = new Deflater();
        this.buffer = new byte[1024 * 8];
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        int size = msg.readableBytes();
        PacketBuf buffer = PacketBuffers.wrappedBuffer(out);

        if (size < this.threshold) {
            buffer.writeVarInt(0);
            buffer.writeBytes(msg);
        } else {
            /*cuts the uncompressed byte array out of the buffer to compress it*/
            byte[] uncompressed = new byte[size];
            msg.readBytes(uncompressed);

            /*write uncompressed size to the buffer*/
            buffer.writeVarInt(uncompressed.length);

            /*compresses the byte array content, from start to end (0,size)*/
            this.deflater.setInput(uncompressed, 0, size);
            this.deflater.finish();

            /*while no finished continue to loop, aka there is some data in the deflater*/
            while (!this.deflater.finished()) {
                /*read the new length of the compressed byte array data, and write the data to the byte array*/
                int length = this.deflater.deflate(this.buffer);
                /*write the new byte array content to the buffer since we cut the old data out*/
                buffer.writeBytes(this.buffer, 0, length);
            }

            this.deflater.reset();
        }
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
