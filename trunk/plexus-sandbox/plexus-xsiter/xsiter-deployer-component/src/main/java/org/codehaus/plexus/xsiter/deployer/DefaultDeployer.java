package org.codehaus.plexus.xsiter.deployer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployedProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployerResource;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject.ProjectProperties;
import org.codehaus.plexus.xsiter.vhost.VirtualHostConfiguration;
import org.codehaus.plexus.xsiter.vhost.VirtualHostManager;

/**
 * Concrete implementation of a {@link Deployer} Role.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id: DefaultDeployer.java,v 1.1 2006/09/14 05:11:37 rahul
 *          Exp $
 * @plexus.component
 */
public class DefaultDeployer
    extends AbstractLogEnabled
    implements Deployer
{

    /**
     * Cargo Maven2 Plugin artifact ID.
     */
    private static final String ARTIFACT_ID_CARGO_MAVEN_2_PLUGIN = "cargo-maven2-plugin";

    /**
     * Cargo group ID.
     */
    private static final String GROUP_ID_ORG_CODEHAUS_CARGO = "org.codehaus.cargo";

    /**
     * Date Formatter.
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat( "dd-MM-yyyy, HH:mm:ss" );

    /**
     * Source Control Manager.
     * 
     * @plexus.requirement
     */
    private ScmManager scmManager;

    /**
     * Component to Manage Virtual Hosts for a Project.
     * 
     * @plexus.requirement
     */
    private VirtualHostManager virtualHostManager;

    /**
     * Directory where all the Project deployment workspaces are created.
     * 
     * @plexus.configuration
     */
    private String workingDirectory;

    /**
     * Default deplyment goal(s) to use in absence of any goal being specified
     * 
     * @plexus.configuration default-value="clean compile war:war cargo:start"
     */
    private String defaultDeploymentGoals;

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#addProject(DeployableProject)
     */
    public void addProject( DeployableProject project )
        throws Exception
    {
        if ( new File( workingDirectory, project.getLabel() ).exists() )
            throw new Exception( "Workspace with ID: '" + project.getLabel() + "' already exists." );
        createDeploymentWorkspace( project );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#removeProject(DeployableProject)
     */
    public void removeProject( DeployableProject project )
        throws Exception
    {
        File targetDir = new File( workingDirectory, project.getLabel() );
        if ( targetDir.exists() )
        {
            FileUtils.deleteDirectory( targetDir );
        }
        else
        {
            getLogger().info( "No Deployment workspace found for Project '" + project.getLabel() + "'" );
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#getDeploymentWorkspace(DeployableProject)
     */
    public DeploymentWorkspace getDeploymentWorkspace( DeployableProject project )
        throws Exception
    {
        return loadWorkspaceFromDescriptor( project.getLabel() );
    }

    /**
     * @return the workingDirectory
     */
    public String getWorkingDirectory()
    {
        return workingDirectory;
    }

    /**
     * @param workingDirectory the workingDirectory to set
     */
    public void setWorkingDirectory( String workingDirectory )
    {
        this.workingDirectory = workingDirectory;
    }

    /**
     * @return the defaultDeploymentGoals
     */
    public String getDefaultDeploymentGoals()
    {
        return defaultDeploymentGoals;
    }

    /**
     * @param defaultDeploymentGoals the defaultDeploymentGoals to set
     */
    public void setDefaultDeploymentGoals( String defaultDeploymentGoals )
    {
        this.defaultDeploymentGoals = defaultDeploymentGoals;
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#addVirtualHost(DeployableProject)
     */
    public void addVirtualHost( DeployableProject project )
        throws Exception
    {
        createDeploymentWorkspaceIfRequired( project );
        // Updated Implementation, use configuration from pom.xml
        MavenProject mavenProject = getMavenProjectForCheckedoutProject( project );
        Properties props = mavenProject.getProperties();

        String vhostsConfig = (String) props.get( VirtualHostManager.ELT_POM_VHOSTS_CONFIG );
        getLogger().info( "Creating Virtual Host Configuration from XML snippet:\n" + vhostsConfig );
        StringReader sr = new StringReader( vhostsConfig );
        List list = virtualHostManager.loadVirtualHostConfigurations( sr );
        boolean appendVhost = false;
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            VirtualHostConfiguration config = (VirtualHostConfiguration) it.next();
            DeploymentWorkspace workspace = loadWorkspaceFromDescriptor( project.getLabel() );
            File workspaceWorkingDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
            File checkoutDir = new File( workspaceWorkingDir, project.getScmTag() );
            getLogger().info( "Adding Virtual Host for config ID: " + config.getId() );
            // Adjust the Velocity 'file.resource.loader.path' property to the
            // checkout directory, so the Vhosts template could be found.
            Velocity.setProperty( Velocity.FILE_RESOURCE_LOADER_PATH, checkoutDir.getAbsolutePath() );
            // Adjust other directory paths on the VirtualHostConfiguration
            // instance
            resetVirtualHostDirectory( config, workspace );
            virtualHostManager.addVirtualHost( config, appendVhost );
            // subsequent vhost configs to be appended
            if ( !appendVhost )
                appendVhost = true;
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#checkoutProject(DeployableProject)
     */
    public void checkoutProject( DeployableProject project )
        throws Exception
    {
        DeploymentWorkspace workspace = createDeploymentWorkspaceIfRequired( project );
        getLogger().info( "Checking out Project: " + project.getLabel() + ", Version: " + project.getScmTag() );

        // TODO: refactor/add a method that creates a Deployable Project
        // instance from a Workspace descriptor

        // sanity for SCM info, source from workspace
        project.setScmURL( workspace.getScmURL() );
        project.setScmUsername( workspace.getScmUsername() );
        project.setScmPassword( workspace.getScmPassword() );

        try
        {
            ScmRepository repository = getScmRepository( project );
            ScmResult result;
            synchronized ( this )
            {
                File checkoutDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
                try
                {
                    if ( checkoutDir.exists() )
                        FileUtils.cleanDirectory( checkoutDir );
                }
                catch ( IOException e )
                {
                    throw new Exception( "Could not clean directory : " + workspace.getWorkingDirectory(), e );
                }

                // check out to a tagged directory
                String tagDir = null != project.getScmTag() ? project.getScmTag() : "Head";
                ScmFileSet fileSet = new ScmFileSet( new File( checkoutDir, tagDir ) );
                result = scmManager.getProviderByRepository( repository ).checkOut( repository, fileSet,
                                                                                    project.getScmTag() );
            }

            if ( !result.isSuccess() )
            {
                getLogger().warn( "Command output: " + result.getCommandOutput() );
                getLogger().warn( "Provider message: " + result.getProviderMessage() );
            }
            else
            {
                getLogger().info( "Project checked out successfully." );
            }

            // return result;
        }
        catch ( Exception e )
        {
            throw new Exception( "Cannot checkout sources.", e );
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#deployProject(DeployableProject)
     */
    public DeployedProject deployProject( DeployableProject project )
        throws Exception
    {
        getLogger().info( "Using default deployment goals: " + defaultDeploymentGoals );
        // Deploy the checked out Project to Appserver/Webserver
        return deployProject( project, defaultDeploymentGoals );
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#deployProject(DeployableProject,
     *      java.lang.String)
     */
    public DeployedProject deployProject( DeployableProject project, String goals )
        throws Exception
    {
        createDeploymentWorkspaceIfRequired( project );
        // Deploy the checked out Project to Appserver/Webserver
        if ( isCargoPluginConfigurationInPOM( project ) )
        {
            buildProject( project, goals );
        }
        else
        {
            getLogger()
                .error( "Unable to deploy project to an Application server. (Missing Cargo Maven Plugin configuration)" );
            throw new Exception(
                                 "Unable to deploy project to an Application server. (Missing Cargo Maven Plugin configuration)" );
        }
        // TODO Add more stuff to deployed project
        return new DeployedProject();
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#buildProject(DeployableProject,
     *      java.lang.String)
     */
    public void buildProject( DeployableProject project, String goals )
        throws Exception
    {
        DeploymentWorkspace workspace = createDeploymentWorkspaceIfRequired( project );
        checkoutProjectIfRequired( project );
        File workspaceWorkingDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
        File checkoutDir = new File( workspaceWorkingDir, project.getScmTag() );

        if ( !checkoutDir.exists() )
            throw new Exception( "No check out directory exists, nothing to build" );

        // XXX: It would be nice to have an attempt to check that 'mvn' command
        // was on $PATH

        Commandline cmd = new Commandline();
        cmd.setExecutable( MAVEN_EXECUTABLE );
        List argsList = string2List( goals, ' ' );
        for ( Iterator it = argsList.iterator(); it.hasNext(); )
        {
            String arg = (String) it.next();
            cmd.createArgument().setValue( arg );
        }
        cmd.setWorkingDirectory( checkoutDir.getAbsolutePath() );
        getLogger().debug( "Command to run is: " + cmd.toString() );
        int exitValue = -1;
        try
        {
            exitValue = CommandLineUtils.executeCommandLine( cmd, new DefaultConsumer(), new DefaultConsumer() );
            getLogger().info( "Command returned exit value: " + exitValue );
            if ( exitValue != 0 )
            {
                getLogger().error( "Unable to execute command: '" + goals + "'" );
                throw new Exception( "Unable to execute command: '" + goals + "'" );
            }

            getLogger().debug( "Process ID : " + cmd.getPid() );
            FileWriter fw = new FileWriter( new File( workspaceWorkingDir, "pid.txt" ), true );
            fw.append( "# PID for goal(s): '" + goals + "' run on " + sdf.format( new Date() ) );
            fw.write( "\n" + cmd.getPid() );
            fw.close();

        }
        catch ( CommandLineException e )
        {
            getLogger().error( "Unable to execute command: '" + cmd.toString() + "'", e );
        }
    }

    /**
     * @see org.codehaus.plexus.xsiter.deployer.Deployer#updateProject(org.codehaus.plexus.xsiter.deployer.model.DeployableProject)
     */
    public void updateProject( DeployableProject project )
        throws Exception
    {
        DeploymentWorkspace workspace = createDeploymentWorkspaceIfRequired( project );
        getLogger().info( "Updating Project: " + project.getLabel() + ", Version: " + project.getScmTag() );

        // TODO: refactor/add a method that creates a Deployable Project
        // instance from a Workspace descriptor

        // sanity for SCM info, source from workspace
        project.setScmURL( workspace.getScmURL() );
        project.setScmUsername( workspace.getScmUsername() );
        project.setScmPassword( workspace.getScmPassword() );

        try
        {
            ScmRepository repository = getScmRepository( project );
            ScmResult result;
            synchronized ( this )
            {
                File checkoutDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
                try
                {
                    if ( checkoutDir.exists() )
                        FileUtils.cleanDirectory( checkoutDir );
                }
                catch ( IOException e )
                {
                    throw new Exception( "Could not clean directory : " + workspace.getWorkingDirectory(), e );
                }

                // check out to a tagged directory
                ScmFileSet fileSet = new ScmFileSet( new File( checkoutDir, project.getScmTag() ) );
                result = scmManager.getProviderByRepository( repository ).update( repository, fileSet,
                                                                                  project.getScmTag() );
            }

            if ( !result.isSuccess() )
            {
                getLogger().warn( "Command output: " + result.getCommandOutput() );
                getLogger().warn( "Provider message: " + result.getProviderMessage() );
            }
            else
            {
                getLogger().info( "Project checked out successfully." );
            }

            // return result;
        }
        catch ( Exception e )
        {
            throw new Exception( "Cannot checkout sources.", e );
        }
    }

    // Service methods
    /**
     * Creates a deployment workspace if none exists for the specified DeployableProject.
     * @return {@link DeploymentWorkspace} instance 
     * @throws Exception 
     */
    private DeploymentWorkspace createDeploymentWorkspaceIfRequired( DeployerResource project )
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
    private DeploymentWorkspace createDeploymentWorkspace( DeployerResource project )
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
    private ScmRepository getScmRepository( DeployerResource project )
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
    private void resetVirtualHostDirectory( VirtualHostConfiguration config, DeploymentWorkspace workspace )
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
    private File checkoutProjectIfRequired( DeployableProject project )
        throws Exception
    {
        DeploymentWorkspace workspace = loadWorkspaceFromDescriptor( project.getLabel() );
        project.setScmUsername( workspace.getScmUsername() );
        project.setScmPassword( workspace.getScmPassword() );
        project.setScmURL( workspace.getScmURL() );

        File workspaceWorkingDir = new File( workspace.getRootDirectory(), workspace.getWorkingDirectory() );
        File checkoutDir = new File( workspaceWorkingDir, project.getScmTag() );

        // check out project if it is not checked out yet
        if ( !checkoutDir.exists() )
        {
            getLogger().info(
                              "No checked out dir found for Project: " + project.getLabel() + " , version: "
                                  + project.getScmTag() );
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
    private List string2List( String list, char delim )
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
    private boolean isCargoPluginConfigurationInPOM( DeployableProject project )
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
