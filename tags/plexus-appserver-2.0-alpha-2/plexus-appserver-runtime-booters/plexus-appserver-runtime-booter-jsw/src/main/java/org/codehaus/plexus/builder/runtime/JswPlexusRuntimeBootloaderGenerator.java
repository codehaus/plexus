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

import org.codehaus.plexus.builder.runtime.platform.PlatformGenerator;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class JswPlexusRuntimeBootloaderGenerator
    extends AbstractPlexusRuntimeBootloaderGenerator
{
    private static String JSW = "jsw";

    private Map platformGenerators;

    public void generate( File outputDirectory, Properties configurationProperties )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        // ----------------------------------------------------------------------------
        // Look up the appropriate generator
        // ----------------------------------------------------------------------------

        //TODO will need a generator context as i need more then properties.

        PlatformGenerator generator = (PlatformGenerator) platformGenerators.get( "" );

        // ----------------------------------------------------------------------------
        // The wrapper.jar and wrapper.conf can be dealt with here because these
        // two bits are common to all the JSW runtime booters.
        // ----------------------------------------------------------------------------

        copyResourceToFile( JSW + "/wrapper.jar", new File( outputDirectory, "boot/wrapper.jar" ) );
    }
}
