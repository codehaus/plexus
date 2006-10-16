package org.codehaus.plexus.components.urlfactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Creates a URL object from the String representation.
 *
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Revision$
 * @component.specification
 */
public interface URLFactory
{
    String ROLE = URLFactory.class.getName();

    /**
     * Creates a URL object from the String representation.
     * @param the String to parse as a URL
     * @return  a URL object
     * @throws MalformedURLException If the string specifies an unknown protocol.
     */
    public URL getURL( String spec )
            throws MalformedURLException;
        
    //public static URL newURL (final URI uri) throws MalformedURLException;

} 