package org.codehaus.plexus.appserver.application.deploy.lifecycle;

import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.configuration.PlexusConfiguration;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * @author Jason van Zyl
 */
public class AppDeploymentContext
{
    private File par;

    private File applicationsDirectory;

    private Map deployments;

    private DefaultPlexusContainer appServerContainer;

    private Properties context;

    private Map contextValues;
    //

    private String applicationId;

    private PlexusConfiguration appConfiguration;

    private AppRuntimeProfile appRuntimeProfile;

    private DefaultPlexusContainer applicationContainer;

    private File appConfigurationFile;

    private File appDir;

    // app.home/lib used for populating the class realm.
    private File appLibDirectory;

    private ApplicationServer appServer;

    private boolean expandPar;

    public AppDeploymentContext( File par, File applicationsDirectory, Map deployments,
                                 DefaultPlexusContainer appServerContainer, ApplicationServer appServer,
                                 boolean expandPar )
    {
        this.par = par;
        this.applicationsDirectory = applicationsDirectory;
        this.deployments = deployments;
        this.appServerContainer = appServerContainer;
        this.appServer = appServer;
        this.expandPar = expandPar;
    }

    // Read-only

    public File getPar()
    {
        return par;
    }

    public File getApplicationsDirectory()
    {
        return applicationsDirectory;
    }

    public Map getDeployments()
    {
        return deployments;
    }

    public DefaultPlexusContainer getAppServerContainer()
    {
        return appServerContainer;
    }

    // Properties

    public Properties getContext()
    {
        return context;
    }

    public void setContext( Properties context )
    {
        this.context = context;
    }

    public Map getContextValues()
    {
        return contextValues;
    }

    public void setContextValues( Map contextValues )
    {
        this.contextValues = contextValues;
    }

    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId( String applicationId )
    {
        this.applicationId = applicationId;
    }

    public PlexusConfiguration getAppConfiguration()
    {
        return appConfiguration;
    }

    public void setAppConfiguration( PlexusConfiguration appConfiguration )
    {
        this.appConfiguration = appConfiguration;
    }

    public AppRuntimeProfile getAppRuntimeProfile()
    {
        return appRuntimeProfile;
    }

    public void setAppRuntimeProfile( AppRuntimeProfile appRuntimeProfile )
    {
        this.appRuntimeProfile = appRuntimeProfile;
    }

    public DefaultPlexusContainer getApplicationContainer()
    {
        return applicationContainer;
    }

    public void setApplicationContainer( DefaultPlexusContainer applicationContainer )
    {
        this.applicationContainer = applicationContainer;
    }

    public File getAppConfigurationFile()
    {
        return appConfigurationFile;
    }

    public void setAppConfigurationFile( File appConfigurationFile )
    {
        this.appConfigurationFile = appConfigurationFile;
    }

    public File getAppLibDirectory()
    {
        return appLibDirectory;
    }

    public void setAppLibDirectory( File appLibDirectory )
    {
        this.appLibDirectory = appLibDirectory;
    }

    public File getAppDir()
    {
        return appDir;
    }

    public void setAppDir( File appDir )
    {
        this.appDir = appDir;
    }

    public ApplicationServer getAppServer()
    {
        return appServer;
    }

    public boolean isExpandPar()
    {
        return expandPar;
    }
}

