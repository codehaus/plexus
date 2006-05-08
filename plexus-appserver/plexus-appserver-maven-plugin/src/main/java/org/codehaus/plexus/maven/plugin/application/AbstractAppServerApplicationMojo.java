package org.codehaus.plexus.maven.plugin.application;

import org.codehaus.plexus.maven.plugin.AbstractAppServerMojo;
import org.codehaus.plexus.builder.service.ServiceBuilder;
import org.codehaus.plexus.builder.application.ApplicationBuilder;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public abstract class AbstractAppServerApplicationMojo
    extends AbstractAppServerMojo
{
    /**
     * @parameter expression="${project.build.directory}/plexus-application"
     * @required
     */
    protected File applicationAssemblyDirectory;

    /**
     * @parameter expression="${component.org.codehaus.plexus.applicationBuilder.application.ApplicationBuilder}"
     * @required
     */
    protected ApplicationBuilder applicationBuilder;
}
