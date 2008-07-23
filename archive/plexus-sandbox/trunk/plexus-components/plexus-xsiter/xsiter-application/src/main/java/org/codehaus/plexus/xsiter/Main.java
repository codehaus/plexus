package org.codehaus.plexus.xsiter;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.xsiter.deployer.DeployerApplication;
import org.codehaus.plexus.xsiter.deployer.model.DeployableProject;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @dated: 22/09/2006, 4:45:57 PM
 */
public class Main
{

    /**
     * List of valid commands.
     */
    private static final String[] validCommands = new String[] {
        "setup",
        "add-vhost",
        "checkout",
        "deploy",
        "deploy-custom",
        "remove" };

    /**
     * @param args
     * @throws Exception
     */
    public static void main( String[] args )
        throws Exception
    {

        Embedder plexus = new Embedder();
        plexus.start();

        Prompter prompter = (Prompter) plexus.getContainer().lookup( Prompter.ROLE );
        List list = Arrays.asList( validCommands );
        String choice = null;
        while ( null == choice || choice.trim().equals( "" ) )
        {
            choice = prompter.prompt( "Choose a command", list, (String) list.get( 0 ) );
        }

        String projectID = null;
        while ( null == projectID || projectID.trim().equals( "" ) )
        {
            projectID = prompter.prompt( "Enter a project ID" );
        }

        DeployerApplication deployer = (DeployerApplication) plexus.getContainer().lookup( DeployerApplication.ROLE );

        if ( choice.equalsIgnoreCase( "setup" ) )
        {
            Properties props = new Properties();
            props.setProperty( DeployableProject.ProjectProperties.PROP_LABEL.getName(), projectID );
            deployer.addProject( props );
        }
        else if ( choice.equalsIgnoreCase( "add-vhost" ) )
        {
            deployer.addVirtualHost( projectID );
        }
        else if ( choice.equalsIgnoreCase( "deploy" ) )
        {
            deployer.deployProject( projectID );
        }
        else if ( choice.equalsIgnoreCase( "deploy-custom" ) )
        {
            String goals = null;
            while ( null == goals || goals.trim().equals( "" ) )
            {
                goals = prompter.prompt( "Enter goals to use for custom deploy" );
            }
            String tag = prompter.prompt( "Enter SCM tag to checkout" );
            if ( null == tag || tag.trim().equals( "" ) )
                tag = null;
            deployer.deployProject( projectID, goals, tag );
        }
        else if ( choice.equals( "checkout" ) )
        {
            String tag = prompter.prompt( "Enter SCM tag to checkout" );
            if ( null == tag || tag.trim().equals( "" ) )
                tag = null;
            deployer.checkoutProject( projectID, tag );
        }
        else if ( choice.equals( "remove" ) )
        {
            deployer.removeProject( projectID );
        }

    }

}
