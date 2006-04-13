package org.codehaus.plexus.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 7, 2003
 */
public class DefaultHibernateSessionService
    extends AbstractLogEnabled
    implements
        HibernateSessionService,
        Initializable,
        Disposable,
        Serviceable
{
    private ServiceManager manager;

    private SessionFactory sessionFactory;

    private ThreadLocal session = new ThreadLocal();

    private List sessions;

    /**
     * @see org.codehaus.plexus.hibernate.HibernateSessionService#getSession()
     */
    public Session getSession() throws HibernateException
    {
        return currentSession();
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        HibernateService hib =
            (HibernateService) manager.lookup(HibernateService.ROLE);

        sessionFactory = hib.getSessionFactory();

        manager.release(hib);

        sessions = new ArrayList();
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
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

    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.manager = manager;
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
        getLogger().info("Closing session for thread.");
 
        Session s = (Session) session.get();
        sessions.remove(s);
        session.set(null);
        if (s != null)
            s.close();
    }
}
