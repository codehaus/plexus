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
 * @author Andrew Williams
 */
public class AppDeploymentContext
{
    private File par;

    private File applicationsDirectory;

    private Map deployments;

    private DefaultPlexusContainer appServerContainer;

    // XXX This field is never set
    private Properties context;

    private Map contextValues;

    private String applicationId;

    private PlexusConfiguration appMetadata;

    private AppRuntimeProfile appRuntimeProfile;

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

    // XXX this method always returns null
    public Properties getContext()
    {
        return context;
    }

    // XXX this method is never called
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

    public PlexusConfiguration getAppMetadata()
    {
        return appMetadata;
    }

    public void setAppMetadata( PlexusConfiguration appMetadata )
    {
        this.appMetadata = appMetadata;
    }

    public AppRuntimeProfile getAppRuntimeProfile()
    {
        return appRuntimeProfile;
    }

    public void setAppRuntimeProfile( AppRuntimeProfile appRuntimeProfile )
    {
        this.appRuntimeProfile = appRuntimeProfile;
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

