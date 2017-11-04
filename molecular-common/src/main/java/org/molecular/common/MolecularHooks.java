/*
 * This file ("MolecularHooks.java") is part of the molecular-project by Louis.
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

package org.molecular.common;

import org.molecular.api.Molecular;
import org.molecular.api.binary.parcel.DataParcelBoolean;
import org.molecular.api.binary.parcel.DataParcelMap;
import org.molecular.api.binary.parcel.DataParcelString;
import org.molecular.api.binary.parcel.DataParcelText;
import org.molecular.api.binary.parcel.DataParcelUUID;
import org.molecular.api.binary.parcel.array.DataParcelByteArray;
import org.molecular.api.binary.parcel.array.DataParcelIntArray;
import org.molecular.api.binary.parcel.array.DataParcelLongArray;
import org.molecular.api.binary.parcel.array.DataParcelShortArray;
import org.molecular.api.binary.parcel.numeric.DataParcelByte;
import org.molecular.api.binary.parcel.numeric.DataParcelDouble;
import org.molecular.api.binary.parcel.numeric.DataParcelFloat;
import org.molecular.api.binary.parcel.numeric.DataParcelInt;
import org.molecular.api.binary.parcel.numeric.DataParcelLong;
import org.molecular.api.binary.parcel.numeric.DataParcelShort;
import org.molecular.api.platform.PlatformHandler;
import org.molecular.api.resources.text.TextPartEmpty;
import org.molecular.api.resources.text.TextPartString;
import org.molecular.api.resources.text.TextPartTranslation;
import org.molecular.common.command.CommandInfo;
import org.molecular.common.command.CommandStop;
import org.molecular.common.event.DefaultEventController;
import org.molecular.common.network.DefaultNetProtocol;
import org.molecular.common.plugin.DefaultPluginController;
import org.molecular.common.resource.DefaultResourceController;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * @author Louis
 */

final class MolecularHooks {

    static void registerTextComponent() {
        Molecular.TEXT_COMPONENT_REGISTRY.register(TextPartEmpty.class);
        Molecular.TEXT_COMPONENT_REGISTRY.register(TextPartString.class);
        Molecular.TEXT_COMPONENT_REGISTRY.register(TextPartTranslation.class);
    }

    static void registerBinaryDataPart() {
        Molecular.DATA_PART_REGISTRY.register(DataParcelByte.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelShort.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelInt.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelFloat.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelLong.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelDouble.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelByteArray.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelShortArray.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelIntArray.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelLongArray.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelBoolean.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelString.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelUUID.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelText.class);
        Molecular.DATA_PART_REGISTRY.register(DataParcelMap.class);
    }

    static void registerProtocol() {
        Molecular.PROTOCOL_REGISTRY.register(DefaultNetProtocol.HANDSHAKE);
        Molecular.PROTOCOL_REGISTRY.register(DefaultNetProtocol.LOGIN);
        Molecular.PROTOCOL_REGISTRY.register(DefaultNetProtocol.WORK);
    }

    static void registerCommands() {
        new CommandInfo().register();
        new CommandStop().register();
    }

    static DefaultPluginController newPluginController(@Nonnull PlatformHandler handler) {
        try {
            DefaultPluginController controller = new DefaultPluginController(handler);
            Field field = Molecular.class.getDeclaredField("pluginController");
            field.setAccessible(true);
            field.set(null, controller);
            return controller;
        } catch (Throwable throwable) {
            throw new Error("Unable to inject plugin controller reference into molecular", throwable);
        }
    }

    static DefaultEventController newEventController(@Nonnull PlatformHandler handler) {
        try {
            DefaultEventController controller = new DefaultEventController(handler);
            Field field = Molecular.class.getDeclaredField("eventController");
            field.setAccessible(true);
            field.set(null, controller);
            return controller;
        } catch (Throwable throwable) {
            throw new Error("Unable to inject event controller reference into molecular", throwable);
        }
    }

    static DefaultResourceController newResourceController(@Nonnull PlatformHandler handler) {
        try {
            DefaultResourceController controller = new DefaultResourceController(handler);
            Field field = Molecular.class.getDeclaredField("resourceController");
            field.setAccessible(true);
            field.set(null, controller);
            return controller;
        } catch (Throwable throwable) {
            throw new Error("Unable to inject resource controller reference into molecular", throwable);
        }
    }

    static void injectHandler(@Nonnull PlatformHandler handler) {
        try {
            Field field = Molecular.class.getDeclaredField("handler");
            field.setAccessible(true);
            field.set(null, handler);
        } catch (Throwable throwable) {
            throw new Error("Unable to inject resource controller reference into molecular", throwable);
        }
    }
}
