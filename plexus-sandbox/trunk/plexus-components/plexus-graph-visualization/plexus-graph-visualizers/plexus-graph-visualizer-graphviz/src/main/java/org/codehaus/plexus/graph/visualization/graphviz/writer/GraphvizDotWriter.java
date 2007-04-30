package org.codehaus.plexus.graph.visualization.graphviz.writer;

/*
 * Licensed to the Codehaus Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.graph.visualization.VisualizationException;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;

import java.io.File;
import java.io.IOException;

/**
 * GraphvizDotWriter - interface for writing dot files. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface GraphvizDotWriter
{
    /**
     * Write GraphViz dot file of provided DecoratedGraph.
     * 
     * @param graph the graph to use.
     * @param outputFile the output file.
     * @throws IOException if there was a problem writing the file.
     * @throws VisualizationException if there was a problem parsing the graph.
     */
    public void writeDot( DecoratedGraph graph, File outputFile ) throws IOException, VisualizationException;
}
