/**
 * 
 */
package org.codehaus.plexus.site.monitor;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.tutorial.WebsiteMonitor;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class Main
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        Embedder embedder = new Embedder();
        try
        {
            embedder.start();
            PlexusContainer container = embedder.getContainer();
            WebsiteMonitor monitor = (WebsiteMonitor) container.lookup( WebsiteMonitor.ROLE );
            try
            {
                monitor.monitor();
            }
            catch ( Exception e )
            {             
                e.printStackTrace();
            }

        }
        catch ( PlexusContainerException e )
        {
            e.printStackTrace();
        }
        catch ( ComponentLookupException e )
        {
            e.printStackTrace();
        }

    }

}
