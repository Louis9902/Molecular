/*
 * This file ("StashClassVisitor.java") is part of the molecular-project by Louis.
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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Louis
 */

public class StashClassVisitor extends ClassVisitor {

    private final ASMAnnotationStore store;
    private String name;

    StashClassVisitor(ASMAnnotationStore store) {
        super(Opcodes.ASM5);
        this.store = store;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.store.startClassType(this.name = name);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        this.store.startClassAnnotation(this.name, desc);
        return new StashAnnotationVisitor(this.store);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return new StashFieldVisitor(this.store, name);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new StashMethodVisitor(this.store, name + '\000' + desc);
    }

}
