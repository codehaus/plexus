package org.codehaus.plexus.hibernate.persister;

import java.io.Serializable;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;

import org.codehaus.plexus.hibernate.HibernateService;
import org.codehaus.plexus.hibernate.HibernateSessionService;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**  
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 14, 2003
 */
public class DefaultPersister
    extends AbstractLogEnabled
    implements Persister
{

    private Class clazz;
    private HibernateService hib;
    private HibernateSessionService hss;
    
    /**
     * @see com.moveitthere.delivery.service.Persister#setClass(java.lang.Class)
     */
    public void setClass(Class clazz)
    {
        this.clazz = clazz;
    }
    
    protected Session getSession()
    {
        try
		{
			return hss.getSession();
		}
		catch (HibernateException e)
		{
			throw new RuntimeException("Hibernate backend problem.", e);
		}
    }
    
    /**
     * @see com.moveitthere.delivery.service.Persister#save(java.lang.Object)
     */
    public void save(Object o)
    {
        Session session = getSession();
        try
        {
            try
            {
            	session.save( o );
            }
            finally
            {
                session.flush();
            }
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }

    /**
     * @see org.codehaus.plexus.hibernate.persister.Persister#update(org.codehaus.plexus.hibernate.persister.Persistable)
     */
    public void update(Persistable p)
    {
        update( p, p.getId() );
    }
    
    /**
     * @see com.moveitthere.delivery.service.Persister#save(java.lang.Object)
     */
    public void update( Object o, Serializable id )
    {
        Session session = getSession();
        try
        {
            try
            {
                session.update(o, id);
            }
            finally
            {
                session.flush();
            }
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }

    /**
     * @see com.moveitthere.delivery.service.Persister#save(java.lang.Object)
     */
    public void saveOrUpdate( Object o )
    {
        Session session = getSession();
        try
        {
            try
            {
                session.saveOrUpdate(o);
            }
            finally
            {
                session.flush();
            }
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }
    
    /**
     * @see com.moveitthere.delivery.service.Persister#load(java.lang.Object)
     */
    public Object load( Serializable id )
        throws ObjectNotFoundException
    {
        Session session = getSession();
        try
        {
        	return session.load( clazz, id );
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }

    /**
     * @see com.moveitthere.delivery.service.Persister#delete(java.lang.Object)
     */
    public void delete(Object o)
    {
        Session session = getSession();
        try
        {
            try
            {
                session.delete(o);
            }
            finally
            {
                session.flush();
            }
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }

    /**
     * @see com.moveitthere.delivery.service.Persister#selectAll()
     */
    public List selectAll() 
    {
        Session session = getSession();
        try
		{
			return session.find( "from " + clazz.getName() + " as obj");
		}
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }
}
