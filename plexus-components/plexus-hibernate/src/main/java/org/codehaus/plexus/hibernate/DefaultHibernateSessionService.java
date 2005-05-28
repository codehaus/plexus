package org.codehaus.plexus.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Disposable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

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
    private ThreadLocal transactions = new ThreadLocal();

    private List sessions;

    private HibernateService hibService;
    
    private boolean createTx = false;
    
    public boolean hasSession()
    {
        return session.get() != null;
    }

    /**
     * @see org.codehaus.plexus.hibernate.HibernateSessionService#getSession()
     */
    public Session getSession() throws HibernateException
    {
        return currentSession(createTx);
    }
    
    public Session getSession(boolean createTxLocal) throws HibernateException
    {
        return currentSession(createTxLocal);
    }
    
    public void initialize() throws InitializationException
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
                closeSession(s);
                itr.remove();
            }
            catch (HibernateException e)
            {
                getLogger().error("Couldn't close session!", e);
            }
        }
    }

    public Session currentSession(boolean createTxLocal) throws HibernateException
    {
        Session s = (Session) session.get();
        if (s == null)
        {
            s = sessionFactory.openSession();
            session.set(s);
            sessions.add(s);
            
            if (createTxLocal)
            {
                transactions.set(s.beginTransaction());
            }
        }
        return s;
    }

    public Transaction getTransaction()
    {
        return (Transaction) transactions.get();
    }
    
    public void closeSession() throws HibernateException
    {
        Session s = (Session) session.get();
        
        if (s != null)
            closeSession(s);
        
        sessions.remove(s);
        session.set(null);
    }

    protected void closeSession(Session s)
        throws HibernateException
    {
        getLogger().debug("Closing session for thread.");

        try
        {
            Transaction tx = (Transaction) transactions.get();
            if (tx != null)
            {
                tx.commit();
            }
        }
        finally
        {
            if (s != null)
                s.close();
        }
    }
}
