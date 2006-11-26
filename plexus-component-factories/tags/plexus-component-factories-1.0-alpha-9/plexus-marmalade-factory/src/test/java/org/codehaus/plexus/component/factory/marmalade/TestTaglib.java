/* Created on Aug 9, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import org.codehaus.marmalade.metamodel.AbstractMarmaladeTagLibrary;

/**
 * @author jdcasey
 */
public class TestTaglib extends AbstractMarmaladeTagLibrary {

    public TestTaglib() {
        registerTag("testScript", TestTag.class);
    }

}
