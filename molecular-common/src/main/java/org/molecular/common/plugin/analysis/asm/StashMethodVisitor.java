/*
 * This file ("StashMethodVisitor.java") is part of the molecular-project by Louis.
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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Louis
 */

public class StashMethodVisitor extends MethodVisitor {

    private final ASMAnnotationStore store;
    private final String name;

    public StashMethodVisitor(ASMAnnotationStore store, String name) {
        super(Opcodes.ASM5);
        this.store = store;
        this.name = name;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        this.store.startMethodAnnotation(this.name, desc);
        return new StashAnnotationVisitor(this.store);
    }
}
