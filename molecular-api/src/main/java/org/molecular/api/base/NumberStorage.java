/*
 * This file ("NumberStorage.java") is part of the molecular-project by Louis.
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

/**
 * @author Louis
 */

public final class NumberStorage {

    public static short write(int def0, int def1) {
        return (short) (((byte) def1 << 8) + (byte) def0);
    }

    public static byte[] read(short store) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (store & 0xff);
        bytes[1] = (byte) (store >>> 8 & 0xff);
        return bytes;
    }

    public static int write(int def0, int def1, int def2, int def3) {
        return ((byte) def3 << 24) + ((byte) def2 << 16) + ((byte) def1 << 8) + (byte) def0;
    }

    public static byte[] read(int store) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (store & 0xff);
        bytes[1] = (byte) (store >>> 8 & 0xff);
        bytes[2] = (byte) (store >>> 16 & 0xff);
        bytes[3] = (byte) (store >>> 24 & 0xff);
        return bytes;
    }

}
