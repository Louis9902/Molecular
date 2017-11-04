/*
 * This file ("PacketBuffers.java") is part of the molecular-project by Louis.
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
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

/**
 * @author Louis
 */

public final class PacketBuffers {

    private PacketBuffers() {
    }

    public static PacketBuf buffer() {
        return new WrappedPacketBuf(Unpooled.buffer());
    }

    public static PacketBuf buffer(int initialCapacity) {
        return new WrappedPacketBuf(Unpooled.buffer(initialCapacity));
    }

    public static PacketBuf buffer(int initialCapacity, int maxCapacity) {
        return new WrappedPacketBuf(Unpooled.buffer(initialCapacity, maxCapacity));
    }

    public static PacketBuf wrappedBuffer(@Nonnull ByteBuf buffer) {
        return new WrappedPacketBuf(buffer);
    }

    public static PacketBuf wrappedBuffer(@Nonnull byte[] array) {
        return new WrappedPacketBuf(Unpooled.wrappedBuffer(array));
    }

    public static PacketBuf wrappedBuffer(@Nonnull byte[] array, int offset, int length) {
        return new WrappedPacketBuf(Unpooled.wrappedBuffer(array, offset, length));
    }

    public static PacketBuf wrappedBuffer(@Nonnull ByteBuffer buffer) {
        return new WrappedPacketBuf(Unpooled.wrappedBuffer(buffer));
    }

}
