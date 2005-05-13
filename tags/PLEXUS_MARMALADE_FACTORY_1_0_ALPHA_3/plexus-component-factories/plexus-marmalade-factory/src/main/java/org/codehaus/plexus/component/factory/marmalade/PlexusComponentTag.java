/* Created on Aug 6, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

/** Interface which all marmalade scripts' root tags must implement if it is to
 * be used as a plexus component script.
 * 
 * @author jdcasey
 */
public interface PlexusComponentTag {
    
    /** Return the component built by the script for which this tag is the root.
     */
    public Object getComponent();

}
