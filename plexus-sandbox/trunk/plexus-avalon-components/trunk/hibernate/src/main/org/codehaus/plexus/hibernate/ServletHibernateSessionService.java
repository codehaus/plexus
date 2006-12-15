package org.codehaus.plexus.hibernate;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.sf.hibernate.HibernateException;

import org.codehaus.plexus.hibernate.DefaultHibernateSessionService;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 7, 2003
 */
public class ServletHibernateSessionService
    extends DefaultHibernateSessionService
    implements
        HttpSessionListener
{
    /**
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent arg0)
    {
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent session)
    {
        try
        {
            closeSession();
        }
        catch (HibernateException e)
        {
            throw new RuntimeException(e);
        }
    }
}
