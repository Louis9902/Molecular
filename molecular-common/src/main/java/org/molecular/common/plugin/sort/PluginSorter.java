/*
 * This file ("PluginSorter.java") is part of the molecular-project by Louis.
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

package org.molecular.common.plugin.sort;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import org.molecular.api.plugin.PluginContainer;
import org.molecular.api.plugin.meta.PluginDependency;
import org.molecular.common.exception.PluginSortingException;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Louis
 */

public class PluginSorter {


    public static Set<PluginContainer> sort(Iterable<PluginContainer> containers) {
        MutableGraph<PluginContainer> graph = GraphBuilder.directed().build();
        for (PluginContainer container : containers) {
            graph.addNode(container);
            for (PluginDependency dependency : container.metadata().getDependencies()) {

            }
        }
        return null;
    }

    private static <T> Set<T> algorithmTopologicalOrder(@Nonnull Graph<T> graph) {
        MutableGraph<T> mutable = GraphBuilder.from(graph).build();

        if (Graphs.hasCycle(graph)) {
            throw new PluginSortingException("Cycle detected in the input graph, sorting is not possible");
        }

        Set<T> collection = new HashSet<>();
        Set<T> nodes = mutable.nodes();

        while (nodes.size() != 0) {
            for (T value : nodes) {
                if (graph.successors(value).size() == 0) {
                    collection.add(value);
                    mutable.removeNode(value);
                    break;
                }
            }
        }
        return collection;
    }

}
