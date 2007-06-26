package org.codehaus.plexus.appserver.application.deploy.lifecycle.phase;

import org.codehaus.plexus.appserver.PlexusApplicationConstants;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentContext;
import org.codehaus.plexus.appserver.application.deploy.lifecycle.AppDeploymentException;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.Map;

/**
 * @author Jason van Zyl
 * @author Andrew Williams
 */
public class ProcessAppMetadataPhase
    extends AbstractAppDeploymentPhase
{
    public void execute( AppDeploymentContext context )
        throws AppDeploymentException
    {

        Xpp3Dom dom = parseMetadata( context.getPar(), context.getContextValues() );

        String appId = dom.getChild( "name" ).getValue();

        if ( StringUtils.isEmpty( appId ) )
        {
            throw new AppDeploymentException( "Missing 'name' element in the application metadata file." );
        }

        File appDir = new File( context.getApplicationsDirectory(), appId );

        context.setAppDir( appDir );

        // Put the found application id into the context.
        context.setApplicationId( appId );

        context.setAppMetadata( new XmlPlexusConfiguration( dom ) );


        // ----------------------------------------------------------------------------
        // Make the application's home directory available in the context
        // ----------------------------------------------------------------------------
        context.getContextValues().put( "plexus.home", appDir.getAbsolutePath() );

        context.getContextValues().put( "app.home", appDir.getAbsolutePath() );

        // we need to parse again as we have now an updated plexus.home and app.home
        context.setAppMetadata( new XmlPlexusConfiguration( parseMetadata( context.getPar(), context.getContextValues() ) ) );
    }

    private Xpp3Dom parseMetadata( File par, Map contextValues )
        throws AppDeploymentException
    {
                Reader reader;

        JarFile jarFile = null;

        String entryName = PlexusApplicationConstants.CONF_DIRECTORY + File.separatorChar +
            PlexusApplicationConstants.METADATA_FILE;
        try
        {
            jarFile = new JarFile( par );

            ZipEntry entry = jarFile.getEntry( entryName );

            if ( entry == null )
            {
                throw new AppDeploymentException( "The Plexus appserver jar is missing it's metadata file '" +
                    entryName + "'." );
            }

            reader = new InterpolationFilterReader( new InputStreamReader( jarFile.getInputStream( entry ) ),
                                                    contextValues );
        }
        catch ( IOException e )
        {
            throw new AppDeploymentException( "Error reading application JAR file: " + jarFile, e );
        }

        try
        {
            return Xpp3DomBuilder.build( reader );
        }
        catch ( XmlPullParserException e )
        {
            throw new AppDeploymentException( "Error parsing application configurator file.", e );
        }
        catch ( IOException e )
        {
            throw new AppDeploymentException( "Error reading application configurator file.", e );
        }
    }
}
