package org.codehaus.plexus.application;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.codehaus.plexus.cdc.MavenModelParser;
import org.codehaus.plexus.cdc.MavenModelParser.Dependency;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.xstream.PlexusTools;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

/**
 * @author <a href="jason@zenplex.com">Jason van Zyl</a>
 *
 * @todo Need to download artifacts if they are missing
 * @todo Need to support multiple component repositories
 * @todo Deal with generation of 1) Just the container and 2) Just an application
 *       Right now we have them globbed together
 * @todo Create a simple chain of command for processing so the building of the
 *       container runtime can be easily customized
 * @todo turn this into a plexus component and reuse the velocity component.
 */
public class ApplicationBuilder
{
    /** Base directory */
    private String baseDirectory;

    /** Maven Local Repository. */
    private String mavenRepoLocal;

    /** The location of the project's POM */
    private String projectPom;

    /** Plexus configuration. */
    private String plexusConfiguration;

    /** Velocity bean. */
    private VelocityBean velocityBean;

    /** Component manifes used. */
    private String componentManifest;

    /** Directory where configuration files live. */
    private String configurationsDirectory;

    /** Configuration properties. */
    private String configurationPropertiesFile;

    /** Configuration properties */
    private Properties configurationProperties;

    /** Application name. */
    private String application;

    /** Application base directory. */
    private String applicationBase;

    /** Application lib directory. */
    private String applicationLib;
    
    /** Interpolation properties. */
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

    public void setPlexusConfiguration( String plexusConfiguration )
    {
        this.plexusConfiguration = plexusConfiguration;
    }
    
    public void setBaseDirectory( String baseDirectory )
    {
        this.baseDirectory = baseDirectory;
    }

    public void setMavenRepoLocal( String mavenRepoLocal )
    {
        this.mavenRepoLocal = mavenRepoLocal;
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
        velocityBean = new VelocityBean();
        properties = new Properties();
        properties.put( "maven.repo.local", mavenRepoLocal );
    }

    public void build()
        throws Exception
    {
        applicationBase = baseDirectory + "/" + application;
        
        applicationLib = applicationBase + "/lib";
        
        initialize();

        checkBaseDirectory();

        checkMavenRepoLocal();

        createDirectoryStructure();

        findComponents();

        addProjectDependencies();
        
        processMainConfiguration();
        
        processConfigurations();
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
        System.out.println( message );
    }

    void createDirectoryStructure()
        throws Exception
    {
        mkdir( application );

        mkdir( application + "/conf" );

        mkdir( application + "/lib" );
    }

    void mkdir( String directory )
    {
        File f = new File( baseDirectory, directory );

        if ( !f.exists() )
        {
            f.mkdirs();
        }
    }

    /**
     * Copy a POM's dependencies to the specified directory.
     * 
     * @param f The File pointing to the POM.
     * @param dir THe directory to copy the dependencies to.
     * @throws Exception
     */
    void copyDependencies( File f, String dir )
        throws Exception
    {
        if ( !f.exists() )
        {
            throw new IllegalArgumentException( "The specified POM must exist: " + f );
        }
        
        MavenModelParser project = new MavenModelParser( f, properties );

        File dest = new File( dir );
         
        // Copy each dependency to the project directory.
        for ( Iterator i = project.getDependencies().iterator(); i.hasNext(); )
        {
            Dependency dep = (Dependency) i.next();

            File depFile = new File( getDependencyPath( dep ) );
            
            if ( depFile.exists() )
            {
                FileUtils.copyFileToDirectory( getDependencyPath( dep ), 
                                               dest.getPath() );
            }
            else
            {
                log("Dependency " + depFile.getPath() + " does not exist!");  
            }
        }

        // Copy the project jar itself
        String projectDir = project.getGroupId();

        if ( projectDir == null )
        {
            projectDir = project.getId();

            int index = projectDir.indexOf( ":" );
            
            if ( index != -1 )
            	projectDir = projectDir.substring( 0, projectDir.indexOf( ":" ) );
        }

        String artifactFile = project.getArtifactId() + "-" + project.getCurrentVersion() + ".jar";

        File g = new File( new File( new File( mavenRepoLocal, projectDir ), "jars" ), artifactFile );

        if ( !g.exists() )
        {
            System.out.println( "Cannot copy POM artifact!" );

            return;
        }
       
        FileUtils.copyFileToDirectory( g.getPath(), dest.getPath() );
    }

	/**
	 * @param dep
	 * @return
	 */
	private String getDependencyPath( Dependency dep )
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
        
        return mavenRepoLocal + File.separator +
            groupId + File.separator +
            "jars" + File.separator +
            artifactId + "-" + dep.getVersion() + ".jar";
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
                copyDependencies( getPom( artifact ), applicationLib );
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
        copyDependencies( new File(projectPom), applicationLib  );
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

            File out = new File( new File( applicationBase, "conf" ), files[i] );

            filterCopy( in, out, getConfigurationProperties() );
        }
    }

    /**
     * Copy the main configuration file.
     */
    void processMainConfiguration()
        throws Exception
    {
        File in = new File( plexusConfiguration );

        File out = new File( applicationBase, "/conf/plexus.conf" );

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
}
