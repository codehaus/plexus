/* Created on Aug 9, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;

/**
 * @author jdcasey
 */
public class TestTag extends AbstractMarmaladeTag implements PlexusComponentTag{

    public Object getComponent() {
        return this;
    }
    
}
