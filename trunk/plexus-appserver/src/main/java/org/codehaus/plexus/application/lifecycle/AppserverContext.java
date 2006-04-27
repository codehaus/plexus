package org.codehaus.plexus.application.lifecycle;

import org.codehaus.plexus.application.ApplicationServer;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class AppServerContext
{
    private ApplicationServer appServer;

    private File appServerHome;

    public AppServerContext( ApplicationServer appServer,
                             File appServerHome )
    {
        this.appServer = appServer;
        this.appServerHome = appServerHome;
    }

    public ApplicationServer getAppServer()
    {
        return appServer;
    }

    public File getAppServerHome()
    {
        return appServerHome;
    }
}
