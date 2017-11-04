/*
 * This file ("NumberParser.java") is part of the molecular-project by Louis.
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

import javax.annotation.Nullable;

public final class NumberParser {

    private NumberParser() {
    }

    public static byte asByte(@Nullable Object object) {
        return NumberParser.asByte(object, (byte) 0);
    }

    public static byte asByte(@Nullable Object object, byte def) {
        if (object instanceof Number) {
            return ((Number) object).byteValue();
        }

        try {
            return Byte.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static short asShort(@Nullable Object object) {
        return NumberParser.asShort(object, (short) 0);
    }

    public static short asShort(@Nullable Object object, short def) {
        if (object instanceof Number) {
            return ((Number) object).shortValue();
        }

        try {
            return Short.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static int asInt(@Nullable Object object) {
        return NumberParser.asInt(object, 0);
    }

    public static int asInt(@Nullable Object object, int def) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }

        try {
            return Integer.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static float asFloat(@Nullable Object object) {
        return NumberParser.asFloat(object, 0F);
    }

    public static float asFloat(@Nullable Object object, float def) {
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }

        try {
            return Float.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static long asLong(@Nullable Object object) {
        return NumberParser.asLong(object, 0L);
    }

    public static long asLong(@Nullable Object object, long def) {
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }

        try {
            return Long.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

    public static double asDouble(@Nullable Object object) {
        return NumberParser.asDouble(object, 0D);
    }

    public static double asDouble(@Nullable Object object, double def) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }

        try {
            return Double.valueOf(object.toString());
        } catch (NumberFormatException | NullPointerException e) {
            return def;
        }
    }

}
