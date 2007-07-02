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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.appserver.PlexusRuntimeConstants;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.velocity.VelocityComponent;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class DefaultPlexusRuntimeBuilder
    extends AbstractBuilder
    implements PlexusRuntimeBuilder
{
    /**
     * @plexus.requirement
     */
    private GeneratorTools tools;

    private static final String TEMPLATES = "org/codehaus/plexus/builder/templates";

    private static final String CLASSWORLDS_TEMPLATE = TEMPLATES + "/classworlds.vm";

    // ----------------------------------------------------------------------
    // Properties in the configurator properties file
    // ----------------------------------------------------------------------

    private static final String PROPERTY_CLASSWORLDS_VERSION = "classworlds.version";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    protected VelocityComponent velocity;

    /**
     * @plexus.requirement
     */
    private Map runtimeBooterGenerators;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void build( File workingDirectory, List remoteRepositories, ArtifactRepository localRepository, Set projectArtifacts, Set additionalCoreArtifacts, File containerConfiguration,
                       File containerContextProperties, Properties configurationProperties, boolean addManagementAgent )
        throws PlexusRuntimeBuilderException
    {
        Set managementArtifacts = new HashSet();

        if ( addManagementAgent )
        {
            managementArtifacts.add( "mx4j:mx4j" );
            managementArtifacts.add( "mx4j:mx4j-remote" );
            managementArtifacts.add( "org.livetribe:livetribe-slp" );
            managementArtifacts.add( "backport-util-concurrent:backport-util-concurrent" );
        }

        build( workingDirectory, remoteRepositories, localRepository, projectArtifacts, additionalCoreArtifacts,
               containerConfiguration, containerContextProperties, configurationProperties, addManagementAgent, managementArtifacts );
    }

    public void build( File workingDirectory, List remoteRepositories, ArtifactRepository localRepository,
                       Set projectArtifacts, Set additionalCoreArtifacts, File containerConfiguration,
                       File containerContextProperties, Properties configurationProperties, boolean addManagementAgent, Set managementArtifacts )
        throws PlexusRuntimeBuilderException
    {
        // ----------------------------------------------------------------------
        // Assert the parameters
        // ----------------------------------------------------------------------

        if ( workingDirectory == null )
        {
            throw new PlexusRuntimeBuilderException( "The output directory must be specified." );
        }

        if ( localRepository == null )
        {
            throw new PlexusRuntimeBuilderException( "The local Maven repository must be specified." );
        }

        if ( containerConfiguration == null )
        {
            throw new PlexusRuntimeBuilderException( "The plexus configurator file must be set." );
        }

        if ( !containerConfiguration.exists() )
        {
            throw new PlexusRuntimeBuilderException( "The specified plexus configurator file " + "'" +
                containerConfiguration.getAbsolutePath() + "'" + " doesn't exist." );
        }

        // ----------------------------------------------------------------------
        // Find the artifact lists.
        // ----------------------------------------------------------------------

        Set bootArtifacts;

        Set coreArtifacts;

        try
        {
            bootArtifacts = getBootArtifacts( projectArtifacts, remoteRepositories, localRepository, false );

            Set newAdditionalCoreArtifacts = new HashSet();
            if ( additionalCoreArtifacts != null && !additionalCoreArtifacts.isEmpty() )
            {
                newAdditionalCoreArtifacts.addAll( additionalCoreArtifacts );
            }
            if ( addManagementAgent )
            {
                newAdditionalCoreArtifacts.addAll( managementArtifacts );
            }

            coreArtifacts = getCoreArtifacts( projectArtifacts, newAdditionalCoreArtifacts, remoteRepositories,
                                              localRepository, false );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new PlexusRuntimeBuilderException( "Could not resolve a artifact.", e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new PlexusRuntimeBuilderException( "Could not resolve a artifact.", e );
        }

        // ----------------------------------------------------------------------
        // Find and put the classworlds version into the properties
        // ----------------------------------------------------------------------

        String classworldsVersion =
            resolveVersion( "org.codehaus.plexus", "plexus-classworlds", projectArtifacts, false, new HashSet() );
        configurationProperties.setProperty( PROPERTY_CLASSWORLDS_VERSION, classworldsVersion );

        // add defaults
        configurationProperties.setProperty( "app.max.memory",
                                             configurationProperties.getProperty( "app.max.memory", "128m" ) );

        configurationProperties.setProperty( "app.init.memory",
                                             configurationProperties.getProperty( "app.init.memory", "3m" ) );

        configurationProperties.setProperty( "app.jvm.options",
                                             configurationProperties.getProperty( "app.jvm.options", "" ) );

        // ----------------------------------------------------------------------
        // Build the runtime
        // ----------------------------------------------------------------------

        try
        {
            tools.mkdirs( workingDirectory );

            getLogger().info( "Building runtime in " + workingDirectory.getAbsolutePath() );

            // ----------------------------------------------------------------------
            // Set up the directory structure
            // ----------------------------------------------------------------------

            // TODO: should we set up .base separately?
            tools.mkdirs( getAppsDirectory( workingDirectory ) );

            File binDir = tools.mkdirs( new File( workingDirectory, PlexusRuntimeConstants.BIN_DIRECTORY ) );

            File confDir = tools.mkdirs( new File( workingDirectory, PlexusRuntimeConstants.CONF_DIRECTORY ) );

            File coreDir = tools.mkdirs( new File( workingDirectory, PlexusRuntimeConstants.CORE_DIRECTORY ) );

            File bootDir = tools.mkdirs( new File( workingDirectory, PlexusRuntimeConstants.BOOT_DIRECTORY ) );

            tools.mkdirs( new File( workingDirectory, PlexusRuntimeConstants.LOGS_DIRECTORY ) );

            tools.mkdirs( new File( workingDirectory, PlexusRuntimeConstants.SERVICES_DIRECTORY ) );

            tools.mkdirs( new File( workingDirectory, PlexusRuntimeConstants.TEMP_DIRECTORY ) );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            copyArtifacts( workingDirectory, bootDir, bootArtifacts );

            copyArtifacts( workingDirectory, coreDir, coreArtifacts );

            if ( containerContextProperties != null )
            {
                File rcpTarget = new File( confDir, "plexus.properties" );

                try
                {
                    IOUtil.copy( new FileInputStream( containerContextProperties ), new FileOutputStream( rcpTarget ) );
                }
                catch ( IOException e )
                {
                    throw new PlexusRuntimeBuilderException( "Cannot copy " + containerContextProperties + " to "
                        + rcpTarget, e );
                }
            }

            // ----------------------------------------------------------------------
            // We need to separate between the container configurator that you want
            // shared amongst the apps and the appserver configurator.
            // ----------------------------------------------------------------------

            processMainConfiguration( containerConfiguration, configurationProperties, confDir, addManagementAgent );

            createClassworldsConfiguration( confDir, configurationProperties );

            Iterator generators = runtimeBooterGenerators.keySet().iterator();
            while ( generators.hasNext() )
            {
                String generatorId = (String) generators.next();
                PlexusRuntimeBootloaderGenerator generator = (PlexusRuntimeBootloaderGenerator)
                    runtimeBooterGenerators.get( generatorId );

                System.out.print( "Generating runtime booters " + generatorId + "... ");

                try
                {
                    generator.generate( workingDirectory, configurationProperties );
                    System.out.println( "OK" );
                }
                catch ( PlexusRuntimeBootloaderGeneratorException e )
                {
                    System.out.println( "FAILED" );
                    getLogger().error( e.getMessage(), e );
                }
            }
        }
        catch ( IOException ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception while building the runtime.", ex );
        }
    }

    public void bundle( File outputFile, File workingDirectory )
        throws PlexusRuntimeBuilderException
    {
        Archiver archiver = new JarArchiver();

        try
        {
            archiver.addDirectory( workingDirectory );

            archiver.setDestFile( outputFile );

            archiver.createArchive();
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBuilderException( "Error while creating the archive.", e );
        }
        catch ( ArchiverException e )
        {
            throw new PlexusRuntimeBuilderException( "Error while creating the archive.", e );
        }
    }

    public void addPlexusApplication( File plexusApplication, File runtimeDirectory )
        throws PlexusRuntimeBuilderException
    {
        try
        {
            FileUtils.copyFileToDirectory( plexusApplication, getAppsDirectory( runtimeDirectory ) );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBuilderException( "Error while copying the appserver into the runtime.", e );
        }
    }

    public void addPlexusService( File plexusService, File runtimeDirectory )
        throws PlexusRuntimeBuilderException
    {
        try
        {
            File dir = getServicesDirectory( runtimeDirectory );

            String name = plexusService.getName();

            FileUtils.copyFile( plexusService, new File( dir, name ) );
        }
        catch ( IOException e )
        {
            throw new PlexusRuntimeBuilderException( "Error while copying the appserver into the runtime.", e );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private File getAppsDirectory( File workingDirectory )
    {
        return new File( workingDirectory, PlexusRuntimeConstants.APPLICATIONS_DIRECTORY );
    }

    private File getServicesDirectory( File workingDirectory )
    {
        return new File( workingDirectory, PlexusRuntimeConstants.SERVICES_DIRECTORY );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void createClassworldsConfiguration( File confDir, Properties configurationProperties )
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( CLASSWORLDS_TEMPLATE, new File( confDir, "classworlds.conf" ), true, configurationProperties );
    }

    private void processMainConfiguration( File containerConfiguration, Properties configurationProperties,
                                           File confDir, boolean addManagementAgent )
        throws IOException
    {
        File out = new File( confDir, "plexus.xml" );

        File conf = containerConfiguration;

        if ( addManagementAgent )
        {
            try
            {
                SAXReader reader = new SAXReader();
                Document doc = reader.read( conf );
                Element loadOnStart = (Element) doc.selectSingleNode( "//plexus/load-on-start" );
                if ( loadOnStart == null )
                {
                    loadOnStart = doc.getRootElement().addElement( "load-on-start" );
                }

                loadOnStart.addElement( "component" ).addElement( "role" ).addText(
                    "org.codehaus.plexus.appserver.management.Agent" );

                Element components = (Element) doc.selectSingleNode( "//plexus/components" );
                if ( components == null )
                {
                    components = doc.getRootElement().addElement( "components" );
                }

                Element component = components.addElement( "component" );
                component.addElement( "role" ).addText( "org.codehaus.plexus.appserver.management.Agent" );
                component.addElement( "implementation" ).addText(
                    "org.codehaus.plexus.appserver.management.DefaultAgent" );
                Element requirement = component.addElement( "requirements" ).addElement( "requirement" );
                requirement.addElement( "role" ).addText( "org.codehaus.plexus.appserver.management.MBean" );
                requirement.addElement( "field-name" ).addText( "mbeans" );
                Element configuration = component.addElement( "configuration" );
                configuration.addElement( "serviceUrl" ).addText( "service:jmx:rmi:///" );
                configuration.addElement( "slpPort" ).addText( "3427" );

                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setLineSeparator( System.getProperty( "line.separator" ) );
                conf = new File( "target/tmp/plexus.xml" );
                conf.getParentFile().mkdirs();
                XMLWriter writer = new XMLWriter( new FileWriter( conf ), format );
                writer.write( doc );
                writer.close();
            }
            catch ( DocumentException e )
            {
                getLogger().warn( "can't read " + conf.getCanonicalPath() );
            }
        }

        tools.filterCopy( conf, out, configurationProperties );
    }

    // ----------------------------------------------------------------------
    // Velocity methods
    // ----------------------------------------------------------------------

    protected void mergeTemplate( String templateName, File outputFileName, boolean dos,
                                  Properties configurationProperties )
        throws IOException, PlexusRuntimeBuilderException
    {
        StringWriter buffer = new StringWriter( 100 * FileUtils.ONE_KB );

        File tmpFile = File.createTempFile( outputFileName.getName(), null );

        //noinspection OverlyBroadCatchBlock
        try
        {
            velocity.getEngine().mergeTemplate( templateName, new VelocityContext(), buffer );
        }
        catch ( ResourceNotFoundException ex )
        {
            throw new PlexusRuntimeBuilderException( "Missing Velocity template: '" + templateName + "'.", ex );
        }
        catch ( Exception ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception merging the velocity template.", ex );
        }

        FileOutputStream output = new FileOutputStream( tmpFile );

        BufferedReader reader = new BufferedReader( new StringReader( buffer.toString() ) );

        String line;

        //noinspection NestedAssignment
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

        tools.filterCopy( tmpFile, outputFileName, configurationProperties, "@{", "}@" );
    }
}
