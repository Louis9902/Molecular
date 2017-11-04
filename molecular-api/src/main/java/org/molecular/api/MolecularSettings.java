/*
 * This file ("MolecularSettings.java") is part of the molecular-project by Louis.
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

package org.molecular.api;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Louis
 */

public final class MolecularSettings {

    public static boolean native_transport = true;

    public static int compression_threshold = 256;

    public static int connection_timeout = 30;

    public static String database_name = "molecular";

    public static SocketAddress database_address = new InetSocketAddress("localhost", 27017);
}
