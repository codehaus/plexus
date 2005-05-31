package org.codehaus.plexus.werkflow;

import org.codehaus.werkflow.simple.ActionManager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface WerkflowActionManager
    extends ActionManager
{
    static String ROLE = WerkflowActionManager.class.getName();
}
