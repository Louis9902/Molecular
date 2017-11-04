/*
 * This file ("TextPartTranslation.java") is part of the molecular-project by Louis.
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

import org.molecular.api.util.APIInternal;

import javax.annotation.Nonnull;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Louis
 */

public class TextPartTranslation extends BaseTextPart {

    private String key;
    private String[] objects;

    @APIInternal
    public TextPartTranslation(@Nonnull DataInput input) throws IOException {
        super(input);
    }

    public TextPartTranslation(@Nonnull String key, @Nonnull String... objects) {
        this.key = key;
        this.objects = objects;
    }

    public void setKey(@Nonnull String key) {
        this.key = key;
    }

    public void setObjects(@Nonnull String[] objects) {
        this.objects = objects;
    }

    @Override
    public String getDisplayString() {
//        return I18n.localize(this.key, (Object[]) this.objects);
        return this.getUnformattedString();
    }

    @Override
    public String getUnformattedString() {
        return String.format("Translation(%s, %s)", this.key, Arrays.toString(this.objects));
    }

    @Override
    public void write(@Nonnull DataOutput output) throws IOException {
        output.writeUTF(this.key);
        output.writeInt(this.objects.length);
        for (String object : this.objects) {
            output.writeUTF(object);
        }
        super.write(output);
    }

    @Override
    public void read(@Nonnull DataInput input) throws IOException {
        this.key = input.readUTF();
        this.objects = new String[input.readInt()];
        for (int i = 0; i < this.objects.length; i++) {
            this.objects[i] = input.readUTF();
        }
        super.read(input);
    }
}
