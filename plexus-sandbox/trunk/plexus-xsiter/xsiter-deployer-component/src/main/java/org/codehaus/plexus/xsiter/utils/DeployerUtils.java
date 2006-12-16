/**
 * 
 */
package org.codehaus.plexus.xsiter.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.xsiter.deployer.Deployer;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;
import org.codehaus.plexus.xsiter.deployer.model.DeployerResource;
import org.codehaus.plexus.xsiter.deployer.model.DeploymentWorkspace;

/**
 * Utility methods.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 */
public class DeployerUtils
{

    /**
     * Reads the XML descriptor for the specified Project and creates a
     * {@link DeploymentWorkspace} instance from it.
     * @param deployerWorkingDirectory 
     * @param workspaceId String identifier for the workspace for which to
     *            source the Workspace descriptor for.
     * 
     * @return {@link DeploymentWorkspace} instance.
     * @throws Exception
     */
    public static DeploymentWorkspace loadWorkspaceFromDescriptor( File deployerWorkingDirectory, String workspaceId )
        throws Exception
    {
        File desc = new File( new File( deployerWorkingDirectory, workspaceId ), "workspace.xml" );
        if ( !desc.exists() )
        {
            //getLogger().error( "Workspace descriptor not found for workspace Id: " + workspaceId );
            throw new Exception( "Workspace descriptor not found for workspace Id: " + workspaceId );
        }
        FileReader reader = new FileReader( desc );
        // root element is <workspace>
        Xpp3Dom eltWorkSpace = Xpp3DomBuilder.build( reader );
        String id = eltWorkSpace.getChild( Deployer.ELT_ID ).getValue();
        String scmURL = eltWorkSpace.getChild( Deployer.ELT_SCM_URL ).getValue();
        String scmUsername = eltWorkSpace.getChild( Deployer.ELT_SCM_USERNAME ).getValue();
        String scmPassword = eltWorkSpace.getChild( Deployer.ELT_SCM_PASSWORD ).getValue();
        String rootDir = eltWorkSpace.getChild( Deployer.ELT_ROOT_DIRECTORY ).getValue();
        String tmpDir = eltWorkSpace.getChild( Deployer.ELT_TEMP_DIRECTORY ).getValue();
        String webserverDir = eltWorkSpace.getChild( Deployer.ELT_WEBSERVER_DIRECTORY ).getValue();
        String webappDir = eltWorkSpace.getChild( Deployer.ELT_WEBAPP_DIRECTORY ).getValue();
        String workingDir = eltWorkSpace.getChild( Deployer.ELT_WORKING_DIRECTORY ).getValue();

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
     * Persists DeploymentWorkspace for the Project to XML.
     * 
     * @param workspace
     */
    public static void persistWorkspaceDescriptor( DeploymentWorkspace workspace )
    {
        String LS = System.getProperty( "line.separator" );

        try
        {
            File workspaceDesc = new File( workspace.getRootDirectory(), "workspace.xml" );
            FileWriter writer = new FileWriter( workspaceDesc );
            XMLWriter w = new PrettyPrintXMLWriter( writer );
            w.startElement( Deployer.ELT_WORKSPACE );

            w.startElement( Deployer.ELT_ID );
            w.writeText( workspace.getLabel() );
            w.endElement();

            w.startElement( Deployer.ELT_SCM_URL );
            w.writeText( workspace.getScmURL() );
            w.endElement();

            w.startElement( Deployer.ELT_SCM_USERNAME );
            w.writeText( workspace.getScmUsername() );
            w.endElement();

            w.startElement( Deployer.ELT_SCM_PASSWORD );
            w.writeText( workspace.getScmPassword() );
            w.endElement();

            w.startElement( Deployer.ELT_ROOT_DIRECTORY );
            w.writeText( workspace.getRootDirectory() );
            w.endElement();

            w.startElement( Deployer.ELT_TEMP_DIRECTORY );
            w.writeText( workspace.getTempDirectory() );
            w.endElement();

            w.startElement( Deployer.ELT_WEBAPP_DIRECTORY );
            w.writeText( workspace.getWebappDirectory() );
            w.endElement();

            w.startElement( Deployer.ELT_WEBSERVER_DIRECTORY );
            w.writeText( workspace.getWebserverDirectory() );
            w.endElement();

            w.startElement( Deployer.ELT_WORKING_DIRECTORY );
            w.writeText( workspace.getWorkingDirectory() );
            w.endElement();

            // close workspace element
            w.endElement();

            writer.write( LS );
            writer.close();
        }
        catch ( Exception e )
        {
            //getLogger().error( "Error persisting Workspace Descriptor", e );
        }
    }

    /**
     * Service method to convert a String to a List
     * 
     * @param list
     * @param delim
     * @return
     */
    public static List string2List( String list, char delim )
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
     * Reads the pom.xml from the checked out project and returns a {@link MavenProject} instance.
     * 
     * @param project
     * @param deployerWorkingDirectory 
     * @return
     */
    protected MavenProject getMavenProjectForCheckedoutProject( DeployableProject project, File deployerWorkingDirectory )
    {
        MavenProject mavenProject = null;
        // Build Maven Project
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try
        {
            File checkoutDir = new File( new File( deployerWorkingDirectory, project.getLabel() ), "working/"
                + project.getScmTag() );
            File pomXml = new File( checkoutDir, "pom.xml" );
            if ( !pomXml.exists() )
                throw new Exception( "No Maven Project descriptor available for checked out project under: "
                    + checkoutDir.getAbsolutePath() );
            Model model = mavenReader.read( new FileReader( pomXml ), true );
            mavenProject = new MavenProject( model );
        }
        catch ( Exception e )
        {
            // getLogger().error( e.getMessage(), e );
        }

        return mavenProject;
    }

    /**
     * Creates a File/Directory resource if it does not exists.
     * @param dir
     */
    public static void createIfNonExistent( File dir )
    {
        if ( !dir.exists() )
        {
            FileUtils.mkdir( dir.getAbsolutePath() );
        }
    }
}
