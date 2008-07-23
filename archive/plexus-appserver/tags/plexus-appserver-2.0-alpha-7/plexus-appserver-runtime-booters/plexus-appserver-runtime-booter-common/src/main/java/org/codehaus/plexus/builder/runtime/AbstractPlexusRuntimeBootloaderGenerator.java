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

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jason van Zyl
 * @version $Id$
 */
public abstract class AbstractPlexusRuntimeBootloaderGenerator
    extends AbstractLogEnabled
    implements PlexusRuntimeBootloaderGenerator
{
    private final static String CLASSWORLDS_TEMPLATE = "org/codehaus/plexus/builder/templates/classworlds.vm";

    private static final String PROPERTY_APP_NAME = "app.name";

    private static final String PROPERTY_APP_LONG_NAME = "app.long.name";

    private static final String PROPERTY_APP_DESCRIPTION = "app.description";

    /**
     * @requirement
     */
    protected VelocityComponent velocity;

    protected void executable( File file )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        if ( Os.isFamily( "unix" ) )
        {
            Commandline cli = new Commandline();

            cli.setExecutable( "chmod" );

            cli.createArgument().setValue( "+x" );

            cli.createArgument().setValue( file.getAbsolutePath() );

            try
            {
                cli.execute();
            }
            catch ( CommandLineException e )
            {
                throw new PlexusRuntimeBootloaderGeneratorException( "Error executing command line.", e );
            }
        }
    }

    protected File mkdirs( File directory )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        if ( !directory.exists() )
        {
            if ( !directory.mkdirs() )
            {
                throw new PlexusRuntimeBootloaderGeneratorException(
                    "Could not make directories '" + directory.getAbsolutePath() + "'." );
            }
        }

        return directory;
    }

    protected InputStream getResourceAsStream( String resource )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        InputStream is = getClass().getClassLoader().getResourceAsStream( resource );

        if ( is == null )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Could not find resource '" + resource + "'." );
        }

        return is;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected void filterCopy( File in,
                               File out,
                               Map map )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        try
        {
            filterCopy( new FileReader( in ), out, map );
        }
        catch ( FileNotFoundException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error copying resource.", e );
        }
    }

    protected void filterCopy( InputStream in,
                               File out,
                               Map map )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        filterCopy( new InputStreamReader( in ), out, map );
    }

    protected void filterCopy( Reader in,
                               File out,
                               Map map )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( in, map, "@", "@" );

        try
        {
            Writer writer = new FileWriter( out );

            IOUtil.copy( reader, writer );

            writer.close();
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error copying resource.", e );
        }
    }

    protected void copyResource( String filename,
                                 String resource,
                                 boolean makeExecutable,
                                 File basedir )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        File target = new File( basedir, filename );

        try
        {
            copyResourceToFile( resource, target );

            if ( makeExecutable )
            {
                executable( target );
            }
        }
        catch ( Exception e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error copying resource.", e );
        }
    }

    protected void copyResourceToFile( String resource,
                                       File target )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        try
        {
            InputStream is = getResourceAsStream( resource );

            mkdirs( target.getParentFile() );

            OutputStream os = new FileOutputStream( target );

            IOUtil.copy( is, os );

            IOUtil.close( is );

            IOUtil.close( os );
        }
        catch ( Exception e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error copying resource.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Properties loadConfigurationProperties( File configurationPropertiesFile )
        throws IOException, PlexusRuntimeBootloaderGeneratorException
    {
        Properties properties = new Properties();

        if ( configurationPropertiesFile == null )
        {
            throw new PlexusRuntimeBootloaderGeneratorException(
                "The runtime builder requires a configurator properties file." );
        }

        properties.load( new FileInputStream( configurationPropertiesFile ) );

        // ----------------------------------------------------------------------
        // Validate that some required properties are present
        // ----------------------------------------------------------------------

        assertHasProperty( properties, PROPERTY_APP_NAME );

        assertHasProperty( properties, PROPERTY_APP_LONG_NAME );

        assertHasProperty( properties, PROPERTY_APP_DESCRIPTION );

        return properties;
    }

    private void assertHasProperty( Properties properties,
                                    String key )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        if ( StringUtils.isEmpty( properties.getProperty( key ) ) )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Missing configurator property '" + key + "'." );
        }
    }

    // ----------------------------------------------------------------------
    // Velocity methods
    // ----------------------------------------------------------------------

    protected void mergeTemplate( String templateName,
                                  File outputFileName,
                                  boolean dos )
        throws PlexusRuntimeBootloaderGeneratorException
    {
        StringWriter buffer = new StringWriter( 100 * FileUtils.ONE_KB );

        try
        {
            velocity.getEngine().mergeTemplate( templateName, new VelocityContext(), buffer );
        }
        catch ( ResourceNotFoundException ex )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Missing Velocity template: '" + templateName + "'.",
                                                                 ex );
        }
        catch ( Exception ex )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Exception merging the velocity template.", ex );
        }

        try
        {

            FileOutputStream output = new FileOutputStream( outputFileName );

            BufferedReader reader = new BufferedReader( new StringReader( buffer.toString() ) );

            String line;

            while ( ( line = reader.readLine() ) != null )
            {
                output.write( line.getBytes() );

                if ( dos )
                {
                    output.write( '\r' );
                }

                output.write( '\n' );
            }

            output.close();
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBootloaderGeneratorException( "Error merging template.", e );
        }
    }
}
