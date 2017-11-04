/*
 * This file ("TextPartEmpty.java") is part of the molecular-project by Louis.
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
import java.io.IOException;

/**
 * @author Louis
 */

public class TextPartEmpty extends BaseTextPart {

    @APIInternal
    public TextPartEmpty(@Nonnull DataInput input) throws IOException {
        super(input);
    }

    public TextPartEmpty() {
    }

    @Override
    public String getDisplayString() {
        return "";
    }

    @Override
    public String getUnformattedString() {
        return "";
    }

}
