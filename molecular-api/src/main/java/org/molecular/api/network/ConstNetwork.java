/*
 * This file ("ConstNetwork.java") is part of the molecular-project by Louis.
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

package org.molecular.api.network;

/**
 * @author Louis
 */

public final class ConstNetwork {

    public static final String TIMEOUT = "molecular:timeout";

    public static final String DECRYPT = "molecular:decrypt";

    public static final String SPLITTER = "molecular:splitter";

    public static final String DECOMPRESS = "molecular:decompress";

    public static final String DECODER = "molecular:decoder";

    public static final String ENCRYPT = "molecular:encrypt";

    public static final String PREPENDER = "molecular:prepender";

    public static final String COMPRESS = "molecular:compress";

    public static final String ENCODER = "molecular:encoder";

    public static final String HANDLER = "molecular:handler";

    public static final int HANDSHAKE = -1;

    public static final int LOGIN = 0;

    public static final int WORK = 1;

}
