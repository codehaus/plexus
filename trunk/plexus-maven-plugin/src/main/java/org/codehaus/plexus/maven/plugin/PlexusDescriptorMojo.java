package org.apache.maven.plugin.plexus;

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.cdc.ComponentDescriptorCreator;

/**
 * @goal descriptor
 *
 * @description Builds a plexus descriptor.
 *
 * @parameter
 *  name="basedir"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#basedir"
 *  description=""
 * @parameter
 *  name="project"
 *  type="org.apache.maven.project.MavenProject"
 *  required="true"
 *  validator=""
 *  expression="#project"
 *  description=""
 * @parameter
 *  name="output"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project.build.output"
 *  description=""
 * 
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusDescriptorMojo
    extends AbstractPlugin
{
    public void execute( PluginExecutionRequest request, PluginExecutionResponse response )
        throws Exception
    {
        String basedir = (String) request.getParameter( "basedir" );

        MavenProject project = (MavenProject) request.getParameter( "project" );

        String output = (String) request.getParameter( "output" );

        ComponentDescriptorCreator creator = new ComponentDescriptorCreator();

        creator.setBasedir( basedir );

        creator.setProject( project );

        creator.setDestDir( output + "/META-INF/plexus" );

        creator.execute();
    }
}
