/*
 * This file ("BaseTextPart.java") is part of the molecular-project by Louis.
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

package org.molecular.api.resources.text;

import com.google.common.collect.Iterators;
import org.molecular.api.binary.io.DataParcelable;
import org.molecular.api.binary.parcel.BaseDataParcel;
import org.molecular.api.binary.parcel.DataParcelText;
import org.molecular.api.resources.text.io.TextPartReader;
import org.molecular.api.resources.text.io.TextPartWriter;

import javax.annotation.Nonnull;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Louis
 */

public abstract class BaseTextPart implements DataParcelable, Iterable<BaseTextPart> {

    protected List<BaseTextPart> children;

    protected BaseTextPart() {
        this.children = new ArrayList<>();
    }

    protected BaseTextPart(@Nonnull DataInput input) throws IOException {
        this.children = new ArrayList<>();
        this.read(input);
    }

    public final BaseTextPart append(@Nonnull BaseTextPart component) {
        this.children.add(component);
        return this;
    }

    public final BaseTextPart append(@Nonnull BaseTextPart... components) {
        Arrays.stream(components).forEach(this::append);
        return this;
    }

    public abstract String getDisplayString();

    public abstract String getUnformattedString();

    public final String getDisplayWithChildren() {
        StringBuilder variable = new StringBuilder(this.getDisplayString());
        this.children.forEach(component -> variable.append(component.getDisplayWithChildren()));
        return variable.toString();
    }

    public final String getUnformattedWithChildren() {
        StringBuilder variable = new StringBuilder(this.getUnformattedString());
        this.children.forEach(component -> variable.append(component.getUnformattedWithChildren()));
        return variable.toString();
    }

    @Nonnull
    public final List<BaseTextPart> children() {
        return this.children;
    }


    public void write(@Nonnull DataOutput output) throws IOException {
        output.writeInt(this.children.size());
        for (BaseTextPart child : this.children) {
            TextPartWriter.writePart(child, output);
        }
    }


    public void read(@Nonnull DataInput input) throws IOException {
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            this.append(TextPartReader.readPart(input));
        }
    }

    @Override
    public final BaseDataParcel<?> parcel(@Nonnull String name) {
        return new DataParcelText(name, this);
    }

    @Nonnull
    @Override
    public final Iterator<BaseTextPart> iterator() {
        Iterator<BaseTextPart> children = Iterators.concat(Iterators.transform(this.children.iterator(), BaseTextPart::iterator));
        return Iterators.concat(Iterators.forArray(this), children);
    }
}
