/* Created on Sep 13, 2004 */
package org.codehaus.cling.model;

import java.net.URL;
import java.util.List;

/**
 * @author jdcasey
 */
public interface Classpath
{

    public void addEntry( URL entry );

    public List getEntries();

}
