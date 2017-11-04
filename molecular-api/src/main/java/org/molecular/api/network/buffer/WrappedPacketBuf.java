/*
 * This file ("WrappedPacketBuf.java") is part of the molecular-project by Louis.
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
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;
import org.molecular.api.binary.io.DataParcelReader;
import org.molecular.api.binary.io.DataParcelWriter;
import org.molecular.api.binary.parcel.BaseDataParcel;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @author Louis
 */

public class WrappedPacketBuf extends PacketBuf {

    @Nonnull
    private final ByteBuf buffer;

    public WrappedPacketBuf(@Nonnull ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public PacketBuf writeString(@Nonnull String string) throws EncoderException {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        if (PacketBuf.calcVarByteSize(bytes.length) < 3) {
            this.writeVarInt(bytes.length);
            this.writeBytes(bytes);
        } else {
            throw new EncoderException("String is too big ( " + string.length() + " bytes encoded, max value is 16383 (biggest 14-bit value))");
        }
        return this;
    }

    @Override
    public String readString() throws DecoderException {
        int size = this.readVarInt();

        if (size >= 0) {
            byte[] bytes = new byte[size];
            this.buffer.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } else {
            throw new DecoderException("The received encoded string wrappedBuffer length is less than zero");
        }
    }

    @Override
    public String readString(int maxLength) throws DecoderException {
        int size = this.readVarInt();

        if (size >= 0) {
            if (size <= maxLength) {
                byte[] bytes = new byte[size];
                this.buffer.readBytes(bytes);
                return new String(bytes, StandardCharsets.UTF_8);
            } else {
                throw new DecoderException("The received string length is longer than maximum allowed (" + size + " > " + maxLength + ")");
            }
        } else {
            throw new DecoderException("The received encoded string wrappedBuffer length is less than zero");
        }
    }

    @Override
    public PacketBuf writeVarInt(int value) {
        byte part;
        do {
            /*reads 7 data-bits into part [0111 1111](127)*/
            part = (byte) (value & 127);

            /*zero fill right shift -> shifts the left bit to pos 1 of the byte byte*/
            /*is like a read with no return*/
            /*[1010 1010](170) >>>7  ->  [0000 0001](1)*/
            value >>>= 7;

            /*should it be zero no don't write continue 1 at 128-Bit place*/
            if (value != 0) {
                /*if value is bigger than 128 (because of last isKeyValid)*/
                /*take bits both binary bytes and combine them*/
                /*so the value [1001 1101](157) and [1000 0000](128) -> 1001 1101(157)*/
                part |= 128;
            }
            this.writeByte(part);
        } while (value != 0);
        /*works its way through the int because a byte is only 8 bits, but a int is 32 bits long*/
        return this;
    }

    @Override
    public int readVarInt() throws DecoderException {
        int result = 0, count = 0;
        byte part;
        do {
            part = this.readByte();
            /*result = bit combination of part, which has only bits similar to 127 and is shifted to the left by 7 (bits * iteration)*/
            /*0000 0000 |= ((0101 0010 & 0111 1111)[=0101 0010{82}] << (0*7))  --> [0101 0010{82}]*/
            result |= ((part & 127) << (count++ * 7));
            if (count > 5) {
                /*if the int is to big we write actual 5 bytes because we can only read 7 bits of the byte*/
                /*the 8th is the byte to determine if it goes on or not*/
                /*so but if we have now more than the max of 5 is must be a long or sth. similar*/
                throw new DecoderException("VarInt is too long (" + count + " > 5)");
            }
            /*as long as the part byte is 128 continue to loop, means the byte is "full" can may be continued on the next byte*/
        } while ((part & 128) == 128);
        return result;
    }

    @Override
    public PacketBuf writeVarLong(long value) {
        byte part;
        do {
            part = (byte) (value & 127L);
            value >>>= 7;
            if (value != 0L) {
                part |= 128;
            }
            this.writeByte(part);
        } while (value != 0L);
        return this;
    }

    @Override
    public long readVarLong() throws DecoderException {
        long out = 0;
        int bytes = 0;
        byte part;
        do {
            part = this.readByte();
            out |= (part & 127) << (bytes++ * 7);
            if (bytes > 10) {
                throw new DecoderException("VarLong is too long (" + bytes + " > 10)");
            }
        } while ((part & 128) == 128);
        return out;
    }

    @Override
    public PacketBuf writeVarIntArray(int[] ints) {
        this.writeVarInt(ints.length);
        for (int i : ints) {
            this.writeVarInt(i);
        }
        return this;
    }

    @Override
    public int[] readVarIntArray() {
        int length = this.readVarInt();
        int[] ints = new int[length];
        for (int i = 0; i < length; i++) {
            ints[i] = this.readVarInt();
        }
        return ints;
    }

    @Override
    public PacketBuf writeVarLongArray(long[] longs) {
        this.writeVarInt(longs.length);
        for (long i : longs) {
            this.writeVarLong(i);
        }
        return this;
    }

    @Override
    public long[] readVarLongArray() {
        int length = this.readVarInt();
        long[] longs = new long[length];
        for (int i = 0; i < length; i++) {
            longs[i] = this.readVarLong();
        }
        return longs;
    }

    @Override
    public PacketBuf writeByteArray(byte[] bytes) {
        this.writeVarInt(bytes.length);
        this.writeBytes(bytes);
        return this;
    }

    @Override
    public byte[] readByteArray(int size) throws DecoderException {
        int length = this.readVarInt();
        if (length > size) {
            throw new DecoderException("ByteArray with size " + length + " is bigger than allowed " + size);
        } else {
            byte[] bytes = new byte[length];
            this.readBytes(bytes);
            return bytes;
        }
    }

    @Override
    public PacketBuf writeShortArray(short[] shorts) {
        this.writeVarInt(shorts.length);
        for (short s : shorts) {
            this.writeShort(s);
        }
        return this;
    }

    @Override
    public short[] readShortArray(int size) throws DecoderException {
        int length = this.readVarInt();
        if (length > size) {
            throw new DecoderException("ShortArray with size " + length + " is bigger than allowed " + size);
        } else {
            short[] shorts = new short[length];
            for (int i = 0; i < length; i++) {
                shorts[i] = this.readShort();
            }
            return shorts;
        }
    }

    @Override
    public PacketBuf writeIntArray(int[] ints) {
        this.writeVarInt(ints.length);
        for (int i : ints) {
            this.writeInt(i);
        }
        return this;
    }

    @Override
    public int[] readIntArray(int size) throws DecoderException {
        int length = this.readVarInt();
        if (length > size) {
            throw new DecoderException("IntArray with size " + length + " is bigger than allowed " + size);
        } else {
            int[] ints = new int[length];
            for (int i = 0; i < length; i++) {
                ints[i] = this.readInt();
            }
            return ints;
        }
    }

    @Override
    public PacketBuf writeFloatArray(float[] floats) {
        this.writeVarInt(floats.length);
        for (float f : floats) {
            this.writeFloat(f);
        }
        return this;
    }

    @Override
    public float[] readFloatArray(int size) throws DecoderException {
        int length = this.readVarInt();
        if (length > size) {
            throw new DecoderException("FloatArray with size " + length + " is bigger than allowed " + size);
        } else {
            float[] floats = new float[length];
            for (int i = 0; i < length; i++) {
                floats[i] = this.readFloat();
            }
            return floats;
        }
    }

    @Override
    public PacketBuf writeLongArray(long[] longs) {
        this.writeVarInt(longs.length);
        for (long l : longs) {
            this.writeLong(l);
        }
        return this;
    }

    @Override
    public long[] readLongArray(int size) throws DecoderException {
        int length = this.readVarInt();
        if (length > size) {
            throw new DecoderException("LongArray with size " + length + " is bigger than allowed " + size);
        } else {
            long[] longs = new long[length];
            for (int i = 0; i < length; i++) {
                longs[i] = this.readLong();
            }
            return longs;
        }
    }

    @Override
    public PacketBuf writeDoubleArray(double[] doubles) {
        this.writeVarInt(doubles.length);
        for (double d : doubles) {
            this.writeDouble(d);
        }
        return this;
    }

    @Override
    public double[] readDoubleArray(int size) throws DecoderException {
        int length = this.readVarInt();
        if (length > size) {
            throw new DecoderException("DoubleArray with size " + length + " is bigger than allowed " + size);
        } else {
            double[] doubles = new double[length];
            for (int i = 0; i < length; i++) {
                doubles[i] = this.readDouble();
            }
            return doubles;
        }
    }

    @Override
    public PacketBuf writeEnum(@Nonnull Enum<?> value) {
        this.writeVarInt(value.ordinal());
        return this;
    }

    @Override
    public <T extends Enum<T>> T readEnum(@Nonnull Class<T> clazz) {
        return clazz.getEnumConstants()[this.readVarInt()];
    }

    @Override
    public PacketBuf writeUUID(@Nonnull UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    @Override
    public UUID readUUID() {
        return new UUID(this.readLong(), this.readLong());
    }

    @Override
    public PacketBuf writeImage(@Nonnull BufferedImage image, @Nonnull String format) throws EncoderException {
        try (OutputStream stream = new ByteBufOutputStream(this)) {
            ImageIO.write(image, format, stream);
        } catch (Exception e) {
            throw new EncoderException("BufferedImage could not be written to PacketBuf due to an Exception", e);
        }
        return this;
    }

    @Override
    public BufferedImage readImage() throws DecoderException {
        try (InputStream stream = new ByteBufInputStream(this)) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new DecoderException("BufferedImage could not be read from PacketBuf due to an IOException", e);
        }
    }

    @Override
    public PacketBuf writeBinaryData(@Nonnull BaseDataParcel<?> dataPart) throws EncoderException {
        try (OutputStream stream = new ByteBufOutputStream(this)) {
            DataParcelWriter writer = new DataParcelWriter(stream);
            writer.writeParcel(dataPart);
        } catch (IOException e) {
            throw new EncoderException("BaseDataParcel could not be written to PacketBuf due to an Exception", e);
        }
        return this;
    }

    @Override
    public BaseDataParcel<?> readBinaryData() throws DecoderException {
        try (InputStream stream = new ByteBufInputStream(this)) {
            DataParcelReader reader = new DataParcelReader(stream);
            return reader.readParcel();
        } catch (IOException e) {
            throw new DecoderException("BaseDataParcel could not be read from PacketBuf due to an Exception", e);
        }
    }

    @Override
    public PacketBuf markReaderIndex() {
        buffer.markReaderIndex();
        return this;
    }

    @Override
    public PacketBuf resetReaderIndex() {
        buffer.resetReaderIndex();
        return this;
    }

    @Override
    public PacketBuf markWriterIndex() {
        buffer.markWriterIndex();
        return this;
    }

    @Override
    public PacketBuf resetWriterIndex() {
        buffer.resetWriterIndex();
        return this;
    }

    @Override
    public PacketBuf discardReadBytes() {
        buffer.discardReadBytes();
        return this;
    }

    @Override
    public PacketBuf discardSomeReadBytes() {
        buffer.discardSomeReadBytes();
        return this;
    }

    @Override
    public PacketBuf getBytes(int index, ByteBuf dst) {
        buffer.getBytes(index, dst);
        return this;
    }

    @Override
    public PacketBuf getBytes(int index, ByteBuf dst, int length) {
        buffer.getBytes(index, dst, length);
        return this;
    }

    @Override
    public PacketBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        buffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public PacketBuf getBytes(int index, byte[] dst) {
        buffer.getBytes(index, dst);
        return this;
    }

    @Override
    public PacketBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        buffer.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public PacketBuf getBytes(int index, ByteBuffer dst) {
        buffer.getBytes(index, dst);
        return this;
    }

    @Override
    public PacketBuf getBytes(int index, OutputStream out, int length) throws IOException {
        buffer.getBytes(index, out, length);
        return this;
    }

    @Override
    public PacketBuf retain(int increment) {
        buffer.retain(increment);
        return this;
    }

    @Override
    public PacketBuf retain() {
        buffer.retain();
        return this;
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }

    @Override
    public PacketBuf capacity(int newCapacity) {
        buffer.capacity(newCapacity);
        return this;
    }

    @Override
    public int maxCapacity() {
        return buffer.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return buffer.alloc();
    }

    @Override
    @Deprecated
    public ByteOrder order() {
        return buffer.order();
    }

    @Override
    @Deprecated
    public PacketBuf order(ByteOrder endianness) {
        buffer.order(endianness);
        return this;
    }

    @Override
    public PacketBuf unwrap() {
        buffer.unwrap();
        return this;
    }

    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return buffer.isReadOnly();
    }

