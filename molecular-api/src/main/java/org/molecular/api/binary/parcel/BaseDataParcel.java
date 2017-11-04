/*
 * This file ("BaseDataParcel.java") is part of the molecular-project by Louis.
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

package org.molecular.api.binary.parcel;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.molecular.api.util.APIInternal;

import javax.annotation.Nonnull;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Implementing class must have public constructor with DataInput argument and throws IOException.
 *
 * @author Louis
 */
public abstract class BaseDataParcel<T> {

    protected final String name;
    protected T data;

    @APIInternal
    protected BaseDataParcel(@Nonnull DataInput input) throws IOException {
        this.name = input.readUTF();
        this.read(input);
    }

    protected BaseDataParcel(@Nonnull String name, @Nonnull T data) {
        this.name = name;
        this.data = data;
    }

    @Nonnull
    public T get() {
        return this.data;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public abstract void write(@Nonnull DataOutput output) throws IOException;

    public abstract void read(@Nonnull DataInput input) throws IOException;

    public abstract Class<T> getDataType();

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name, this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseDataParcel)) return false;
        BaseDataParcel<?> that = (BaseDataParcel<?>) o;
        return Objects.equal(this.name, that.name) &&
                Objects.equal(this.data, that.data);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name)
                .add("data", this.data)
                .toString();
    }
}
