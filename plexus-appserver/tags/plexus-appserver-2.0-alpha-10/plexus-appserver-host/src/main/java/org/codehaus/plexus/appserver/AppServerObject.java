package org.codehaus.plexus.appserver;

import org.codehaus.plexus.appserver.deploy.DeploymentException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.Expand;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public abstract class AppServerObject
    extends AbstractLogEnabled
{
    protected void expand( File source, File outputDirectory, boolean overwrite )
        throws DeploymentException
    {
        Expand expander = new Expand();

        expander.setDest( outputDirectory );

        expander.setOverwrite( overwrite );

        expander.setSrc( source );

        try
        {
            expander.execute();
        }
        catch ( Exception e )
        {
            throw new DeploymentException( "Unable to extract " + source + " to " + outputDirectory + ".", e );
        }
    }
}