    @Override
    public PacketBuf asReadOnly() {
        buffer.asReadOnly();
        return this;
    }

    @Override
    public int readerIndex() {
        return buffer.readerIndex();
    }

    @Override
    public PacketBuf readerIndex(int readerIndex) {
        buffer.readerIndex(readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return buffer.writerIndex();
    }

    @Override
    public PacketBuf writerIndex(int writerIndex) {
        buffer.writerIndex(writerIndex);
        return this;
    }

    @Override
    public PacketBuf setIndex(int readerIndex, int writerIndex) {
        buffer.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public int readableBytes() {
        return buffer.readableBytes();
    }

    @Override
    public int writableBytes() {
        return buffer.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return buffer.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return buffer.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return buffer.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return buffer.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return buffer.isWritable(size);
    }

    @Override
    public PacketBuf clear() {
        buffer.clear();
        return this;
    }

    @Override
    public PacketBuf ensureWritable(int minWritableBytes) {
        buffer.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return buffer.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return buffer.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return buffer.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return buffer.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return buffer.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return buffer.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return buffer.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return buffer.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return buffer.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return buffer.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return buffer.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return buffer.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return buffer.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return buffer.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return buffer.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return buffer.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return buffer.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return buffer.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return buffer.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return buffer.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return buffer.getDouble(index);
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return buffer.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return buffer.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return buffer.getCharSequence(index, length, charset);
    }

    @Override
    public PacketBuf setBoolean(int index, boolean value) {
        buffer.setBoolean(index, value);
        return this;
    }

    @Override
    public PacketBuf setByte(int index, int value) {
        buffer.setByte(index, value);
        return this;
    }

    @Override
    public PacketBuf setShort(int index, int value) {
        buffer.setShort(index, value);
        return this;
    }

    @Override
    public PacketBuf setShortLE(int index, int value) {
        buffer.setShortLE(index, value);
        return this;
    }

    @Override
    public PacketBuf setMedium(int index, int value) {
        buffer.setMedium(index, value);
        return this;
    }

    @Override
    public PacketBuf setMediumLE(int index, int value) {
        buffer.setMediumLE(index, value);
        return this;
    }

    @Override
    public PacketBuf setInt(int index, int value) {
        buffer.setInt(index, value);
        return this;
    }

    @Override
    public PacketBuf setIntLE(int index, int value) {
        buffer.setIntLE(index, value);
        return this;
    }

    @Override
    public PacketBuf setLong(int index, long value) {
        buffer.setLong(index, value);
        return this;
    }

    @Override
    public PacketBuf setLongLE(int index, long value) {
        buffer.setLongLE(index, value);
        return this;
    }

    @Override
    public PacketBuf setChar(int index, int value) {
        buffer.setChar(index, value);
        return this;
    }

    @Override
    public PacketBuf setFloat(int index, float value) {
        buffer.setFloat(index, value);
        return this;
    }

    @Override
    public PacketBuf setDouble(int index, double value) {
        buffer.setDouble(index, value);
        return this;
    }

    @Override
    public PacketBuf setBytes(int index, ByteBuf src) {
        buffer.setBytes(index, src);
        return this;
    }

    @Override
    public PacketBuf setBytes(int index, ByteBuf src, int length) {
        buffer.setBytes(index, src, length);
        return this;
    }

    @Override
    public PacketBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        buffer.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public PacketBuf setBytes(int index, byte[] src) {
        buffer.setBytes(index, src);
        return this;
    }

    @Override
    public PacketBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        buffer.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public PacketBuf setBytes(int index, ByteBuffer src) {
        buffer.setBytes(index, src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return buffer.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return buffer.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return buffer.setBytes(index, in, position, length);
    }

    @Override
    public PacketBuf setZero(int index, int length) {
        buffer.setZero(index, length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return buffer.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    @Override
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return buffer.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return buffer.readShort();
    }

    @Override
    public short readShortLE() {
        return buffer.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return buffer.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return buffer.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return buffer.readMedium();
    }

    @Override
    public int readMediumLE() {
        return buffer.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return buffer.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return buffer.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return buffer.readInt();
    }

    @Override
    public int readIntLE() {
        return buffer.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return buffer.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return buffer.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return buffer.readLong();
    }

    @Override
    public long readLongLE() {
        return buffer.readLongLE();
    }

    @Override
    public char readChar() {
        return buffer.readChar();
    }

    @Override
    public float readFloat() {
        return buffer.readFloat();
    }

    @Override
    public double readDouble() {
        return buffer.readDouble();
    }

    @Override
    public ByteBuf readBytes(int length) {
        return buffer.readBytes(length);
    }

    @Override
    public ByteBuf readSlice(int length) {
        return buffer.readSlice(length);
    }

    @Override
    public ByteBuf readRetainedSlice(int length) {
        return buffer.readRetainedSlice(length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst) {
        return buffer.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int length) {
        return buffer.readBytes(dst, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return buffer.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(byte[] dst) {
        return buffer.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return buffer.readBytes(dst, dstIndex, length);
    }

    @Override
    public ByteBuf readBytes(ByteBuffer dst) {
        return buffer.readBytes(dst);
    }

    @Override
    public ByteBuf readBytes(OutputStream out, int length) throws IOException {
        return buffer.readBytes(out, length);
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return buffer.readBytes(out, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return buffer.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return buffer.readBytes(out, position, length);
    }

    @Override
    public ByteBuf skipBytes(int length) {
        buffer.skipBytes(length);
        return this;
    }

    @Override
    public PacketBuf writeBoolean(boolean value) {
        buffer.writeBoolean(value);
        return this;
    }

    @Override
    public PacketBuf writeByte(int value) {
        buffer.writeByte(value);
        return this;
    }

    @Override
    public PacketBuf writeShort(int value) {
        buffer.writeShort(value);
        return this;
    }

    @Override
    public PacketBuf writeShortLE(int value) {
        buffer.writeShortLE(value);
        return this;
    }

    @Override
    public PacketBuf writeMedium(int value) {
        buffer.writeMedium(value);
        return this;
    }

    @Override
    public PacketBuf writeMediumLE(int value) {
        buffer.writeMediumLE(value);
        return this;
    }

    @Override
    public PacketBuf writeInt(int value) {
        buffer.writeInt(value);
        return this;
    }

    @Override
    public PacketBuf writeIntLE(int value) {
        buffer.writeIntLE(value);
        return this;
    }

    @Override
    public PacketBuf writeLong(long value) {
        buffer.writeLong(value);
        return this;
    }

    @Override
    public PacketBuf writeLongLE(long value) {
        buffer.writeLongLE(value);
        return this;
    }

    @Override
    public PacketBuf writeChar(int value) {
        buffer.writeChar(value);
        return this;
    }

    @Override
    public PacketBuf writeFloat(float value) {
        buffer.writeFloat(value);
        return this;
    }

    @Override
    public PacketBuf writeDouble(double value) {
        buffer.writeDouble(value);
        return this;
    }

    @Override
    public PacketBuf writeBytes(ByteBuf src) {
        buffer.writeBytes(src);
        return this;
    }

    @Override
    public PacketBuf writeBytes(ByteBuf src, int length) {
        buffer.writeBytes(src, length);
        return this;
    }

    @Override
    public PacketBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        buffer.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public PacketBuf writeBytes(byte[] src) {
        buffer.writeBytes(src);
        return this;
    }

    @Override
    public PacketBuf writeBytes(byte[] src, int srcIndex, int length) {
        buffer.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public PacketBuf writeBytes(ByteBuffer src) {
        buffer.writeBytes(src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return buffer.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return buffer.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return buffer.writeBytes(in, position, length);
    }

    @Override
    public PacketBuf writeZero(int length) {
        buffer.writeZero(length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return buffer.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return buffer.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return buffer.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return buffer.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return buffer.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return buffer.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return buffer.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return buffer.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return buffer.forEachByteDesc(index, length, processor);
    }

    @Override
    public ByteBuf copy() {
        return buffer.copy();
    }

    @Override
    public ByteBuf copy(int index, int length) {
        return buffer.copy(index, length);
    }

    @Override
    public ByteBuf slice() {
        return buffer.slice();
    }

    @Override
    public ByteBuf retainedSlice() {
        return buffer.retainedSlice();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        return buffer.slice(index, length);
    }

    @Override
    public ByteBuf retainedSlice(int index, int length) {
        return buffer.retainedSlice(index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return buffer.duplicate();
    }

    @Override
    public ByteBuf retainedDuplicate() {
        return buffer.retainedDuplicate();
    }

    @Override
    public int nioBufferCount() {
        return buffer.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return buffer.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return buffer.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return buffer.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return buffer.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return buffer.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return buffer.hasArray();
    }

    @Override
    public byte[] array() {
        return buffer.array();
    }

    @Override
    public int arrayOffset() {
        return buffer.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return buffer.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return buffer.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return buffer.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return buffer.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return buffer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return buffer.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return this.buffer.compareTo(buffer);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }

    @Override
    public PacketBuf touch() {
        buffer.touch();
        return this;
    }

    @Override
    public PacketBuf touch(Object hint) {
        buffer.touch(hint);
        return this;
    }

    @Override
    public int refCnt() {
        return buffer.refCnt();
    }

    @Override
    public boolean release() {
        return buffer.release();
    }

    @Override
    public boolean release(int decrement) {
        return buffer.release(decrement);
    }
}
