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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.MavenArtifact;
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
    public final static String ROLE = DefaultPlexusBuilder.class.getName();

    private final static String CORE = "core";

    private static String JSW = "jsw";

    private final static String CLASSWORLDS_TEMPLATE = "org/codehaus/plexus/builder/templates/classworlds.vm";

    private final static String UNIX_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus.vm";

    private final static String WINDOWS_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus-bat.vm";

    private VelocityComponent velocity;

    private MavenProjectBuilder projectBuilder;

    private ArtifactResolver artifactResolver;

    private String baseDirectory;

    private String mavenRepoLocal;

    private MavenProject project;

    private String plexusConfiguration;

    private String configurationsDirectory;

    private String configurationPropertiesFile;

    private Properties configurationProperties;

    private String applicationBaseDirectory;

    private Properties properties;

    private Map artifacts;

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
        applicationBaseDirectory = "lib";

        try
        {
            checkBaseDirectory();
    
            checkMavenRepoLocal();
    
            createDirectoryStructure();
    
            createClassworldsConfiguration();
    
            createLauncherScripts();
    
            artifacts = new HashMap();
    
            findArtifacts( artifacts, project.getArtifacts(), mavenRepoLocal, project.getRepositories() );
    
            copyPlexusDependencies();
    
            copyDependencies();
    
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

    private void checkBaseDirectory()
        throws PlexusRuntimeBuilderException
    {
        if ( baseDirectory == null )
        {
            throw new IllegalStateException( "The f must be specified and cannot be null." );
        }

        File f = new File( baseDirectory );

        if ( !f.exists() )
        {
            getLogger().info( "Creating baseDirectory: " + f );

            f.mkdirs();
        }
    }

    private void checkMavenRepoLocal()
        throws PlexusRuntimeBuilderException
    {
        if ( mavenRepoLocal == null )
        {
            throw new IllegalStateException( "The local Maven repository must be specified and cannot be null." );
        }

        File mavenRepoLocalDirectory = new File( mavenRepoLocal );

        if ( !mavenRepoLocalDirectory.exists() )
        {
            throw new IllegalStateException( "The specified local Maven repository does not exist." );
        }
    }

    private void createDirectoryStructure()
        throws PlexusRuntimeBuilderException
    {
        mkdir( applicationBaseDirectory );

        mkdir( "bin" );

        mkdir( "conf" );

        mkdir( CORE + "/boot");

        mkdir( "logs" );

        mkdir( "temp" );
    }

    private void copyPlexusDependencies()
        throws ProjectBuildingException, IOException
    {
        File bootPath = new File( baseDirectory, CORE + "/boot" );

        File libPath = new File( baseDirectory, "lib" );

        // Move all boot jars to /core/boot
        for ( Iterator it = artifacts.values().iterator(); it.hasNext(); )
        {
            MavenArtifact artifact = ( MavenArtifact )it.next();

            for ( int j = 0; j < bootArtifacts.length; j++ )
            {
                Dependency dependency = artifact.getDependency();

                String groupId = dependency.getGroupId();

                String artifactId = dependency.getArtifactId();

                if ( bootArtifacts[j][0].equals( groupId ) && 
                     bootArtifacts[j][1].equals( artifactId ) )
                {
                    FileUtils.copyFileToDirectory( artifact.getFile(), bootPath );

                    getLogger().info( "Adding " + artifact.getDependency().getId() + " to /core/boot");

                    it.remove();

                    break;
                }
            }
        }

        Map plexusArtifacts = new HashMap();

        // find the plexus artifact
        for ( Iterator it = artifacts.values().iterator(); it.hasNext(); )
        {
            MavenArtifact artifact = ( MavenArtifact )it.next();

            Dependency dependency = artifact.getDependency();

            String groupId = dependency.getGroupId();

            String artifactId = dependency.getArtifactId();

            if ( groupId.equals( "plexus" ) && artifactId.equals( "plexus" ) )
            {
                FileUtils.copyFileToDirectory( artifact.getFile(), libPath );
            }

            MavenArtifact plexusPom = artifactResolver.getPomArtifact( dependency, mavenRepoLocal, project.getRepositories() );

            MavenProject plexus = projectBuilder.build( plexusPom.getFile() );

            findArtifacts( plexusArtifacts, plexus.getArtifacts(), mavenRepoLocal, plexus.getRepositories() );
        }

        // move plexus itself and all dependencies to /lib
        Iterator it = plexusArtifacts.values().iterator();

        while ( it.hasNext() )
        {
            MavenArtifact artifact = (MavenArtifact) it.next();

            getLogger().info( "Adding " + artifact.getDependency().getId() + " to /" + CORE);

            FileUtils.copyFileToDirectory( artifact.getFile(), new File( baseDirectory, CORE ) );

            artifacts.remove( artifact.getDependency().getId() );
        }
    }

    private void createClassworldsConfiguration()
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( CLASSWORLDS_TEMPLATE, new File( new File( baseDirectory, "conf" ), "classworlds.conf" ) );
    }

    private void createLauncherScripts()
        throws PlexusRuntimeBuilderException, IOException
    {
        mergeTemplate( UNIX_LAUNCHER_TEMPLATE, new File( new File( baseDirectory, "bin" ), "plexus.sh" ) );

        mergeTemplate( WINDOWS_LAUNCHER_TEMPLATE, new File( new File( baseDirectory, "bin" ), "plexus.bat" ) );
    }

    private void copyDependencies()
        throws IOException, ProjectBuildingException
    {
        Iterator it = artifacts.values().iterator();

        while ( it.hasNext() )
        {
            MavenArtifact artifact = (MavenArtifact) it.next();

            getLogger().info( "Adding " + artifact.getDependency().getId() + " to /hell" );

            FileUtils.copyFileToDirectory( artifact.getFile(), new File( baseDirectory, "lib" ) );
        }
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
    private void findArtifacts( Map artifacts, List projectArtifacts, String mavenRepoLocal, List repositories )
        throws ProjectBuildingException
    {
        for ( Iterator i = projectArtifacts.iterator(); i.hasNext(); )
        {
            MavenArtifact artifact = (MavenArtifact) i.next();

            Dependency dependency = artifact.getDependency();

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

            findArtifacts( artifacts, pom.getArtifacts(), mavenRepoLocal, repositories );
        }
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
            throw new PlexusRuntimeBuilderException( "The plexus configuration file doesn't exist." );
        }

        File out = new File( baseDirectory, "conf/plexus.conf" );

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
            throw new PlexusRuntimeBuilderException( "Missing property: configurationPropertiesFile." );
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

            File out = new File( new File( baseDirectory, "conf" ), files[i] );

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

            try
            {
                p.load( new FileInputStream( configurationPropertiesFile ) );
            }
            catch( IOException ex )
            {
                throw new PlexusRuntimeBuilderException( "Exception while reading configuration file: " + configurationPropertiesFile, ex );
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

        OutputStream os = new FileOutputStream( new File( baseDirectory, CORE + "/wrapper.jar" ) );

        IOUtil.copy( is, os );

        filterCopy( cl.getResourceAsStream( JSW + "/wrapper.conf" ),
                    new File( baseDirectory, "conf/wrapper.conf" ),
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

            File target = new File( baseDirectory, "bin/" + resource );

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

    private void mkdir( String directory )
    {
        File f = new File( baseDirectory, directory );

        if ( !f.exists() )
        {
            f.mkdirs();
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
}
