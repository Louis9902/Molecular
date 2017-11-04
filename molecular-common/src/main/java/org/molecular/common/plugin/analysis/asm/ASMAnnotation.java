/*
 * This file ("ASMAnnotation.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.analysis.asm;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Louis
 */

public class ASMAnnotation {

    public final String member;
    public final Type annotation;

    public final Map<String, Object> properties;

    private String arrayName;
    private List<Object> arrayCache;

    ASMAnnotation(Type annotation, String member) {
        this.annotation = annotation;
        this.member = member;
        this.properties = new HashMap<>();
    }

    void addProperty(String name, Object value) {
        if (this.arrayCache == null) {
            this.properties.put(name, value);
        } else {
            this.arrayCache.add(value);
        }
    }

    void addEnumProperty(String name, String enumClass, String enumValue) {
        this.properties.put(name, new EnumValue(Type.getType(enumClass), enumValue));
    }

    void openArray(String name) {
        this.arrayName = name;
        this.arrayCache = new ArrayList<>();
    }

    void closeArray() {
        this.properties.put(this.arrayName, this.arrayCache);
        this.arrayCache = null;
    }

    public class EnumValue {

        public final Type enumClass;
        public final String enumValue;

        private EnumValue(Type enumClass, String enumValue) {
            this.enumClass = enumClass;
            this.enumValue = enumValue;
        }
    }

}
