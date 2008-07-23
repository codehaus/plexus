/* Created on Sep 13, 2004 */
package org.codehaus.plexus.cling.model;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jdcasey
 */
public class DefaultClasspath
    implements Classpath
{

    private List entries = new LinkedList();

    public void addEntry( ClasspathEntry entry )
    {
        entries.add(entry);
    }
    
    public List getEntries() {
        return Collections.unmodifiableList(entries);
    }

}
