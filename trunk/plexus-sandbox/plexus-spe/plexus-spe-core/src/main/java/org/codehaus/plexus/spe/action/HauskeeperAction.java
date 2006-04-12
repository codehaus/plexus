package org.codehaus.hauskeeper.action;

import java.util.Map;
import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface HauskeeperAction
{
    String ROLE = HauskeeperAction.class.getName();

    void execute( Map<String, Serializable> context )
        throws Exception;
}
