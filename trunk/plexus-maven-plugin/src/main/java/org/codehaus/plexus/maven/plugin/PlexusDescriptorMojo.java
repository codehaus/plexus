package org.apache.maven.plugin.plexus;

/*
 * LICENSE
 */

import org.apache.maven.plugin.AbstractPlugin;
import org.apache.maven.plugin.PluginExecutionRequest;
import org.apache.maven.plugin.PluginExecutionResponse;

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
 *  expression="#project.path"
 *  description=""
 * @parameter
 *  name="outputDirectory"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#project.build.outputDirectory"
 *  description=""
 * @parameter
 *  name="componentDescriptorCreator"
 *  type="String"
 *  required="true"
 *  validator=""
 *  expression="#component.org.codehaus.plexus.cdc.ComponentDescriptorCreator"
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

        String outputDirectory = (String) request.getParameter( "outputDirectory" );

        ComponentDescriptorCreator creator = (ComponentDescriptorCreator) request.getParameter( "componentDescriptorCreator" );

        creator.setBasedir( basedir );

        creator.setDestDir( outputDirectory );

        creator.execute();
    }
}
