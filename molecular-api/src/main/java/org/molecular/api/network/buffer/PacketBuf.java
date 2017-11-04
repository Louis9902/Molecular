/*
 * This file ("PacketBuf.java") is part of the molecular-project by Louis.
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

package org.molecular.api.network.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.molecular.api.binary.parcel.BaseDataParcel;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author Louis
 */

public abstract class PacketBuf extends ByteBuf {

    public static int calcVarByteSize(int value) {
        int count = 0;
        do {
            value >>>= 7;
            count++;
        } while (value != 0);
        return count;
    }

    public static int calcVarByteSize(long value) {
        int count = 0;
        do {
            value >>>= 7;
            count++;
        } while (value != 0);
        return count;
    }

    public abstract PacketBuf writeString(@Nonnull String string) throws EncoderException;

    public abstract String readString() throws DecoderException;

    public abstract String readString(@Nonnegative int maxLength) throws DecoderException;

    public abstract PacketBuf writeVarInt(int value);

    public abstract int readVarInt() throws DecoderException;

    public abstract PacketBuf writeVarLong(long value);

    public abstract long readVarLong() throws DecoderException;

    public abstract PacketBuf writeVarIntArray(int[] ints);

    public abstract int[] readVarIntArray();

    public abstract PacketBuf writeVarLongArray(long[] longs);

    public abstract long[] readVarLongArray();

    public abstract PacketBuf writeByteArray(byte[] bytes);

    public final byte[] readByteArray() {
        return this.readByteArray(this.readableBytes());
    }

    public abstract byte[] readByteArray(@Nonnegative int size) throws DecoderException;

    public abstract PacketBuf writeShortArray(short[] shorts);

    public final short[] readShortArray() {
        return this.readShortArray(this.readableBytes() / 2);
    }

    public abstract short[] readShortArray(@Nonnegative int size) throws DecoderException;

    public abstract PacketBuf writeIntArray(int[] ints);

    public final int[] readIntArray() {
        return this.readIntArray(this.readableBytes() / 4);
    }

    public abstract int[] readIntArray(@Nonnegative int size) throws DecoderException;

    public abstract PacketBuf writeFloatArray(float[] floats);

    public final float[] readFloatArray() {
        return this.readFloatArray(this.readableBytes() / 4);
    }

    public abstract float[] readFloatArray(@Nonnegative int size) throws DecoderException;

    public abstract PacketBuf writeLongArray(long[] longs);

    public final long[] readLongArray() {
        return this.readLongArray(this.readableBytes() / 8);
    }

    public abstract long[] readLongArray(@Nonnegative int size) throws DecoderException;

    public abstract PacketBuf writeDoubleArray(double[] doubles);

    public final double[] readDoubleArray() {
        return this.readDoubleArray(this.readableBytes() / 8);
    }

    public abstract double[] readDoubleArray(@Nonnegative int size) throws DecoderException;

    public abstract PacketBuf writeEnum(@Nonnull Enum<?> value);

    public abstract <T extends Enum<T>> T readEnum(@Nonnull Class<T> clazz);

    public abstract PacketBuf writeUUID(@Nonnull UUID uuid);

    public abstract UUID readUUID();

    public abstract PacketBuf writeImage(@Nonnull BufferedImage image, @Nonnull String format) throws EncoderException;

    public abstract BufferedImage readImage() throws DecoderException;

    public abstract PacketBuf writeBinaryData(@Nonnull BaseDataParcel<?> dataPart) throws EncoderException;

    public abstract BaseDataParcel<?> readBinaryData() throws DecoderException;

    @Override
    public abstract PacketBuf markReaderIndex();

    @Override
    public abstract PacketBuf resetReaderIndex();

    @Override
    public abstract PacketBuf markWriterIndex();

    @Override
    public abstract PacketBuf resetWriterIndex();

    @Override
    public abstract PacketBuf discardReadBytes();

    @Override
    public abstract PacketBuf discardSomeReadBytes();

    @Override
    public abstract PacketBuf getBytes(int index, ByteBuf dst);

    @Override
    public abstract PacketBuf getBytes(int index, ByteBuf dst, int length);

    @Override
    public abstract PacketBuf getBytes(int index, ByteBuf dst, int dstIndex, int length);

    @Override
    public abstract PacketBuf getBytes(int index, byte[] dst);

    @Override
    public abstract PacketBuf getBytes(int index, byte[] dst, int dstIndex, int length);

    @Override
    public abstract PacketBuf getBytes(int index, ByteBuffer dst);

    @Override
    public abstract PacketBuf getBytes(int index, OutputStream out, int length) throws IOException;

    @Override
    public abstract PacketBuf retain(int increment);

    @Override
    public abstract PacketBuf retain();
}
