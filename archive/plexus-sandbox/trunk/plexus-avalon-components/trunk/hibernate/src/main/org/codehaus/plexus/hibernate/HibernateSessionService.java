package org.codehaus.plexus.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

/**
 * A service which starts and ends a hibernate session over its lifecycle.
 * By doing this we can optimize starts/ends of sessions.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 7, 2003
 */
public interface HibernateSessionService
{
    final public static String ROLE = HibernateSessionService.class.getName();
    
    public Session getSession() throws HibernateException;
    
    public void closeSession() throws HibernateException;
}
