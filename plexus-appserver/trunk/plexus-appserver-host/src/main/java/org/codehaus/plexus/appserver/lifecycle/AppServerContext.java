package org.codehaus.plexus.appserver.lifecycle;

import org.codehaus.plexus.appserver.ApplicationServer;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class AppServerContext
{
    private ApplicationServer appServer;

    private File appServerHome;

    public AppServerContext( ApplicationServer appServer, File appServerHome )
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
