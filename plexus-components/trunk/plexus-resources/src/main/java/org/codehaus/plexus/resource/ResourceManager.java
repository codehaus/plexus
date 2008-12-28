package org.codehaus.plexus.resource;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.InputStream;
import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.resource.loader.ResourceNotFoundException;
import org.codehaus.plexus.resource.loader.FileResourceCreationException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @author Jason van Zyl
 * @version $Id$
 */
public interface ResourceManager
{
    String ROLE = ResourceManager.class.getName();

    InputStream getResourceAsInputStream( String name )
        throws ResourceNotFoundException;

    File getResourceAsFile( String name )
        throws ResourceNotFoundException, FileResourceCreationException;

    File getResourceAsFile( String name, String outputFile )
        throws ResourceNotFoundException, FileResourceCreationException;

    void setOutputDirectory( File outputDirectory );

    void addSearchPath( String resourceLoaderId, String searchPath );

    /**
     * Provides compatibility with the Locator utility used by several Maven Plugins.
     * 
     * @deprecated
     */
    File resolveLocation( String location, String localfile )
        throws IOException;

    /**
     * Provides compatibility with the Locator utility used by several Maven Plugins.
     * 
     * @deprecated
     */
    File resolveLocation( String location )
        throws IOException;

    /**
     * Searches for a resource with the given name.
     * 
     * @since 1.0-alpha-5
     */
    PlexusResource getResource( String name )
        throws ResourceNotFoundException;

    /**
     * Returns a file with the given resources contents. If the resource is already available as a file, returns that
     * file. Otherwise, a file in the resource managers output directory is created and the resource is downloaded to
     * that file.
     * 
     * @since 1.0-alpha-5
     */
    File getResourceAsFile( PlexusResource resource )
        throws FileResourceCreationException;

    /**
     * Downloads the resource to the given output file.
     * 
     * @since 1.0-alpha-5
     */
    void createResourceAsFile( PlexusResource resource, File outputFile )
        throws FileResourceCreationException;
}
