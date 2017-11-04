/*
 * This file ("ServerLaunch.java") is part of the molecular-project by Louis.
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

package org.molecular;

import joptsimple.OptionSet;
import org.molecular.common.launch.LaunchCommands;
import org.molecular.server.ServerApplication;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;

import static org.molecular.common.launch.LaunchCommands.*;

/**
 * @author Louis
 */

public class ServerLaunch {

    static {
        System.setProperty("io.netty.maxDirectMemory", "0");
        System.setProperty("io.netty.leakDetectionLevel", "DISABLED");
    }

    public static void main(String[] args) throws Exception {
        OptionSet options = LaunchCommands.parse(args);

        if (options.has(HELP)) {
            LaunchCommands.printHelp(System.err);
            return;
        }

        if (options.has(VERSION)) {
            final Package pack = ServerLaunch.class.getPackage();
            System.out.println(pack.getImplementationTitle() + ' ' + pack.getImplementationVersion());
            System.out.println(pack.getSpecificationTitle() + ' ' + pack.getSpecificationVersion());
            return;
        }

        if (options.has(REDIRECT_STDOUT)) {
        }

        if (options.has(LOG_LEVEL)) {
        }

        Path directory = options.valueOf(DIRECTORY).toPath();
        SocketAddress address = new InetSocketAddress(options.valueOf(HOST), options.valueOf(PORT));

        new ServerApplication(directory, address).startup();
    }

}
