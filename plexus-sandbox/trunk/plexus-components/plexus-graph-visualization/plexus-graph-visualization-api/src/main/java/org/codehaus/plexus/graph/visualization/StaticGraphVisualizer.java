package org.codehaus.plexus.graph.visualization;

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

import org.codehaus.plexus.graph.exception.GraphException;
import org.codehaus.plexus.graph.visualization.decorators.DecoratedGraph;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

/**
 * StaticGraphVisualizer - visualizer that supports the generation of static images of the graph. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface StaticGraphVisualizer
{
    public static final String ROLE = StaticGraphVisualizer.class.getName();
    
    /**
     * <p>
     * Return the list of output formats that this renderer supports.
     * </p>
     * 
     * <p>
     * Example:
     * </p> 
     * 
     * <pre>
     *   graphviz = { "jpeg", "jpg", "png", "svg", "svgz", "gif", "ps", "fig", "mif", "hpgl", "pcl" }
     *   prefuse = { "jpeg", "jpg", "gif" }
     * </pre>
     * 
     * @return
     */
    public String[] getOutputFormats();

    /**
     * Test a specific output format against the provided renderer to see if it supports it.
     * 
     * @param format the output format to test support for.
     * @return true if renderer supports this output format.
     */
    public boolean supportsOutputFormat( String format );

    /**
     * Render this graphmodel to the output file.
     * 
     * @param graph the graph model to render.
     * @param outputFile the output file to create.
     * @throws IOException if there was a problem producing the file.
     * @throws GraphException if there was a problem creating the graph.
     */
    public void render( DecoratedGraph graph, File outputFile )
        throws IOException, VisualizationException;
    
    /**
     * Render this graphmodel to the output file, scaling down the image to fit within the dimensions specified.
     * 
     * @param graph the graph model to render.
     * @param outputFile the output file to create.
     * @param maxSize the maximum size of the graph.
     * @throws IOException if there was a problem producing the file.
     * @throws GraphException if there was a problem creating the graph.
     */
    public void render( DecoratedGraph graph, File outputFile, Dimension maxSize )
        throws IOException, VisualizationException;
}
