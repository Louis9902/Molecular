/*
 * This file ("ASMAnnotationStore.java") is part of the molecular-project by Louis.
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

import org.molecular.common.plugin.analysis.DataWatcher;
import org.molecular.common.plugin.analysis.source.PluginSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Louis
 */

public class ASMAnnotationStore implements Iterable<ASMAnnotation> {

    private final LinkedList<ASMAnnotation> annotations;
    private Type clazz;

    public ASMAnnotationStore(@Nonnull InputStream stream) throws IOException {
        this.annotations = new LinkedList<>();
        try {
            ClassReader reader = new ClassReader(stream);
            reader.accept(new StashClassVisitor(this), ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        } catch (Exception e) {
            throw new IOException("Cannot read class from input stream", e);
        }
    }

    void startClassType(@Nonnull String type) {
        this.clazz = Type.getObjectType(type);
    }

    void startClassAnnotation(@Nonnull String member, @Nonnull String annotation) {
        this.annotations.addFirst(new ASMAnnotation(Type.getType(annotation), member));
    }

    void startFieldAnnotation(@Nonnull String member, @Nonnull String annotation) {
        this.annotations.addFirst(new ASMAnnotation(Type.getType(annotation), member));
    }

    void startMethodAnnotation(@Nonnull String member, @Nonnull String annotation) {
        this.annotations.addFirst(new ASMAnnotation(Type.getType(annotation), member));
    }

    void addProperty(@Nonnull String name, @Nullable Object value) {
        this.annotations.getFirst().addProperty(name, value);
    }

    void addEnumProperty(@Nonnull String name, @Nonnull String enumClass, @Nonnull String enumValue) {
        this.annotations.getFirst().addEnumProperty(name, enumClass, enumValue);
    }

    void openArrayProperty(@Nonnull String name) {
        this.annotations.getFirst().openArray(name);
    }

    void closeArrayProperty() {
        this.annotations.getFirst().closeArray();
    }

    public void transfer(DataWatcher watcher, PluginSource source) {
        for (ASMAnnotation annotation : this) {
            watcher.insert(source, annotation.annotation.getClassName(), this.getClassName(), annotation.member, annotation.properties);
        }
    }

    public String getClassName() {
        return this.clazz.getClassName();
    }

    @Override
    public Iterator<ASMAnnotation> iterator() {
        return this.annotations.iterator();
    }
}
