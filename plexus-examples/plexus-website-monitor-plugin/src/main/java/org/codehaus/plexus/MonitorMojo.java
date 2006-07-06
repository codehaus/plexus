/**
 * 
 */
package org.codehaus.plexus;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.tutorial.WebsiteMonitor;

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
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

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

    }

}
