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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.builder.AbstractBuilder;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @component
 */
public class DefaultPlexusRuntimeBuilder
    extends AbstractBuilder
    implements PlexusRuntimeBuilder
{
    private static String JSW = "jsw";

    private final static String CLASSWORLDS_TEMPLATE = "org/codehaus/plexus/builder/templates/classworlds.vm";

    private final static String UNIX_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus.vm";

    private final static String WINDOWS_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus-bat.vm";

    private final static String APPS_DIRECTORY = "apps";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private File binDir;

    private File coreDir;

    private File bootDir;

    private File logsDir;

    private File tempDir;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Set artifacts;

    private Set plexusArtifacts;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void build()
        throws PlexusRuntimeBuilderException
    {
        try
        {
            checkBaseDirectory();

            checkLocalRepository();

            createDirectoryStructure();

            createClassworldsConfiguration();

            createLauncherScripts();

            artifacts = findArtifacts( project );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            Artifact plexusArtifact = null;

            for ( Iterator it = artifacts.iterator(); it.hasNext(); )
            {
                Artifact artifact = (Artifact) it.next();

                String groupId = artifact.getGroupId();

                String artifactId = artifact.getArtifactId();

                String type = artifact.getType();

                if ( groupId.equals( "plexus" ) && artifactId.equals( "plexus" ) && type.equals( "jar" ) )
                {
                    plexusArtifact = artifact;

                    break;
                }
            }

            if ( plexusArtifact == null )
            {
                throw new PlexusRuntimeBuilderException( "Could not find plexus JAR in the dependency list." );
            }

            Artifact plexusPom = resolve( plexusArtifact.getGroupId(), plexusArtifact.getArtifactId(), plexusArtifact.getVersion(), "pom" );

            if ( plexusPom == null )
            {
                throw new PlexusRuntimeBuilderException( "Cannot find pom for: " + plexusArtifact.getId() );
            }

            MavenProject plexus = buildProject( plexusPom.getFile() );

            plexusArtifacts = findArtifacts( plexus, getRemoteRepositories(), getLocalRepository() );

            plexusArtifacts.add( plexusArtifact );

            Artifact appserver = new DefaultArtifact( "plexus", "plexus-appserver", "1.0-alpha-1-SNAPSHOT", "jar" );

            appserver.setPath( getLocalRepository().getBasedir() + "/plexus/jars/plexus-appserver-1.0-alpha-1-SNAPSHOT.jar" );

            plexusArtifacts.add( appserver );

            // ----------------------------------------------------------------------
            //
            // ----------------------------------------------------------------------

            copyBootDependencies();

            copyPlexusDependencies();

            // ----------------------------------------------------------------------
            // We need to separate between the container configuration that you want
            // shared amongst the apps and the application configuration.
            // ----------------------------------------------------------------------

            processMainConfiguration();

            //processConfigurations();

            javaServiceWrapper();

            packageJavaRuntime();

//            executable( baseDirectory + "/bin/plexus.sh" );
        }
        catch ( PlexusRuntimeBuilderException ex )
        {
            throw ex;
        }
//        catch ( CommandLineException ex )
//        {
//            throw new PlexusRuntimeBuilderException( "Exception while building the runtime.", ex );
//        }
        catch ( IOException ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception while building the runtime.", ex );
        }
    }


    private void checkBaseDirectory()
        throws PlexusRuntimeBuilderException
    {
        if ( baseDirectory == null )
        {
            throw new PlexusRuntimeBuilderException( "The basedir must be specified." );
        }

        File f = new File( baseDirectory );

        mkdir( f );

        getLogger().info( "Building runtime in " + f.getAbsolutePath() );
    }

    private void checkLocalRepository()
        throws PlexusRuntimeBuilderException
    {
        if ( localRepository == null )
        {
            throw new PlexusRuntimeBuilderException( "The local Maven repository must be specified." );
        }
    }

    protected void createDirectoryStructure()
    {
        binDir = new File( baseDirectory, "bin" );

        confDir = new File( baseDirectory, "conf" );

        coreDir = new File( baseDirectory, "core" );

        bootDir = new File( baseDirectory, "core/boot" );

        logsDir = new File( baseDirectory, "logs" );

        tempDir = new File( baseDirectory, "temp" );

        mkdir( binDir );

        mkdir( confDir );

        mkdir( bootDir );

        mkdir( logsDir );

        mkdir( tempDir );
    }

    private void copyBootDependencies()
        throws IOException
    {
        for ( Iterator it = artifacts.iterator(); it.hasNext(); )
        {
            Artifact artifact = (Artifact) it.next();

            if ( !isBootArtifact( artifact ) )
            {
                continue;
            }

            copyArtifact( artifact, bootDir );
        }
    }

    private void copyPlexusDependencies()
        throws IOException
    {
        for ( Iterator i = plexusArtifacts.iterator(); i.hasNext(); )
        {
            Artifact artifact = (Artifact) i.next();

            if ( isBootArtifact( artifact ) )
            {
                continue;
            }

            copyArtifact( artifact, coreDir );
        }
    }

    private void createClassworldsConfiguration()
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( CLASSWORLDS_TEMPLATE, new File( confDir, "classworlds.conf" ) );
    }

    private void createLauncherScripts()
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( UNIX_LAUNCHER_TEMPLATE, new File( binDir, "plexus.sh" ) );

        mergeTemplate( WINDOWS_LAUNCHER_TEMPLATE, new File( binDir, "plexus.bat" ) );
    }

    private void processMainConfiguration()
        throws PlexusRuntimeBuilderException, IOException
    {
        if ( plexusConfiguration == null )
        {
            throw new PlexusRuntimeBuilderException( "The plexus configuration file must be set." );
        }

        File in = new File( plexusConfiguration );

        if ( !in.exists() )
        {
            throw new PlexusRuntimeBuilderException( "The specified plexus configuration file " + "'" + in + "'" + " doesn't exist." );
        }

        File out = new File( confDir, "plexus.conf" );

        filterCopy( in, out, getConfigurationProperties() );
    }

    private void javaServiceWrapper()
        throws PlexusRuntimeBuilderException/*, CommandLineException*/, IOException
    {
        ClassLoader cl = getClass().getClassLoader();

        String[] linux = new String[]
        {
            "linux/libwrapper.so|x",
            "linux/run.sh|x",
            "linux/wrapper|x"
        };

        String[] windows = new String[]
        {
            "windows/InstallService.bat",
            "windows/UninstallService.bat",
            "windows/Wrapper.dll",
            "windows/Wrapper.exe",
            "windows/run.bat"
        };

        InputStream is = cl.getResourceAsStream( JSW + "/wrapper.jar" );

        OutputStream os = new FileOutputStream( new File( coreDir + "/wrapper.jar" ) );

        IOUtil.copy( is, os );

        filterCopy( cl.getResourceAsStream( JSW + "/wrapper.conf" ),
                    new File( confDir, "wrapper.conf" ),
                    getConfigurationProperties() );

        copyResources( "bin/linux", cl, linux );

        copyResources( "bin/windows", cl, windows );
    }

    protected void copyResources( String directory, ClassLoader cl, String[] resources )
        throws /*CommandLineException, */IOException
    {
        InputStream is;

        OutputStream os;

        File file = new File( baseDirectory, directory );

        file.mkdirs();

        for ( int i = 0; i < resources.length; i++ )
        {
            String[] s = StringUtils.split( resources[i], "|" );

            String resource = s[0];

            is = cl.getResourceAsStream( JSW + "/" + resource );

            if ( is == null )
            {
                continue;
            }

            File target = new File( binDir, resource );

            os = new FileOutputStream( target );

            IOUtil.copy( is, os );

            if ( s.length == 2 )
            {
                String instructions = s[1];

                if ( instructions.indexOf( "x" ) >= 0 )
                {
//                    executable( target.getPath() );
                }
            }
        }
    }

    private void packageJavaRuntime()
        throws IOException
    {
    }
}
