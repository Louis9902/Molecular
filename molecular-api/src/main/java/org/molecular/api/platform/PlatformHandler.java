/*
 * This file ("PlatformHandler.java") is part of the molecular-project by Louis.
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

package org.molecular.api.platform;

import org.molecular.api.network.NetworkBootstrap;

import javax.annotation.Nonnull;

/**
 * @author Louis
 */

public interface PlatformHandler<T extends PlatformApplication> {

    /**
     * This is called in the constructor of the {@link PlatformApplication}, which means is called before
     * the application is started via {@link PlatformApplication#startup()}.
     *
     * @param application The application which calls the {@link PlatformHandler}
     */
    void inject(@Nonnull T application);

    /**
     * Before this is called all plugins are loaded, computed and assembled.
     * <p>
     * This should be used to load files like config etc.
     */
    void loading();

    /**
     * Before this all plugins have passed the initialization and are ready to be used.
     * This should be used to register listener or sth. similar.
     * After this all {@link org.molecular.api.event.EventHandlers} are baked.
     */
    void initialize();

    /**
     * Called at the first tick of the application.
     */
    void complete();

    void terminate();

    void destroy();

    Platform getPlatform();

    PlatformPersistent getPersistent();

    PlatformDelegate getDelegate();

    NetworkBootstrap getBootstrap();

    T getApplication();

}
