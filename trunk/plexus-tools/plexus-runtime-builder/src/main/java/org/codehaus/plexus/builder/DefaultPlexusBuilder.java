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
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.codehaus.plexus.cdc.MavenModelParser;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;
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
{
    /** */
    public final static String ROLE = DefaultPlexusBuilder.class.getName();

    /** */
    private final static String CORE = "core";

    /** */
    private final static String CLASSWORLDS_TEMPLATE = "org/codehaus/plexus/builder/templates/classworlds.vm";

    /** */
    private final static String UNIX_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus.vm";

    /** */
    private final static String WINDOWS_LAUNCHER_TEMPLATE = "org/codehaus/plexus/builder/templates/plexus-bat.vm";

    ///////////////////////////////////////////////////////////////////////////
    // Requirements

    /** */
    private VelocityComponent velocity;

    ///////////////////////////////////////////////////////////////////////////
    // Configuration

    /**
     * Base directory
     */
    private String baseDirectory;

    /**
     * Maven Local Repository.
     */
    private String mavenRepoLocal;

    /**
     * The location of the project's POM
     */
    private String projectPom;

    /**
     * Plexus version
     */
    private String plexusVersion;

    /**
     * Plexus configuration.
     */
    private String plexusConfiguration;

    /**
     * Velocity bean.
     */
//    private VelocityBean velocityBean;

    /**
     * Component manifes used.
     */
    private String componentManifest;

    /**
     * Directory where configuration files live.
     */
    private String configurationsDirectory;

    /**
     * Configuration properties.
     */
    private String configurationPropertiesFile;

    /**
     * Configuration properties
     */
    private Properties configurationProperties;

    /**
     * Application name.
     */
    private String application;

    /**
     * Application base directory.
     */
    private String applicationBaseDirectory;

    /**
     * Interpolation properties.
     */
    private Properties properties;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public String getApplication()
    {
        return application;
    }

    public void setApplication( String application )
    {
        this.application = application;
    }

    public void setBaseDirectory( String baseDirectory )
    {
        this.baseDirectory = baseDirectory;
    }

    public void setMavenRepoLocal( String mavenRepoLocal )
    {
        this.mavenRepoLocal = mavenRepoLocal;
    }

    public void setPlexusVersion( String plexusVersion )
    {
        this.plexusVersion = plexusVersion;
    }

    public void setPlexusConfiguration( String plexusConfiguration )
    {
        this.plexusConfiguration = plexusConfiguration;
    }

    public void setComponentManifest( String componentManifest )
    {
        this.componentManifest = componentManifest;
    }

    public void setConfigurationsDirectory( String configurationsDirectory )
    {
        this.configurationsDirectory = configurationsDirectory;
    }

    public void setConfigurationPropertiesFile( String configurationPropertiesFile )
    {
        this.configurationPropertiesFile = configurationPropertiesFile;
    }

    public void setProjectPom( String projectPom )
    {
        this.projectPom = projectPom;
    }
    
    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    private void initialize()
        throws Exception
    {
//        velocityBean = new VelocityBean();
        properties = new Properties();
        properties.put( "maven.repo.local", mavenRepoLocal );
    }

    public void build()
        throws Exception
    {
        applicationBaseDirectory = "lib";

        initialize();

        checkBaseDirectory();

        checkMavenRepoLocal();

        createDirectoryStructure();

        assemblePlexusDependencies();

        createClassworldsConfiguration();

        createLauncherScripts();

        findComponents();

        addProjectDependencies();

        processMainConfiguration();

        processConfigurations();

        javaServiceWrapper();

        packageJavaRuntime();

        executable( baseDirectory + "/bin/plexus.sh" );
    }

    private void executable( String file )
        throws Exception
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

    void checkBaseDirectory()
        throws Exception
    {
        if ( baseDirectory == null )
        {
            throw new IllegalStateException( "The f must be specified and cannot be null." );
        }

        File f = new File( baseDirectory );

        if ( !f.exists() )
        {
            log( "Creating baseDirectory: " + f );

            f.mkdirs();
        }
    }

    void checkMavenRepoLocal()
        throws Exception
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

    void log( String message )
    {
        getLogger().info( message );
    }

    void createDirectoryStructure()
        throws Exception
    {
        mkdir( applicationBaseDirectory );

        mkdir( "bin" );

        mkdir( "conf" );

        mkdir( CORE + "/boot");

        mkdir( "logs" );

        mkdir( "temp" );
    }

    void mkdir( String directory )
    {
        File f = new File( baseDirectory, directory );

        if ( !f.exists() )
        {
            f.mkdirs();
        }
    }

    void assemblePlexusDependencies()
        throws Exception
    {
        File p = new File( mavenRepoLocal, "plexus" );

        File f = new File( new File( p, "poms" ), "plexus" + "-" + plexusVersion + ".pom" );

        if ( !f.exists() )
        {
            throw new IllegalStateException( "Plexus POM for the specified version of Plexus does not exists: " + f );
        }

        String[] excludes = new String[]{"classworlds", "junit"};

        MavenModelParser project = new MavenModelParser( f, properties );

        copyDependencies( project, CORE, excludes );

        // Copy classworlds into the core/boot directory

        for ( Iterator i = project.getDependencies().iterator(); i.hasNext(); )
        {
            MavenModelParser.Dependency d = ( MavenModelParser.Dependency )i.next();

            if ( d.getArtifactId().startsWith( "classworlds" ) )
            {
                File dest = new File( baseDirectory, "core/boot" );

                FileUtils.copyFileToDirectory( getDependencyPath( d ), dest.getPath() );
            }
        }
    }

    void copyDependencies( File f, String dir, String[] excludes )
        throws Exception
    {
        if ( !f.exists() )
        {
            System.err.println( "The specified POM doesn't exist: " + f );

            return;
        }

        MavenModelParser project = new MavenModelParser( f, properties );

        copyDependencies( project, dir, excludes );
    }

    void copyDependencies( MavenModelParser project, String dir, String[] excludes )
        throws Exception
    {

        File dest = new File( baseDirectory, dir );
         
        // Copy each dependency to the project directory.
        for ( Iterator i = project.getDependencies().iterator(); i.hasNext(); )
        {
            MavenModelParser.Dependency dep = (MavenModelParser.Dependency) i.next();

            boolean excludeDependency = false;

            if ( excludes != null )
            {
                for ( int j = 0; j < excludes.length; j++ )
                {
                    if ( dep.getArtifactId().startsWith( excludes[j] ) )
                    {
                        excludeDependency = true;

                        break;
                    }
                }
            }

            if ( excludeDependency )
            {
                continue;
            }

            File depFile = new File( getDependencyPath( dep ) );

            if ( depFile.exists() )
            {
                FileUtils.copyFileToDirectory( getDependencyPath( dep ), dest.getPath() );
            }
            else
            {
                System.out.println( "Dependency " + depFile.getPath() + " does not exist!" );
            }
        }

        // Copy the project jar itself
        String projectDir = project.getGroupId();

        if ( projectDir == null )
        {
            projectDir = project.getId();

            int index = projectDir.indexOf( ":" );

            if ( index != -1 )
            {
                projectDir = projectDir.substring( 0, projectDir.indexOf( ":" ) );
            }
        }

        String artifactFile = project.getArtifactId() + "-" + project.getCurrentVersion() + ".jar";

        File g = new File( new File( new File( mavenRepoLocal, projectDir ), "jars" ), artifactFile );

        if ( !g.exists() )
        {
            System.out.println( "Cannot copy POM artifact itself!" );

            return;
        }

        FileUtils.copyFileToDirectory( g.getPath(), dest.getPath() );
    }

    private String getDependencyPath( MavenModelParser.Dependency dep )
    {
        String groupId = dep.getGroupId();

        String artifactId = dep.getArtifactId();

        if ( groupId == null )
        {
            groupId = dep.getId();
        }
        if ( artifactId == null )
        {
            artifactId = dep.getId();
        }

        return mavenRepoLocal + 
            File.separator + groupId + 
            File.separator + dep.getType() + "s" + 
            File.separator + artifactId + "-" + dep.getVersion() + "." + dep.getType();
    }

    void createClassworldsConfiguration()
        throws Exception
    {
        mergeTemplate( CLASSWORLDS_TEMPLATE, new File( new File( baseDirectory, "conf" ), "classworlds.conf" ) );
    }

    void createLauncherScripts()
        throws Exception
    {
        mergeTemplate( UNIX_LAUNCHER_TEMPLATE, new File( new File( baseDirectory, "bin" ), "plexus.sh" ) );

        mergeTemplate( WINDOWS_LAUNCHER_TEMPLATE, new File( new File( baseDirectory, "bin" ), "plexus.bat" ) );
    }

    void findComponents()
        throws Exception
    {
        Set implementations = new HashSet();

        // Load the component manifest.
        Properties p = new Properties();

        FileInputStream is = new FileInputStream( componentManifest );

        p.load( is );

        PlexusConfiguration c = PlexusTools.buildConfiguration( new FileReader( plexusConfiguration ) );

        PlexusConfiguration[] components = c.getChild( "components" ).getChildren( "component" );

        for ( int i = 0; i < components.length; i++ )
        {
            String implementation = components[i].getChild( "implementation" ).getValue();

            implementations.add( implementation );

            // Lookup up the artifact that actually contains the implementation.
            String artifact = p.getProperty( implementation );

            // If we have an entry for this artifact then copy over the dependencies.
            if ( artifact != null )
            {
                copyDependencies( getPom( artifact ), applicationBaseDirectory, null );
            }
        }

        is.close();
    }

    /**
     * Add the dependencies in the main project pom.
     * 
     * @todo find some way to exclude testing dependencies.
     */
    private void addProjectDependencies()
        throws Exception
    {
        copyDependencies( new File( projectPom ), applicationBaseDirectory, null );
    }

    File getPom( String implementation )
    {
        File p = new File( mavenRepoLocal, "plexus" );

        File f = new File( new File( p, "poms" ), implementation + ".pom" );

        return f;
    }

    void processConfigurations()
        throws Exception
    {
        if ( configurationsDirectory == null )
        {
            return;
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

    void processMainConfiguration()
        throws Exception
    {
        File in = new File( plexusConfiguration );

        File out = new File( baseDirectory, "conf/plexus.conf" );

        filterCopy( in, out, getConfigurationProperties() );
    }

    void filterCopy( File in, File out, Map map )
        throws Exception
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( new FileReader( in ), map, "@", "@" );

        Writer writer = new FileWriter( out );

        IOUtil.copy( reader, writer );
    }

    void filterCopy( InputStream in, File out, Map map )
        throws Exception
    {
        InterpolationFilterReader reader = new InterpolationFilterReader( new InputStreamReader( in ), map, "@", "@" );

        Writer writer = new FileWriter( out );

        IOUtil.copy( reader, writer );
    }

    Properties getConfigurationProperties()
        throws Exception
    {
        if ( configurationProperties == null )
        {
            Properties p = new Properties();

            p.load( new FileInputStream( configurationPropertiesFile ) );

            configurationProperties = new Properties();

            for ( Enumeration e = p.propertyNames(); e.hasMoreElements(); )
            {
                String name = (String) e.nextElement();

                configurationProperties.setProperty( name, p.getProperty( name ) );
            }
        }

        return configurationProperties;
    }

    private static String JSW = "jsw";

    void javaServiceWrapper()
        throws Exception
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

        OutputStream os = new FileOutputStream( new File( baseDirectory, "core/wrapper.jar" ) );

        IOUtil.copy( is, os );

        filterCopy( cl.getResourceAsStream( JSW + "/wrapper.conf" ),
                    new File( baseDirectory, "conf/wrapper.conf" ),
                    getConfigurationProperties() );

        copyResources( "bin/linux", cl, linux );

        copyResources( "bin/windows", cl, windows );
    }

    private void copyResources( String directory, ClassLoader cl, String[] resources )
        throws Exception
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

    void packageJavaRuntime()
        throws Exception
    {
    }

    private void mergeTemplate( String templateName, File outputFileName )
        throws IOException, PlexusRuntimeBuilderException
    {
        FileWriter output = new FileWriter( outputFileName );

        try
        {
        	velocity.mergeTemplate( templateName, new VelocityContext(), output );
        }
        catch( ResourceNotFoundException ex)
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
