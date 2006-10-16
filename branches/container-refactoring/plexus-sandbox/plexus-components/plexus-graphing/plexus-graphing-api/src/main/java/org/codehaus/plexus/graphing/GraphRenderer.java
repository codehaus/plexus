package org.codehaus.plexus.graphing;

/*
 * Copyright 2001-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.graphing.model.Graph;

import java.io.File;
import java.io.IOException;

/**
 * GraphRenderer - A renderer of static graphs (non-interactive).
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public interface GraphRenderer
{
    public static final String ROLE = GraphRenderer.class.getName();
    
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
     * @param dag the graph model to render.
     * @param outputFile the output file to create.
     * @throws IOException if there was a problem producing the file.
     * @throws GraphingException if there was a problem creating the graph.
     */
    public void render( Graph graph, File outputFile )
        throws IOException, GraphingException;
}
