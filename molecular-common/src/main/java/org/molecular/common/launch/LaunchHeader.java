/*
 * This file ("LaunchHeader.java") is part of the molecular-project by Louis.
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

package org.molecular.common.launch;

/**
 * @author Louis
 */

public final class LaunchHeader {

    private static final LaunchHeader instance = new LaunchHeader();

    private LaunchHeader() {
    }

    public static LaunchHeader instance() {
        return LaunchHeader.instance;
    }

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append('\n');
        {
            builder.append('\t').append(" _____     _             _             _____           _         _   ").append('\n');
            builder.append('\t').append("|     |___| |___ ___ _ _| |___ ___ ___|  _  |___ ___  |_|___ ___| |_ ").append('\n');
            builder.append('\t').append("| | | | . | | -_|  _| | | | .'|  _|___|   __|  _| . | | | -_|  _|  _|").append('\n');
            builder.append('\t').append("|_|_|_|___|_|___|___|___|_|__,|_|     |__|  |_| |___|_| |___|___|_|  ").append('\n');
            builder.append('\t').append("                                                    |___|            ").append('\n');
            builder.append('\t').append("_____________________________________________________________________").append('\n');
            builder.append('\t').append("                                                                     ").append('\n');
            builder.append("\t").append("                     Software: Molecular Service                     ").append('\n');
            builder.append("\t").append("      You don't have the permission to decompile this resource!      ").append('\n');
            builder.append('\t').append("_____________________________________________________________________").append('\n');
            builder.append('\t').append("                                                                     ").append('\n');
            builder.append('\t').append("Copyright © Louis S. 2017                                            ").append('\n');
        }
        builder.append('\n');
        return builder.toString();
    }

}
