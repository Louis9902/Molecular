/*
 * This file ("DataParcelWriter.java") is part of the molecular-project by Louis.
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

package org.molecular.api.binary.io;

import org.molecular.api.Molecular;
import org.molecular.api.binary.parcel.BaseDataParcel;
import org.molecular.api.util.APIInternal;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Louis
 */

public class DataParcelWriter implements Closeable {

    private final DataOutputStream stream;

    public DataParcelWriter(OutputStream stream) {
        this.stream = stream instanceof DataOutputStream ? (DataOutputStream) stream : new DataOutputStream(stream);
    }

    @APIInternal
    public static void writeParcel(@Nonnull BaseDataParcel<?> parcel, @Nonnull DataOutput output) throws IOException {
        output.writeByte(Molecular.DATA_PART_REGISTRY.getKeySafe(parcel.getClass()));
        output.writeUTF(parcel.getName());
        parcel.write(output);
    }

    public void writeParcel(@Nonnull BaseDataParcel<?> part) throws IOException {
        DataParcelWriter.writeParcel(part, this.stream);
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
