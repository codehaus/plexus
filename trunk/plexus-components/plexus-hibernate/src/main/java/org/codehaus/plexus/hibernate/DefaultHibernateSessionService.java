package org.codehaus.plexus.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 7, 2003
 */
public class DefaultHibernateSessionService
    extends AbstractLogEnabled
    implements
        HibernateSessionService,
        Initializable,
        Disposable
{
    private SessionFactory sessionFactory;

    private ThreadLocal session = new ThreadLocal();

    private List sessions;

    private HibernateService hibService;
    
    /**
     * @see org.codehaus.plexus.hibernate.HibernateSessionService#getSession()
     */
    public Session getSession() throws HibernateException
    {
        return currentSession();
    }

    public void initialize() throws Exception
    {
        sessions = new ArrayList();
        
        sessionFactory = hibService.getSessionFactory();
    }

    public void dispose()
    {
        for ( Iterator itr = sessions.iterator(); itr.hasNext(); )
        {
            Session s = (Session) itr.next();
            try
            {
                getLogger().info("Closing open hibernate session.");
                s.close();
                itr.remove();
            }
            catch (HibernateException e)
            {
                getLogger().error("Couldn't close session!", e);
            }
        }
    }

    public Session currentSession() throws HibernateException
    {
        Session s = (Session) session.get();
        if (s == null)
        {
            s = sessionFactory.openSession();
            session.set(s);
            sessions.add(s);
        }
        return s;
    }

    public void closeSession() throws HibernateException
    {
        getLogger().debug("Closing session for thread.");
 
        Session s = (Session) session.get();
        sessions.remove(s);
        session.set(null);
        if (s != null)
            s.close();
    }
}
