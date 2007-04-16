package org.codehaus.plexus.builder.runtime;

/*
 * Copyright (c) 2004, Codehaus.org
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

import org.codehaus.plexus.builder.runtime.platform.JswPlatformGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Iterator;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 *
 * @plexus.component role="org.codehaus.plexus.builder.runtime.PlexusRuntimeBootloaderGenerator"
 *                   role-hint="jsw"
 */
public class JswPlexusRuntimeBootloaderGenerator
    implements PlexusRuntimeBootloaderGenerator
{
    private static String JSW = "jsw";

    public static String JSW_VERSION = "3.2.3";

    /**
     * @plexus.requirement role="org.codehaus.plexus.builder.runtime.platform.JswPlatformGenerator"
     */
    private Map platformGenerators;

    /**
     * @plexus.requirement role="org.codehaus.plexus.builder.runtime.GeneratorTools"
     */
    private GeneratorTools tools;

    public void generate( File outputDirectory, Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        try
        {
            File binDirectory = new File( outputDirectory, "bin" );
            tools.mkdirs( binDirectory );

            // ----------------------------------------------------------------------------
            // Look up the appropriate generators
            // ----------------------------------------------------------------------------

            // for now just run them all, this needs to be configurable
            // TODO make the list we load configurable
            Iterator platforms = platformGenerators.keySet().iterator();
            while ( platforms.hasNext() )
            {
                String platformId = (String) platforms.next();
                JswPlatformGenerator platform = (JswPlatformGenerator) platformGenerators.get( platformId );

                configurationProperties.put( "platform.id", platformId );
                platform.generate( binDirectory, null, configurationProperties );
            }
            configurationProperties.remove( "platform.id" );

            // ----------------------------------------------------------------------------
            // The wrapper.jar can be dealt with here because
            // it is common to all the JSW runtime booters.
            // ----------------------------------------------------------------------------

            tools.copyResourceToFile( JSW + "/wrapper-common-" + JSW_VERSION + "/lib/wrapper.jar",
                                      new File( outputDirectory, "core/boot/wrapper.jar" ) );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error whilst creating JSW booters", e);
        }
    }

    public Map getPlatformGenerators()
    {
        return platformGenerators;
    }
}
