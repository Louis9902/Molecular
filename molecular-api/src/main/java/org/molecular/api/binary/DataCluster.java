/*
 * This file ("DataCluster.java") is part of the molecular-project by Louis.
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

package org.molecular.api.binary;

import org.molecular.api.binary.io.DataParcelable;
import org.molecular.api.binary.parcel.BaseDataParcel;
import org.molecular.api.binary.parcel.DataParcelBoolean;
import org.molecular.api.binary.parcel.DataParcelMap;
import org.molecular.api.binary.parcel.DataParcelString;
import org.molecular.api.binary.parcel.DataParcelUUID;
import org.molecular.api.binary.parcel.array.DataParcelByteArray;
import org.molecular.api.binary.parcel.array.DataParcelIntArray;
import org.molecular.api.binary.parcel.array.DataParcelLongArray;
import org.molecular.api.binary.parcel.array.DataParcelShortArray;
import org.molecular.api.binary.parcel.numeric.DataParcelByte;
import org.molecular.api.binary.parcel.numeric.DataParcelDouble;
import org.molecular.api.binary.parcel.numeric.DataParcelFloat;
import org.molecular.api.binary.parcel.numeric.DataParcelInt;
import org.molecular.api.binary.parcel.numeric.DataParcelLong;
import org.molecular.api.binary.parcel.numeric.DataParcelShort;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author Louis
 */

public class DataCluster implements DataParcelable, Iterable<Map.Entry<String, BaseDataParcel<?>>> {

    private final Map<String, BaseDataParcel<?>> mapping;

    public DataCluster() {
        this.mapping = new HashMap<>();
    }

    public DataCluster(@Nonnull Map<String, BaseDataParcel<?>> mapping) {
        this.mapping = mapping;
    }

    //<editor-fold desc="Primitives">
    public byte getByte(@Nonnull String key) {
        return this.retrieve(key, DataParcelByte.class, (byte) 0);
    }

    public void addByte(@Nonnull String key, byte value) {
        this.add(new DataParcelByte(key, value));
    }

    public short getShort(@Nonnull String key) {
        return this.retrieve(key, DataParcelShort.class, (short) 0);
    }

    public void addSort(@Nonnull String key, short value) {
        this.add(new DataParcelShort(key, value));
    }

    public int getInt(@Nonnull String key) {
        return this.retrieve(key, DataParcelInt.class, 0);
    }

    public void addInt(@Nonnull String key, int value) {
        this.add(new DataParcelInt(key, value));
    }

    public float getFloat(@Nonnull String key) {
        return this.retrieve(key, DataParcelFloat.class, 0F);
    }

    public void addFloat(@Nonnull String key, float value) {
        this.add(new DataParcelFloat(key, value));
    }

    public long getLong(@Nonnull String key) {
        return this.retrieve(key, DataParcelLong.class, 0L);
    }

    public void addLong(@Nonnull String key, long value) {
        this.add(new DataParcelLong(key, value));
    }

    public double getDouble(@Nonnull String key) {
        return this.retrieve(key, DataParcelDouble.class, 0D);
    }

    public void addDouble(@Nonnull String key, double value) {
        this.add(new DataParcelDouble(key, value));
    }

    public boolean getBoolean(@Nonnull String key) {
        return this.retrieve(key, DataParcelBoolean.class, false);
    }

    public void addBoolean(@Nonnull String key, boolean value) {
        this.add(new DataParcelBoolean(key, value));
    }
    //</editor-fold>

    //<editor-fold desc="Arrays">
    @Nonnull
    public byte[] getByteArray(@Nonnull String key, @Nonnegative int defSize) {
        return this.retrieve(key, DataParcelByteArray.class, new byte[defSize]);
    }

    public void addByteArray(@Nonnull String key, @Nonnull byte[] value) {
        this.add(new DataParcelByteArray(key, value));
    }

    @Nonnull
    public short[] getShortArray(@Nonnull String key, @Nonnegative int defSize) {
        return this.retrieve(key, DataParcelShortArray.class, new short[defSize]);
    }

    public void addShortArray(@Nonnull String key, @Nonnull short[] value) {
        this.add(new DataParcelShortArray(key, value));
    }

    @Nonnull
    public int[] getIntArray(@Nonnull String key, @Nonnegative int defSize) {
        return this.retrieve(key, DataParcelIntArray.class, new int[defSize]);
    }

    public void addIntArray(@Nonnull String key, @Nonnull int[] value) {
        this.add(new DataParcelIntArray(key, value));
    }

    @Nonnull
    public long[] getLongArray(@Nonnull String key, @Nonnegative int defSize) {
        return this.retrieve(key, DataParcelLongArray.class, new long[defSize]);
    }

    public void addLongArray(@Nonnull String key, @Nonnull long[] value) {
        this.add(new DataParcelLongArray(key, value));
    }
    //</editor-fold>

    //<editor-fold desc="Util">
    @Nullable
    public String getString(@Nonnull String key) {
        return this.retrieve(key, DataParcelString.class, null);
    }

    public void addString(@Nonnull String key, @Nonnull String value) {
        this.add(new DataParcelString(key, value));
    }

    @Nullable
    public UUID getUUID(@Nonnull String key) {
        return this.retrieve(key, DataParcelUUID.class, null);
    }

    public void addUUID(@Nonnull String key, @Nonnull UUID value) {
        this.add(new DataParcelUUID(key, value));
    }
    //</editor-fold>

    @Nullable
    public BaseDataParcel<?> get(@Nonnull String key) {
        return this.mapping.get(key);
    }

    public void add(@Nonnull BaseDataParcel<?> parcel) {
        this.mapping.put(parcel.getName(), parcel);
    }

    public void remove(@Nonnull String key) {
        this.mapping.remove(key);
    }

    public void clear() {
        this.mapping.clear();
    }

    public int size() {
        return this.mapping.size();
    }

    public boolean contains(@Nonnull String key) {
        return this.mapping.containsKey(key);
    }

    private <T> T retrieve(@Nonnull String key, @Nonnull Class<? extends BaseDataParcel<T>> clazz, @Nullable T def) {
        BaseDataParcel part = this.get(key);
        if (part != null && clazz.isAssignableFrom(part.getClass())) {
            return clazz.cast(part).get();
        }
        return def;
    }

    @Nonnull
    @Override
    public BaseDataParcel<?> parcel(@Nonnull String name) {
        return new DataParcelMap(name, this);
    }

    @Nonnull
    @Override
    public Iterator<Map.Entry<String, BaseDataParcel<?>>> iterator() {
        return this.mapping.entrySet().iterator();
    }
}
