/**
 * 
 */
package org.codehaus.plexus;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.tutorial.WebsiteMonitor;

import java.util.Iterator;
import java.util.List;

/**
 * A Mojo that monitors a given list of websites.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 * @goal monitor
 */
public class MonitorMojo
    extends AbstractMojo
{

    /**
     * List of websites to monitor.
     * @parameter 
     */
    private List websites;

    /**
     * The website monitor component instance that will be injected 
     * by the Plexus runtime.
     * @component 
     */
    private WebsiteMonitor monitor;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( !hasWebsites() )
        {
            if ( getLog().isWarnEnabled() )
                getLog().warn( "No websites specified to be monitored." );
            return;
        }

        if ( getLog().isDebugEnabled() )
            showMonitoredWebsites();

        monitor.addWebsites( websites );
        try
        {
            monitor.monitor();
        }
        catch ( Exception e )
        {
            if ( getLog().isErrorEnabled() )
                getLog().error( "Error monitoring websites.", e );
        }
    }

    /**
     * Displays the list of Monitored websites (if debug was ON).
     */
    private void showMonitoredWebsites()
    {
        getLog().debug( "Monitoring following websites:" );
        for ( Iterator it = websites.iterator(); it.hasNext(); )
        {
            String website = (String) it.next();
            getLog().debug( "\t" + website );
        }
    }

    /**
     * Determines if websites were specified to the Mojo.
     * 
     * @return <code>true</code> if there were websites specified.
     */
    private boolean hasWebsites()
    {
        return ( null != websites && websites.size() > 0 );
    }

    /**
     * Returns the list of websites.<p>
     * <em>Not public API. For unit tests only.</em>
     * 
     * @return List of websites specified via Mojo configuration.
     */
    protected List getWebsites()
    {
        return this.websites;
    }

    /**
     * Returns the {@link WebsiteMonitor} component instance.<p>
     * <em>Not public API. For unit tests only.</em>
     * 
     * @return the {@link WebsiteMonitor} instance.
     */
    protected WebsiteMonitor getMonitor()
    {
        return monitor;
    }

}
