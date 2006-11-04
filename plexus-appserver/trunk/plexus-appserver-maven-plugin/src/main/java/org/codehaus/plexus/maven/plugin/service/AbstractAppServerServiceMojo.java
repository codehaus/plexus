package org.codehaus.plexus.maven.plugin.service;

import org.codehaus.plexus.builder.service.ServiceBuilder;
import org.codehaus.plexus.maven.plugin.AbstractAppServerMojo;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public abstract class AbstractAppServerServiceMojo
    extends AbstractAppServerMojo
{
    /**
     * @parameter expression="${project.build.directory}/plexus-service"
     * @required
     */
    protected File serviceAssemblyDirectory;

    /**
     * @parameter expression="${component.org.codehaus.plexus.builder.service.ServiceBuilder}"
     * @required
     */
    protected ServiceBuilder builder;

}
