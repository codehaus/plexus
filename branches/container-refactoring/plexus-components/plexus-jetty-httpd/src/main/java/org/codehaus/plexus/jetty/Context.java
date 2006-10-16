package org.codehaus.plexus.jetty;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class Context
{
    private String path;

    private String documentRoot;

    public String getPath()
    {
        return path;
    }

    public String getDocumentRoot()
    {
        return documentRoot;
    }
}
