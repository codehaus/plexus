package org.codehaus.plexus.builder;

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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.GenericMavenArtifact;
import org.apache.maven.artifact.MavenArtifact;
import org.apache.maven.artifact.collector.ArtifactCollectionResult;
import org.apache.maven.artifact.collector.ArtifactCollector;
import org.apache.maven.artifact.factory.MavenArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * @todo Need to download artifacts if they are missing
 * @todo Need to support multiple component repositories
 * @todo Deal with generation of 1) Just the container and 2) Just an application
 * Right now we have them globbed together
 * @todo Create a simple chain of command for processing so the building of the
 * container runtime can be easily customized
 * @todo turn this into a plexus component and reuse the velocity component.
 *
 * @author <a href="jason@zenplex.com">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 */
public class DefaultPlexusBuilder
    extends AbstractLogEnabled
    implements PlexusBuilder
{
    private final static String CORE = "core";

    private static String JSW = "jsw";

    private final static String CLASSWORLDS_TEMPLATE = "org/codehaus/plexus/builder/templates/classworlds.vm";

    private final static String UNIX_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus.vm";

    private final static String WINDOWS_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus-bat.vm";

    private VelocityComponent velocity;

    private MavenProjectBuilder projectBuilder;

    private ArtifactResolver artifactResolver;

    private ArtifactCollector artifactCollector;

    private MavenArtifactFactory artifactFactory;

    private String baseDirectory;

    private String mavenRepoLocal;

    private MavenProject project;

    private String applicationName;

    private String plexusConfiguration;

    private String configurationsDirectory;

    private String configurationPropertiesFile;

    private Properties configurationProperties;

    private String applicationBaseDirectory;

    private Properties properties;

    private Map artifacts;

    private File applicationLibDir;

    private File binDir;

    private File confDir;

    private File coreDir;

    private File bootDir;

    private File logsDir;

    private File tempDir;

    private final static String[][] bootArtifacts =
    {
        { "classworlds", "classworlds" }
    };

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public void setBaseDirectory( String baseDirectory )
    {
        this.baseDirectory = baseDirectory;
    }

    public void setMavenRepoLocal( String mavenRepoLocal )
    {
        this.mavenRepoLocal = mavenRepoLocal;
    }

    public void setApplicationName( String applicationName )
    {
        this.applicationName = applicationName;
    }

    public void setPlexusConfiguration( String plexusConfiguration )
    {
        this.plexusConfiguration = plexusConfiguration;
    }

    public void setConfigurationsDirectory( String configurationsDirectory )
    {
        this.configurationsDirectory = configurationsDirectory;
    }

    public void setConfigurationPropertiesFile( String configurationPropertiesFile )
    {
        this.configurationPropertiesFile = configurationPropertiesFile;
    }

    public void setProject( MavenProject project )
    {
        this.project = project;
    }

    public void build()
        throws PlexusRuntimeBuilderException
    {
        try
        {
            checkApplicationConfiguration();

            checkBaseDirectory();
    
            checkMavenRepoLocal();
    
            createDirectoryStructure();
    
            createClassworldsConfiguration();
    
            createLauncherScripts();
    
            artifacts = findArtifacts( project, mavenRepoLocal, project.getRepositories() );

            copyBootDependencies();

            copyPlexusDependencies();
    
            copyApplicationDependencies();
    
            processMainConfiguration();
    
            processConfigurations();
    
            javaServiceWrapper();
    
            packageJavaRuntime();
    
            executable( baseDirectory + "/bin/plexus.sh" );
        }
        catch( PlexusRuntimeBuilderException ex )
        {
            throw ex;
        }
        catch( ProjectBuildingException ex)
        {
            throw new PlexusRuntimeBuilderException( "Exception while building the runtime.", ex );
        }
        catch( IOException ex)
        {
            throw new PlexusRuntimeBuilderException( "Exception while building the runtime.", ex );
        }
    }

    private void executable( String file )
        throws IOException
    {
        if ( Os.isFamily( "unix" ) )
        {
            Commandline cli = new Commandline();

            cli.setExecutable( "chmod" );

            cli.createArgument().setValue( "+x" );

            cli.createArgument().setValue( file );

            cli.execute();
        }
    }

    private void checkApplicationConfiguration()
        throws PlexusRuntimeBuilderException
    {
        if ( applicationName == null || applicationName.trim().length() == 0 )
        {
            throw new PlexusRuntimeBuilderException( "The application name must be set." );
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
    }

    private void checkMavenRepoLocal()
        throws PlexusRuntimeBuilderException
    {
        if ( mavenRepoLocal == null )
        {
            throw new PlexusRuntimeBuilderException( "The local Maven repository must be specified." );
        }

        File mavenRepoLocalDirectory = new File( mavenRepoLocal );

        if ( !mavenRepoLocalDirectory.exists() )
        {
            throw new PlexusRuntimeBuilderException( "The specified local Maven repository does not exist." );
        }
    }

    private void createDirectoryStructure()
        throws PlexusRuntimeBuilderException
    {
        applicationLibDir = new File( baseDirectory, "application/" + applicationName + "/lib" );

        binDir = new File( baseDirectory, "bin" );

        confDir = new File( baseDirectory, "conf" );

        coreDir = new File( baseDirectory, "core" );

        bootDir = new File( baseDirectory, "core/boot" );

        logsDir = new File( baseDirectory, "logs" );

        tempDir = new File( baseDirectory, "temp" );

        mkdir( applicationLibDir );

        mkdir( binDir );

        mkdir( confDir );

        mkdir( bootDir );

        mkdir( logsDir );

        mkdir( tempDir );
    }

    private void copyBootDependencies()
        throws PlexusRuntimeBuilderException, ProjectBuildingException, IOException
    {
        // Move all boot jars to /core/boot
        for ( Iterator it = new ArrayList( artifacts.values() ).iterator(); it.hasNext(); )
        {
            MavenArtifact artifact = (MavenArtifact) it.next();

            if ( isBootArtifact( artifact ) )
            {
                copyArtifact( artifact, "core/boot" );
            }
        }
    }

    private void copyPlexusDependencies()
        throws PlexusRuntimeBuilderException, ProjectBuildingException, IOException
    {
        Map plexusArtifacts = new HashMap();

        MavenArtifact plexusArtifact = null;

        // find the plexus artifact
        for ( Iterator it = artifacts.values().iterator(); it.hasNext(); )
        {
            MavenArtifact artifact = ( MavenArtifact )it.next();

            Dependency dependency = artifact.getDependency();

            String groupId = dependency.getGroupId();

            String artifactId = dependency.getArtifactId();

            String type = dependency.getType();

            if ( groupId.equals( "plexus" ) && 
                 artifactId.equals( "plexus" ) &&
                 type.equals( "jar" ) )
            {
                plexusArtifact = artifact;

                break;
            }
        }

        if ( plexusArtifact == null )
        {
            throw new PlexusRuntimeBuilderException( "Could not find plexus:plexus:jar in the dependency list." );
        }

        MavenArtifact plexusPom = artifactResolver.getPomArtifact( plexusArtifact.getDependency(), mavenRepoLocal, project.getRepositories() );

        if ( plexusPom == null )
        {
            throw new PlexusRuntimeBuilderException( "Cannot find pom for: " + plexusArtifact.getDependency().getId() );
        }

        copyArtifact( plexusArtifact, "core" );

        MavenProject plexus = projectBuilder.build( plexusPom.getFile(), project.getLocalRepository() );

        plexusArtifacts = findArtifacts( plexus, mavenRepoLocal, plexus.getRepositories() );

        // move plexus itself and all dependencies to /lib
        Iterator it = plexusArtifacts.values().iterator();

        while ( it.hasNext() )
        {
            MavenArtifact artifact = (MavenArtifact) it.next();

            // as the plexusArtifacs is a new list we got to avoid the bootArtifacs
            if ( isBootArtifact( artifact ) )
            {
                continue;
            }

            copyArtifact( artifact, "core" );
        }
    }

    private void copyApplicationDependencies()
        throws MissingArtifactException, IOException
    {
        Iterator it = new ArrayList( artifacts.values() ).iterator();

        MavenArtifact artifact = new GenericMavenArtifact( project.getGroupId(), project.getArtifactId(), project.getType(), project.getVersion() );

        artifact.setPath( mavenRepoLocal + artifact.generatePath() );

        copyArtifact( artifact, "apps/" + applicationName + "/lib" );

        while ( it.hasNext() )
        {
            artifact = (MavenArtifact) it.next();

            copyArtifact( artifact, "apps/" + applicationName + "/lib" );
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

    /**
     * Loads the POM for the artifact and then tries to download the artifact.
     * 
     * @param dependencies
     * @param artifacts
     * @param mavenRepoLocal
     * @param repositories
     * @throws ProjectBuildingException
     */
    private Map findArtifacts( MavenProject project, String mavenRepoLocal, List repositories )
        throws PlexusRuntimeBuilderException
    {
        ArtifactCollectionResult artifactCollectionResult;

        try
        {
            artifactCollectionResult = artifactCollector.collect( project.getArtifacts(), mavenRepoLocal, repositories, projectBuilder );
        }
        catch( Exception ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception while getting artifacts for " + project.getId(), ex );
        }

        // TODO: Remove this when MNG-20 is solved
        for ( Iterator it = artifactCollectionResult.getArtifacts().values().iterator(); it.hasNext(); )
        {
            MavenArtifact artifact = (MavenArtifact) it.next();

            artifact.setPath( mavenRepoLocal + artifact.generatePath() );
        }

        return artifactCollectionResult.getArtifacts();
    }
/*
    private void findArtifacts( Map artifacts, MavenProject project, String mavenRepoLocal, List repositories )
        throws ProjectBuildingException
    {
        for ( Iterator i = project.getArtifacts().iterator(); i.hasNext(); )
        {
            MavenArtifact artifact = (MavenArtifact) i.next();

            Dependency dependency = artifact.getDependency();

            if ( artifacts.containsKey( dependency.getId() ) )
            {
                continue;
            }

            MavenArtifact pomArtifact = artifactResolver.getPomArtifact( dependency, mavenRepoLocal, repositories );

            if ( pomArtifact == null )
            {
                continue;
                //throw new ProjectBuildingException( "Missing POM for: " + dependency.getId() );
            }

            MavenProject pom = projectBuilder.build( pomArtifact.getFile() );

            artifact = artifactResolver.getArtifact( artifact.getDependency(), mavenRepoLocal, pom.getRepositories() );

//            Dependency d = artifact.getDependency();

            artifacts.put( artifact.getDependency().getId(), artifact );

            // Find all the dependencies of this dependency

            System.err.println( "Finding artifacts for: " + pom.getId() );

            findArtifacts( artifacts, pom, mavenRepoLocal, repositories );
        }
    }
*/
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
            throw new PlexusRuntimeBuilderException( "The plexus configuration file doesn't exist." );
        }

        File out = new File( confDir, "plexus.conf" );

        filterCopy( in, out, getConfigurationProperties() );
    }

    private void processConfigurations()
        throws PlexusRuntimeBuilderException, IOException
    {
        if ( configurationsDirectory == null )
        {
            return;
        }

        if ( configurationPropertiesFile == null )
        {
            throw new PlexusRuntimeBuilderException( "The configuration properties file must be set." );
        }

        DirectoryScanner scanner = new DirectoryScanner();

        scanner.setBasedir( configurationsDirectory );

        scanner.setExcludes( new String[]{configurationPropertiesFile, "**/CVS/**"} );

        scanner.scan();

        String[] files = scanner.getIncludedFiles();

        for ( int i = 0; i < files.length; i++ )
        {
            String file = files[i];

            File in = new File( configurationsDirectory, file );

            File out = new File( confDir, files[i] );

            filterCopy( in, out, getConfigurationProperties() );
        }
    }

    private void filterCopy( File in, File out, Map map )
        throws IOException
    {
        filterCopy( new FileReader( in ), out, map );
    }

    private void filterCopy( InputStream in, File out, Map map )
        throws IOException
    {
        filterCopy( new InputStreamReader( in ), out, map );
    }

    private void filterCopy( Reader in, File out, Map map )
        throws IOException
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( in, map, "@", "@" );

        Writer writer = new FileWriter( out );

        IOUtil.copy( reader, writer );
    }

    private Properties getConfigurationProperties()
        throws PlexusRuntimeBuilderException
    {
        if ( configurationProperties == null )
        {
            Properties p = new Properties();

            File input = new File( configurationPropertiesFile );

            try
            {
                p.load( new FileInputStream( input ) );
            }
            catch( IOException ex )
            {
                throw new PlexusRuntimeBuilderException( "Exception while reading configuration file: " + input.getPath(), ex );
            }

            configurationProperties = new Properties();

            for ( Enumeration e = p.propertyNames(); e.hasMoreElements(); )
            {
                String name = (String) e.nextElement();

                configurationProperties.setProperty( name, p.getProperty( name ) );
            }
        }

        return configurationProperties;
    }

    private void javaServiceWrapper()
        throws PlexusRuntimeBuilderException, IOException
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

    private void copyResources( String directory, ClassLoader cl, String[] resources )
        throws IOException
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
                    executable( target.getPath() );
                }
            }
        }
    }

    private void packageJavaRuntime()
        throws IOException
    {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Util Methods

    private void mkdir( File directory )
    {
        if ( !directory.exists() )
        {
            directory.mkdirs();
        }
    }

    private void mergeTemplate( String templateName, File outputFileName )
        throws IOException, PlexusRuntimeBuilderException
    {
        FileWriter output = new FileWriter( outputFileName );

        try
        {
        	velocity.getEngine().mergeTemplate( templateName, new VelocityContext(), output );
        }
        catch( ResourceNotFoundException ex )
        {
            throw new PlexusRuntimeBuilderException( "Missing Velocity template: '" + templateName + "'.", ex ); 
        }
        catch( Exception ex )
        {
            throw new PlexusRuntimeBuilderException( "Exception merging the velocity template.", ex ); 
        }

        output.close();
    }

    private void copyArtifact( MavenArtifact artifact, String destination )
        throws IOException, MissingArtifactException
    {
        getLogger().info( "Adding " + artifact.getDependency().getId() + " to " + destination );

        if ( !artifact.getFile().exists() )
            throw new MissingArtifactException( artifact );

        FileUtils.copyFileToDirectory( artifact.getFile(), new File( baseDirectory, destination ) );

        artifacts.remove( artifact.getDependency().getId() );
    }

    private boolean isBootArtifact( MavenArtifact artifact )
    {
        for ( int i = 0; i < bootArtifacts.length; i++ )
        {
            Dependency dependency = artifact.getDependency();

            String groupId = dependency.getGroupId();

            String artifactId = dependency.getArtifactId();

            if ( bootArtifacts[i][0].equals( groupId ) && 
                 bootArtifacts[i][1].equals( artifactId ) )
            {
                return true;
            }
        }

        return false;
    }
}
