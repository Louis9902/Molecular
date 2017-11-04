/*
 * This file ("CipherTranslator.java") is part of the molecular-project by Louis.
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

package org.molecular.common.crypt;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

/**
 * @author Louis
 */

public final class CipherTranslator {

    @Nonnull
    private final Cipher cipher;

    private byte[] readBuffer = new byte[0];
    private byte[] writeBuffer = new byte[0];

    public CipherTranslator(@Nonnull Cipher cipher) {
        this.cipher = cipher;
    }

    public ByteBuf decipher(@Nonnull ChannelHandlerContext ctx, @Nonnull ByteBuf byteBuf) throws ShortBufferException {
        int readableBytes = byteBuf.readableBytes();
        byte[] bytes = this.bufferToByteArray(byteBuf);

        ByteBuf buffer = ctx.alloc().heapBuffer(this.cipher.getOutputSize(readableBytes));
        buffer.writerIndex(this.cipher.update(bytes, 0, readableBytes, buffer.array(), buffer.arrayOffset()));

        return buffer;
    }

    public void cipher(@Nonnull ByteBuf input, @Nonnull ByteBuf output) throws ShortBufferException {
        int readableBytes = input.readableBytes();
        byte[] bytes = this.bufferToByteArray(input);
        int outputSize = this.cipher.getOutputSize(readableBytes);

        if (this.writeBuffer.length < outputSize) {
            this.writeBuffer = new byte[outputSize];
        }

        int updateSize = this.cipher.update(bytes, 0, readableBytes, this.writeBuffer);
        output.writeBytes(this.writeBuffer, 0, updateSize);
    }

    private byte[] bufferToByteArray(@Nonnull ByteBuf buffer) {
        int readableBytes = buffer.readableBytes();

        if (this.readBuffer.length < readableBytes) {
            this.readBuffer = new byte[readableBytes];
        }

        buffer.readBytes(this.readBuffer, 0, readableBytes);
        return this.readBuffer;
    }
}
