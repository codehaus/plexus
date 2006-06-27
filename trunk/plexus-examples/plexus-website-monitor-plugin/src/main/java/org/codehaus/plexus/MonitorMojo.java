/**
 * 
 */
package org.codehaus.plexus;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.tutorial.WebsiteMonitor;

/**
 * A Mojo that monitors a given list of websites.
 * 
 * @version $Id:$
 * @goal monitor
 */
public class MonitorMojo
    extends AbstractMojo
{

    /**
     * The website monitor component instance that will be injected 
     * by the Plexus runtime.
     * @component role-hint="ftp"
     */
    private WebsiteMonitor monitor;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        // TODO Auto-generated method stub
        
    }

}
