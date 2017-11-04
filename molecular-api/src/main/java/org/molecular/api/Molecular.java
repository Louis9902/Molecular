/*
 * This file ("Molecular.java") is part of the molecular-project by Louis.
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

import org.molecular.api.binary.parcel.BaseDataParcel;
import org.molecular.api.command.Command;
import org.molecular.api.event.EventController;
import org.molecular.api.network.NetworkProtocol;
import org.molecular.api.platform.PlatformHandler;
import org.molecular.api.plugin.PluginController;
import org.molecular.api.registry.IdentRegistry;
import org.molecular.api.registry.IndexRegistry;
import org.molecular.api.resources.ResourceController;
import org.molecular.api.resources.text.BaseTextPart;

import java.util.Random;

/**
 * @author Louis
 */
public final class Molecular {

    public static final Random RANDOM = new Random();

    public static final IndexRegistry<Class<? extends BaseTextPart>> TEXT_COMPONENT_REGISTRY = new IndexRegistry<>("text_component", true);
    public static final IndexRegistry<Class<? extends BaseDataParcel>> DATA_PART_REGISTRY = new IndexRegistry<>("data_part", true);

    public static final IdentRegistry<Integer, NetworkProtocol> PROTOCOL_REGISTRY = new IdentRegistry<>("phase_registry");
    public static final IdentRegistry<String, Command> COMMAND_REGISTRY = new IdentRegistry<>("command_registry");

    private static PlatformHandler handler;

    private static ResourceController resourceController;
    private static PluginController pluginController;
    private static EventController eventController;

    private Molecular() {
    }

    public static String version() {
        return Molecular.class.getPackage().getImplementationVersion();
    }

    public static PlatformHandler getHandler() {
        return Molecular.handler;
    }

    public static ResourceController getResourceController() {
        return Molecular.resourceController;
    }

    public static PluginController getPluginController() {
        return Molecular.pluginController;
    }

    public static EventController getEventController() {
        return Molecular.eventController;
    }

}
