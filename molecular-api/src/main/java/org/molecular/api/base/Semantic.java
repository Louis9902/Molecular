/*
 * This file ("Semantic.java") is part of the molecular-project by Louis.
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

package org.molecular.api.base;

import javax.annotation.Nonnull;

import static org.molecular.api.base.NumberParser.asInt;

/**
 * @author Louis
 */

public final class Semantic {

    public final int major, minor, patch;
    public final int version;
    public final String print;

    public Semantic(@Nonnull String version) {
        this(asInt(version, -1));
    }

    public Semantic(int version) {
        this.version = version;
        this.patch = (byte) (version & 255);
        this.minor = (byte) (version >>> 8 & 255);
        this.major = (byte) (version >>> 16 & 255);
        this.print = String.format("%d.%d.%d", this.major, this.minor, this.patch);
    }

    public Semantic(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.version = ((byte) major << 16) + ((byte) minor << 8) + (byte) patch;
        this.print = String.format("%d.%d.%d", this.major, this.minor, this.patch);
    }

    @Override
    public String toString() {
        return this.print;
    }

}
