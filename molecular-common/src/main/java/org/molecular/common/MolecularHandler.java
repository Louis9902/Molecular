/*
 * This file ("MolecularHandler.java") is part of the molecular-project by Louis.
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

import org.molecular.api.event.EventHandlers;
import org.molecular.api.platform.PlatformHandler;
import org.molecular.api.plugin.PluginContainer;
import org.molecular.common.event.DefaultEventController;
import org.molecular.common.plugin.DefaultPluginController;
import org.molecular.common.resource.DefaultResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;

/**
 * @author Louis
 */

public final class MolecularHandler {

    private static final MolecularHandler instance = new MolecularHandler();

    private final Logger logger;
    private PlatformHandler handler;

    private DefaultPluginController pluginController;
    private DefaultEventController eventController;
    private DefaultResourceController resourceController;

    private MolecularHandler() {
        this.logger = LoggerFactory.getLogger("org.molecular.platform");
    }

    public static MolecularHandler instance() {
        return MolecularHandler.instance;
    }

    public void inject(@Nonnull PlatformHandler handler) {
        this.handler = handler;

        MolecularHooks.registerBinaryDataPart();
        MolecularHooks.registerTextComponent();
        MolecularHooks.registerProtocol();
        MolecularHooks.registerCommands();

        MolecularHooks.injectHandler(handler);

        this.pluginController = MolecularHooks.newPluginController(handler);
        this.eventController = MolecularHooks.newEventController(handler);
        this.resourceController = MolecularHooks.newResourceController(handler);

        //triggers complete at first tick of application
        this.handler.getApplication().scheduleDelayedTask(Executors.callable(this::complete));
    }

    public void loading() {
        {
            this.handler.loading();
        }
        this.pluginController.loading();
    }

    public void initialize() {
        {
            this.handler.initialize();
        }
        this.pluginController.initialize();
        EventHandlers.bakeAll();
    }

    public void complete() {
        {
            this.handler.complete();
        }
        this.pluginController.complete();
    }

    public void terminate() {
        {
            this.handler.terminate();
        }
        this.pluginController.terminate();
    }

    public void destroy() {
        {
            this.handler.destroy();
        }
    }

    public void readResource(@Nonnull PluginContainer container) {
//        Internationalization.instance().inject(container, Locale.US);
    }

    public PlatformHandler getHandler() {
        return this.handler;
    }

    public Logger getLogger() {
        return this.logger;
    }

}
