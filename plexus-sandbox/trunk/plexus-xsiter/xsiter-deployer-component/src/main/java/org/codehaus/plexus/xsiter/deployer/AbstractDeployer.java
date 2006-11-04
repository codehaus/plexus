package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployerResource;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject.ProjectProperties;
import org.codehaus.plexus.xsiter.vhost.VirtualHostConfiguration;
import org.codehaus.plexus.xsiter.vhost.VirtualHostManager;

/**
 * Base for concrete {@link Deployer} implementation containing service methods.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public abstract class AbstractDeployer
    extends AbstractLogEnabled
    implements Deployer
{

    /**
     * Cargo Maven2 Plugin artifact ID.
     */
    static final String ARTIFACT_ID_CARGO_MAVEN_2_PLUGIN = "cargo-maven2-plugin";

    /**
     * Cargo group ID.
     */
    static final String GROUP_ID_ORG_CODEHAUS_CARGO = "org.codehaus.cargo";

    /**
     * Name of the deployment workspace descriptor.
     */
    protected static final String DESCRIPTOR_WORKSPACE_XML = "workspace.xml";

    /**
     * Source Control Manager.
     * 
     * @plexus.requirement
     */
    protected ScmManager scmManager;

    /**
     * Component to Manage Virtual Hosts for a Project.
     * 
     * @plexus.requirement
     */
    protected VirtualHostManager virtualHostManager;

    /**
     * Directory where all the Project deployment workspaces are created.
     * 
     * @plexus.configuration
     */
    protected String workingDirectory;

    /**
     * Default deplyment goal(s) to use in absence of any goal being specified
     * 
     * @plexus.configuration default-value="clean compile war:war cargo:start"
     */
    protected String defaultDeploymentGoals;

    public AbstractDeployer()
    {
        super();
    }

    /**
     * Creates a deployment workspace if none exists for the specified DeployableProject.
     * @return {@link DeploymentWorkspace} instance 
     * @throws Exception 
     */
    protected DeploymentWorkspace createDeploymentWorkspaceIfRequired( DeployerResource project )
        throws Exception
    {
        if ( !new File( workingDirectory, project.getLabel() ).exists() )
            return createDeploymentWorkspace( project );
        else
            return loadWorkspaceFromDescriptor( project.getLabel() );
    }

    /**
     * Creates a {@link DeploymentWorkspace} for the specified Project.
     * 
     * @param project
     * @param workspaceID
     * @return created {@link DeploymentWorkspace} instance 
     * @throws Exception 
     */
    protected DeploymentWorkspace createDeploymentWorkspace( DeployerResource project )
        throws Exception
    {
        DeploymentWorkspace workspace = new DeploymentWorkspace();
        String id = project.getLabel();
        File rootDir = new File( workingDirectory, project.getLabel() );
        // set absolute path for the workspace root directory.
        workspace.setRootDirectory( rootDir.getAbsolutePath() );
        createIfNonExistent( rootDir );
        createIfNonExistent( new File( workspace.getRootDirectory(), workspace.getTempDirectory() ) );
        createIfNonExistent( new File( workspace.getRootDirectory(), workspace.getWebappDirectory() ) );
        createIfNonExistent( new File( workspace.getRootDirectory(), workspace.getWebserverDirectory() ) );
        createIfNonExistent( new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() ) );
        persistWorkspaceDescriptor( project, workspace );
        // setup other properties for workspace;
        workspace = loadWorkspaceFromDescriptor( id );
        return workspace;
    }

    /**
     * @param dir
     */
    private void createIfNonExistent( File dir )
    {
        if ( !dir.exists() )
        {
            getLogger().info( "Creating Deployment workspace dir: " + dir.getAbsolutePath() );
            FileUtils.mkdir( dir.getAbsolutePath() );
        }
    }

    /**
     * Persists DeploymentWorkspace for the Project to XML.
     * 
     * @param workspace
     */
    private void persistWorkspaceDescriptor( DeployerResource project, DeploymentWorkspace workspace )
    {
        String LS = System.getProperty( "line.separator" );

        try
        {
            File workspaceDesc = new File( workspace.getRootDirectory(), "workspace.xml" );
            FileWriter writer = new FileWriter( workspaceDesc );
            XMLWriter w = new PrettyPrintXMLWriter( writer );
            w.startElement( ELT_WORKSPACE );

            w.startElement( ELT_ID );
            w.writeText( project.getLabel() );
            w.endElement();

            w.startElement( ELT_SCM_URL );
            w.writeText( project.getScmURL() );
            w.endElement();

            w.startElement( ELT_SCM_USERNAME );
            w.writeText( project.getScmUsername() );
            w.endElement();

            w.startElement( ELT_SCM_PASSWORD );
            w.writeText( project.getScmPassword() );
            w.endElement();

            w.startElement( ELT_ROOT_DIRECTORY );
            w.writeText( workspace.getRootDirectory() );
            w.endElement();

            w.startElement( ELT_TEMP_DIRECTORY );
            w.writeText( workspace.getTempDirectory() );
            w.endElement();

            w.startElement( ELT_WEBAPP_DIRECTORY );
            w.writeText( workspace.getWebappDirectory() );
            w.endElement();

            w.startElement( ELT_WEBSERVER_DIRECTORY );
            w.writeText( workspace.getWebserverDirectory() );
            w.endElement();

            w.startElement( ELT_WORKING_DIRECTORY );
            w.writeText( workspace.getWorkingDirectory() );
            w.endElement();

            // close workspace element
            w.endElement();

            writer.write( LS );
            writer.close();
        }
        catch ( Exception e )
        {
            getLogger().error( "Error persisting Workspace Descriptor", e );
        }
    }

    /**
     * Reads the XML descriptor for the specified Project and creates a
     * {@link DeploymentWorkspace} instance from it.
     * 
     * @param workspaceId String identifier for the workspace for which to
     *            source the Workspace descriptor for.
     * @return {@link DeploymentWorkspace} instance.
     * @throws Exception
     */
    protected DeploymentWorkspace loadWorkspaceFromDescriptor( String workspaceId )
        throws Exception
    {
        File desc = new File( new File( workingDirectory, workspaceId ), "workspace.xml" );
        if ( !desc.exists() )
        {
            getLogger().error( "Workspace descriptor not found for workspace Id: " + workspaceId );
            throw new Exception( "Workspace descriptor not found for workspace Id: " + workspaceId );
        }
        FileReader reader = new FileReader( desc );
        // root element is <workspace>
        Xpp3Dom eltWorkSpace = Xpp3DomBuilder.build( reader );
        String id = eltWorkSpace.getChild( ELT_ID ).getValue();
        String scmURL = eltWorkSpace.getChild( ELT_SCM_URL ).getValue();
        String scmUsername = eltWorkSpace.getChild( ELT_SCM_USERNAME ).getValue();
        String scmPassword = eltWorkSpace.getChild( ELT_SCM_PASSWORD ).getValue();
        String rootDir = eltWorkSpace.getChild( ELT_ROOT_DIRECTORY ).getValue();
        String tmpDir = eltWorkSpace.getChild( ELT_TEMP_DIRECTORY ).getValue();
        String webserverDir = eltWorkSpace.getChild( ELT_WEBSERVER_DIRECTORY ).getValue();
        String webappDir = eltWorkSpace.getChild( ELT_WEBAPP_DIRECTORY ).getValue();
        String workingDir = eltWorkSpace.getChild( ELT_WORKING_DIRECTORY ).getValue();

        DeploymentWorkspace workspace = new DeploymentWorkspace();
        workspace.setLabel( id );
        workspace.setScmURL( scmURL );
        workspace.setScmUsername( scmUsername );
        workspace.setScmPassword( scmPassword );
        workspace.setRootDirectory( rootDir );
        workspace.setTempDirectory( tmpDir );
        workspace.setWebappDirectory( webappDir );
        workspace.setWebserverDirectory( webserverDir );
        workspace.setWebappDirectory( webappDir );
        workspace.setWorkingDirectory( workingDir );

        return workspace;
    }

    /**
     * Return an {@link ScmRepository} from the SCM URL.
     * 
     * @param project
     * @return
     * @throws ScmRepositoryException
     * @throws NoSuchScmProviderException
     */
    protected ScmRepository getScmRepository( DeployerResource project )
        throws ScmRepositoryException, NoSuchScmProviderException
    {
        ScmRepository repository = scmManager.makeScmRepository( project.getScmURL().trim() );

        repository.getProviderRepository().setPersistCheckout( true );

        if ( !StringUtils.isEmpty( project.getScmUsername() ) )
        {
            repository.getProviderRepository().setUser( project.getScmUsername() );

            if ( !StringUtils.isEmpty( project.getScmPassword() ) )
            {
                repository.getProviderRepository().setPassword( project.getScmPassword() );
            }
            else
            {
                repository.getProviderRepository().setPassword( "" );
            }
        }

        return repository;
    }

    /**
     * Service method to translate and adjust the directory paths for the
     * Virtual Host configuration to be relative from the Deployment workspaces
     * ROOT dir.
     * <p>
     * Also deletes any existing vhost resources.
     * 
     * @param config
     * @param workspace
     */
    protected void resetVirtualHostDirectory( VirtualHostConfiguration config, DeploymentWorkspace workspace )
    {
        if ( config.getVhostDirectory().startsWith( "/" ) )
        {
            config.setVhostDirectory( StringUtils.replace( workspace.getRootDirectory() + config.getVhostDirectory(),
                                                           "\\", "/" ) );
        }
        else
        {
            config.setVhostDirectory( StringUtils.replace( workspace.getRootDirectory() + "/"
                + config.getVhostDirectory(), "\\", "/" ) );
        }
        if ( config.getVhostLogDirectory().startsWith( "/" ) )
        {
            config.setVhostLogDirectory( StringUtils.replace( workspace.getRootDirectory()
                + config.getVhostLogDirectory(), "\\", "/" ) );
        }
        else
        {
            config.setVhostLogDirectory( StringUtils.replace( workspace.getRootDirectory() + "/"
                + config.getVhostLogDirectory(), "\\", "/" ) );
        }
    }

    /**
     * Reads the pom.xml from the checked out project and returns a
     * {@link MavenProject} instance.
     * 
     * @param project
     * @return
     */
    protected MavenProject getMavenProjectForCheckedoutProject( DeployableProject project )
    {
        MavenProject mavenProject = null;
        // Build Maven Project
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try
        {
            File checkoutDir = checkoutProjectIfRequired( project );
            File pomXml = new File( checkoutDir, "pom.xml" );
            if ( !pomXml.exists() )
                throw new Exception( "No Maven Project descriptor available for checked out project under: "
                    + checkoutDir.getAbsolutePath() );
            // getLogger ().info ("Reading pom.xml located at: " +
            // pomXml.getAbsolutePath ());
            Model model = mavenReader.read( new FileReader( pomXml ), true );
            mavenProject = new MavenProject( model );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            getLogger().error( e.getMessage() );
        }

        return mavenProject;
    }

    /**
     * Checks out the project if its not yet checked out and returns the Project
     * Check out directory.
     * 
     * @param project
     * @return Check out directory where the project was checked out to.
     * @throws Exception
     */
    protected File checkoutProjectIfRequired( DeployableProject project )
        throws Exception
    {
        DeploymentWorkspace workspace = loadWorkspaceFromDescriptor( project.getLabel() );
        project.setScmUsername( workspace.getScmUsername() );
        project.setScmPassword( workspace.getScmPassword() );
        project.setScmURL( workspace.getScmURL() );

        File workspaceWorkingDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
        String scmTag = null != project.getScmTag() ? project.getScmTag() : "HEAD";

        File checkoutDir = new File( workspaceWorkingDir, scmTag );

        // check out project if it is not checked out yet
        if ( !checkoutDir.exists() )
        {
            getLogger().info( "No checked out dir found for Project: " + project.getLabel() + " , version: " + scmTag );
            checkoutProject( project );
        }
        return checkoutDir;
    }

    /**
     * Service method to convert a String to a List
     * 
     * @param list
     * @param delim
     * @return
     */
    protected List string2List( String list, char delim )
    {
        List v = new ArrayList();

        if ( list == null )
            return v;

        int s = 0, l = list.length(), i = -1;

        while ( ( s < l ) && ( i = list.indexOf( delim, s ) ) != -1 )
        {
            if ( s != i )
                v.add( list.substring( s, i ) );
            s = i + 1;
        }
        if ( s < l )
            v.add( list.substring( s, l ) );

        return v;
    }

    /**
     * Returns <code>true</code> if there exists cargo plugin configuration in
     * the Maven POM.xml which we need to deploy to appserver.
     * 
     * @return
     */
    protected boolean isCargoPluginConfigurationInPOM( DeployableProject project )
        throws Exception
    {
        MavenProject mavenProject = getMavenProjectForCheckedoutProject( project );
        List pomBuildPlugins = mavenProject.getBuildPlugins();
        for ( Iterator it = pomBuildPlugins.iterator(); it.hasNext(); )
        {
            Plugin plugin = (Plugin) it.next();
            String groupId = plugin.getGroupId();
            String artifactId = plugin.getArtifactId();
            if ( groupId.equals( GROUP_ID_ORG_CODEHAUS_CARGO ) && artifactId.equals( ARTIFACT_ID_CARGO_MAVEN_2_PLUGIN ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Service method that sets up properties for a {@link DeployableProject}
     * instance.
     * 
     * @param project
     * @param property
     * @param value
     */
    private void assignProjectProperty( DeployerResource project, ProjectProperties property, String value )
    {
        if ( property == ProjectProperties.PROP_LABEL )
            project.setLabel( value );
        if ( property == ProjectProperties.PROP_SCM_URL )
            project.setScmURL( value );
        if ( property == ProjectProperties.PROP_SCM_USER )
            project.setScmUsername( value );
        if ( property == ProjectProperties.PROP_SCM_PASSWORD )
            project.setScmPassword( value );
        if ( property == ProjectProperties.PROP_SCM_TAG )
            project.setScmTag( value );
    }

}