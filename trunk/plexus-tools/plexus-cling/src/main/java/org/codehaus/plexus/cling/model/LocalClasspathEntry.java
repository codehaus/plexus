/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.model;

import java.net.URL;

/**
 * @author jdcasey
 */
public class LocalClasspathEntry
    implements ClasspathEntry
{

    private final URL url;

    public LocalClasspathEntry(URL url)
    {
        this.url = url;
    }

    public URL getURL()
    {
        return url;
    }

}
