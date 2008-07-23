/**
 * 
 */
package org.codehaus.plexus.xsiter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.xsiter.deployer.Deployer;
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
        return loadWorkspaceFromDescriptor( desc );
    }

    /**
     * Given the absolute path to the workspace descriptor (workspace.xml), the descriptor
     * is read and an instance of {@link DeploymentWorkspace} is created and returned
     * @param descriptorFile
     * @return
     * @throws FileNotFoundException
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static DeploymentWorkspace loadWorkspaceFromDescriptor( File descriptorFile )
        throws FileNotFoundException, XmlPullParserException, IOException
    {
        FileReader reader = new FileReader( descriptorFile );
        // root element is <workspace>
        Xpp3Dom eltWorkSpace = Xpp3DomBuilder.build( reader );
        String id = readElement( eltWorkSpace, Deployer.ELT_ID );
        String scmURL = readElement( eltWorkSpace, Deployer.ELT_SCM_URL );
        String scmUsername = readElement( eltWorkSpace, Deployer.ELT_SCM_USERNAME );
        String scmPassword = readElement( eltWorkSpace, Deployer.ELT_SCM_PASSWORD );
        String rootDir = readElement( eltWorkSpace, Deployer.ELT_ROOT_DIRECTORY );
        String tmpDir = readElement( eltWorkSpace, Deployer.ELT_TEMP_DIRECTORY );
        String webserverDir = readElement( eltWorkSpace, Deployer.ELT_WEBSERVER_DIRECTORY );
        String webappDir = readElement( eltWorkSpace, Deployer.ELT_WEBAPP_DIRECTORY );
        String workingDir = readElement( eltWorkSpace, Deployer.ELT_WORKING_DIRECTORY );

        DeploymentWorkspace workspace = new DeploymentWorkspace();
        workspace.setLabel( id );
        // check for an SCM Url
        if ( null != scmURL )
        {
            workspace.setScmURL( scmURL );
            workspace.setScmUsername( scmUsername );
            workspace.setScmPassword( scmPassword );
        }
        workspace.setRootDirectory( rootDir );
        workspace.setTempDirectory( tmpDir );
        workspace.setWebappDirectory( webappDir );
        workspace.setWebserverDirectory( webserverDir );
        workspace.setWebappDirectory( webappDir );
        workspace.setWorkingDirectory( workingDir );
        reader.close();

        return workspace;
    }

    /**
     * Reads and returns the values for specified element from the passed in 
     * DOM.<p>
     * Checks if element actually exists, if not a <code>null</code> value is returned.
     * 
     * @param dom
     * @param elementName
     * @return  value of element as String or <code>null</code> if the element 
     *          was not found.
     */
    private static String readElement( Xpp3Dom dom, String elementName )
    {
        Xpp3Dom child = dom.getChild( elementName );
        if ( null != child )
            return child.getValue();
        return null;
    }

    /**
     * Persists {@link DeploymentWorkspace} for the Project to XML.
     * @param deployerWorkingDir Working directory where all deployment workspaces are created.
     * @param workspace
     */
    public static void persistWorkspaceDescriptor( File deployerWorkingDir, DeploymentWorkspace workspace )
    {
        String LS = System.getProperty( "line.separator" );

        try
        {
            File workspaceDir = new File( deployerWorkingDir, workspace.getId() );
            File workspaceDesc = new File( workspaceDir, "workspace.xml" );

            FileWriter writer = new FileWriter( workspaceDesc );
            XMLWriter w = new PrettyPrintXMLWriter( writer );

            w.startElement( Deployer.ELT_WORKSPACE );
            writeElement( w, Deployer.ELT_ID, workspace.getId(), false );

            // write out SCM info if there was an SCM URL
            if ( null != workspace.getScmURL() && !workspace.getScmURL().trim().equals( "" ) )
            {
                writeElement( w, Deployer.ELT_SCM_URL, workspace.getScmURL(), true );
                writeElement( w, Deployer.ELT_SCM_USERNAME, workspace.getScmUsername(), true );
                writeElement( w, Deployer.ELT_SCM_PASSWORD, workspace.getScmPassword(), true );
            }
            writeElement( w, Deployer.ELT_ROOT_DIRECTORY, workspaceDir.getAbsolutePath(), false );
            writeElement( w, Deployer.ELT_TEMP_DIRECTORY, workspace.getTempDirectory(), false );
            writeElement( w, Deployer.ELT_WEBAPP_DIRECTORY, workspace.getWebappDirectory(), false );
            writeElement( w, Deployer.ELT_WEBSERVER_DIRECTORY, workspace.getWebserverDirectory(), false );
            writeElement( w, Deployer.ELT_WORKING_DIRECTORY, workspace.getWorkingDirectory(), false );

            // close workspace element
            w.endElement();

            writer.write( LS );
            writer.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Writes out XML elements.
     * @param w 
     * @param eltName Element to be written out.
     * @param value Value for for the element.
     * @param skipNullValue If <code>true</code> the element is not written if its 
     *        value is <code>null</code>. 
     */
    private static void writeElement( XMLWriter w, String eltName, String value, boolean skipNullValue )
    {
        if ( skipNullValue && null == value )
            return;
        w.startElement( eltName );
        w.writeText( value );
        w.endElement();
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
     * @param deployerWorkingDirectory 
     * @param workspace
     * 
     * @return
     */
    public static MavenProject getMavenProjectForCheckedoutProject( File deployerWorkingDirectory,
                                                                    DeploymentWorkspace workspace )
    {
        MavenProject mavenProject = null;
        // Build Maven Project
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try
        {

            File checkoutDir = getWorkspaceCheckoutDirectory( deployerWorkingDirectory, workspace );
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
            e.printStackTrace();
        }

        return mavenProject;
    }

    /**
     * @param deployerWorkingDirectory
     * @param workspace
     * @return
     */
    public static File getWorkspaceCheckoutDirectory( File deployerWorkingDirectory, DeploymentWorkspace workspace )
    {
        return new File( deployerWorkingDirectory, workspace.getId() + "/" + workspace.getWorkingDirectory() + "/"
            + workspace.getScmTag() );
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
