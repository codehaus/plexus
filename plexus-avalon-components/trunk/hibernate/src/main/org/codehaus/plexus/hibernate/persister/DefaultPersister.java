package org.codehaus.plexus.hibernate.persister;

import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.codehaus.plexus.hibernate.HibernateService;
import org.codehaus.plexus.hibernate.HibernateSessionService;

/**  
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 14, 2003
 */
public class DefaultPersister
    extends AbstractLogEnabled
    implements Persister, Serviceable, Disposable, Initializable
{

    private Class clazz;
    private ServiceManager manager;
    private HibernateService hib;
    
    /**
     * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
     */
    public void service(ServiceManager manager) throws ServiceException
    {
        this.manager = manager;
    }
    
    /**
     * @see com.moveitthere.delivery.service.Persister#setClass(java.lang.Class)
     */
    public void setClass(Class clazz)
    {
        this.clazz = clazz;
    }
    
    protected Session getSession()
        throws HibernateException
    {
        HibernateSessionService hss;
        try
        {
            hss = (HibernateSessionService) manager.lookup(HibernateSessionService.ROLE);
            return hss.getSession();
        }
        catch (ServiceException e)
        {
            throw new RuntimeException( "Could not find the Hibernate service.", e );
        }
    }
    
    /**
     * @see com.moveitthere.delivery.service.Persister#save(java.lang.Object)
     */
    public void save(Object o)
        throws HibernateException
    {
        Session session = getSession();
        try
        {
        	session.save( o );
        }
        finally
        {
            session.flush();
        }
    }

    /**
     * @see org.codehaus.plexus.hibernate.persister.Persister#update(org.codehaus.plexus.hibernate.persister.Persistable)
     */
    public void update(Persistable p) throws HibernateException
    {
        update( p, p.getId() );
    }
    
    /**
     * @see com.moveitthere.delivery.service.Persister#save(java.lang.Object)
     */
    public void update( Object o, long id ) throws HibernateException
    {
        Session session = getSession();
        try
        {
            session.update( o, new Long(id) );
        }
        finally
        {
            session.flush();
        }

    }

    /**
     * @see com.moveitthere.delivery.service.Persister#save(java.lang.Object)
     */
    public void saveOrUpdate( Object o ) throws HibernateException
    {
        Session session = getSession();
        try
        {
            session.saveOrUpdate(o);
        }
        finally
        {
            session.flush();
        }
    }
    
    /**
     * @see com.moveitthere.delivery.service.Persister#load(java.lang.Object)
     */
    public Object load( long id )
        throws HibernateException, ObjectNotFoundException
    {
        Session session = getSession();
        return session.load( clazz, new Long(id) );
    }

    /**
     * @see com.moveitthere.delivery.service.Persister#delete(java.lang.Object)
     */
    public void delete(Object o) throws HibernateException
    {
        Session session = getSession();
        try
        {
            session.delete( o );
        }
        finally
        {
            session.flush();
        }
    }

    /**
     * @see com.moveitthere.delivery.service.Persister#selectAll()
     */
    public List selectAll() throws HibernateException
    {
        Session session = getSession();
        return session.find( "from " + clazz.getName() + " as obj");
    }

    /**
     * @see org.apache.avalon.framework.activity.Disposable#dispose()
     */
    public void dispose()
    {
        manager.release( hib );
    }

    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        try
        {
            hib = (HibernateService) manager.lookup(HibernateService.ROLE);
        }
        catch (ServiceException e)
        {
            throw new RuntimeException( "Could not find the HibernateService.", e );
        }
    }


}
