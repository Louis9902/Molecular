/*
 * This file ("CommandInfo.java") is part of the molecular-project by Louis.
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

package org.molecular.common.command;

import org.molecular.api.command.AbstractCommand;
import org.molecular.api.platform.PlatformHandler;
import org.molecular.common.MolecularHandler;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public class CommandInfo extends AbstractCommand {

    public CommandInfo() {
        super("info", "Prints the current connection info of the NetworkBootstrap");
    }

    @Override
    public void execute(@Nonnull String[] args) {
        PlatformHandler handler = MolecularHandler.instance().getHandler();
        String platform = args.length != 0 ? args[0] != null ? args[0] : "" : "";

        switch (platform.toLowerCase()) {
            case "client": {
                System.out.println("----[ Client Channel ]----");
                handler.getBootstrap().channelClient().forEach(System.out::println);
                break;
            }
            case "server": {
                System.out.println("----[ Server Channel ]----");
                handler.getBootstrap().channelServer().forEach(System.out::println);
                break;
            }
            default: {
                System.out.println("----[ Client Channel ]----");
                handler.getBootstrap().channelClient().forEach(System.out::println);
                System.out.println("----[ Server Channel ]----");
                handler.getBootstrap().channelServer().forEach(System.out::println);
            }
        }
    }
}
