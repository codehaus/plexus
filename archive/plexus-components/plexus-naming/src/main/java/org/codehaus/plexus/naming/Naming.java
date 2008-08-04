package org.codehaus.plexus.naming;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * A naming component for establishing a JNDI tree. This is useful to <code>load-on-start</code> in your plexus
 * applications.
 *
 * @author Brett Porter
 */
public interface Naming
{
    String ROLE = Naming.class.getName();

    Context createInitialContext()
        throws NamingException;
}
