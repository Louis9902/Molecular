/*
 * This file ("PacketBufUtils.java") is part of the molecular-project by Louis.
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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Louis
 */

public final class PacketBufUtils {

    public static <E> void writeOptional(@Nonnull PacketBuf buffer, @Nonnull Optional<E> optional, @Nonnull BiConsumer<PacketBuf, E> consumer) {
        buffer.writeBoolean(optional.isPresent());
        optional.ifPresent(value -> consumer.accept(buffer, value));
    }

    public static <E> Optional<E> readOptional(@Nonnull PacketBuf buffer, @Nonnull Function<PacketBuf, E> function) {
        boolean flag = buffer.readBoolean();
        if (flag) {
            return Optional.ofNullable(function.apply(buffer));
        }
        return Optional.empty();
    }

    public static <E> void writeCollection(@Nonnull PacketBuf buffer, @Nonnull Collection<E> collection, @Nonnull BiConsumer<E, PacketBuf> consumer) {
        buffer.writeVarInt(collection.size());
        for (E element : collection) {
            consumer.accept(element, buffer);
        }
    }

    public static <E> void readCollection(@Nonnull PacketBuf buffer, @Nonnull Collection<E> collection, @Nonnull Function<PacketBuf, E> function) {
        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            collection.add(function.apply(buffer));
        }
    }

}
