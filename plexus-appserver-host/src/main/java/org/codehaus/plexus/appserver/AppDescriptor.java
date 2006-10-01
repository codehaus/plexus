package org.codehaus.plexus.appserver;

import java.io.File;

/**
 * @author Jason van Zyl
 */
public class AppDescriptor
{
    /**
     * Application id
     */
    private String id;

    /**
     * Application name
     */
    private String name;

    /**
     * Plexus Application Archive
     */
    private File par;

    /**
     * Application deployment directory
     */
    private File appDirectory;

    public AppDescriptor( String id, String name, File par, File appDirectory )
    {
        this.id = id;
        this.name = name;
        this.par = par;
        this.appDirectory = appDirectory;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public File getPar()
    {
        return par;
    }

    public File getAppDirectory()
    {
        return appDirectory;
    }
}
