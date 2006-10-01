package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.PlexusApplicationConstants;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author Jason van Zyl
 */
public class ProcessAppMetadataPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {
        Reader reader;

        JarFile jarFile = null;

        try
        {
            jarFile = new JarFile( context.getPar() );

            ZipEntry entry = jarFile.getEntry( PlexusApplicationConstants.METADATA_FILE );

            if ( entry == null )
            {
                throw new AppDeploymentException( "The Plexus appserver jar is missing it's metadata file '" +
                    PlexusApplicationConstants.METADATA_FILE + "'." );
            }

            reader = new InputStreamReader( jarFile.getInputStream( entry ) );
        }
        catch ( IOException e )
        {
            throw new AppDeploymentException( "Error reading application JAR file: " + jarFile, e );
        }

        Xpp3Dom dom;

        try
        {
            dom = Xpp3DomBuilder.build( reader );
        }
        catch ( XmlPullParserException e )
        {
            throw new AppDeploymentException( "Error parsing application configurator file.", e );
        }
        catch ( IOException e )
        {
            throw new AppDeploymentException( "Error reading application configurator file.", e );
        }

        String appId = dom.getChild( "name" ).getValue();

        if ( StringUtils.isEmpty( appId ) )
        {
            throw new AppDeploymentException( "Missing 'name' element in the application metadata file." );
        }

        File appDir = new File( context.getApplicationsDirectory(), appId );

        context.setAppDir( appDir );

        // Put the found application id into the context.
        context.setApplicationId( appId );
    }
}
