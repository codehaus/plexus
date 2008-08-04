package org.codehaus.plexus.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

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
    
    public boolean hasSession();
    
    public Session getSession() throws HibernateException;

    public Session getSession(boolean createTx) throws HibernateException;
    
    public Transaction getTransaction();
    
    public void closeSession() throws HibernateException;
}
