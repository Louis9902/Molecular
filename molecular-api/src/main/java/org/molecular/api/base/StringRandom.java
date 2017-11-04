/*
 * This file ("StringRandom.java") is part of the molecular-project by Louis.
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

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * @author Louis
 */

public final class StringRandom {

    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String lower = upper.toLowerCase(Locale.ENGLISH);
    private static final String digits = "0123456789";
    private static final String alphanumeric = StringRandom.upper + StringRandom.lower + StringRandom.digits;

    private final Random random;
    private final char[] symbols;
    private final char[] buf;

    public StringRandom() {
        this(21);
    }

    public StringRandom(int length) {
        this(length, new SecureRandom());
    }

    public StringRandom(int length, Random random) {
        this(length, random, StringRandom.alphanumeric);
    }

    public StringRandom(int length, Random random, String symbols) {
        if (length < 1 || symbols.length() < 2) {
            throw new IllegalArgumentException();
        }
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    public String nextString() {
        for (int index = 0; index < this.buf.length; ++index) {
            this.buf[index] = this.symbols[this.random.nextInt(this.symbols.length)];
        }
        return new String(this.buf);
    }

}
