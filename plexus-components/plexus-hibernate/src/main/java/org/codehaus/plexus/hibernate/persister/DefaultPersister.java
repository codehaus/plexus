package org.codehaus.plexus.hibernate.persister;

import java.io.Serializable;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;

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
    private HibernateSessionService hss;
    
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
     * @see org.codehaus.plexus.hibernate.persister.Persister#save(java.lang.Object)
     */
    public void save(Object o)
    {
        Session session = getSession();
        try
        {
        	session.save( o );
            session.flush();
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
     * @see org.codehaus.plexus.hibernate.persister.Persister#save(java.lang.Object)
     */
    public void update( Object o, Serializable id )
    {
        Session session = getSession();
        try
        {
            session.update(o, id);
            session.flush();
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }

    /**
     * @see org.codehaus.plexus.hibernate.persister.Persister#save(java.lang.Object)
     */
    public void saveOrUpdate( Object o )
    {
        Session session = getSession();
        try
        {
            session.saveOrUpdate(o);
            session.flush();
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }
    
    /**
     * @see org.codehaus.plexus.hibernate.persister.Persister#load(Serializable, Class)
     */
    public Object load( Serializable id, Class clazz )
        throws ObjectNotFoundException
    {
        Session session = getSession();
        try
        {
        	return session.load( clazz, id );
        }
        catch (HibernateException e)
        {
            if ( e instanceof ObjectNotFoundException )
                throw (ObjectNotFoundException) e;
            
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }

    /**
     * @see org.codehaus.plexus.hibernate.persister.Persister#delete(java.lang.Object)
     */
    public void delete(Object o)
    {
        Session session = getSession();
        try
        {
            session.delete(o);
            session.flush();
        }
        catch (HibernateException e)
        {
            throw new RuntimeException("Hibernate backend problem.", e);
        }
    }

    /**
     * @see org.codehaus.plexus.hibernate.persister.Persister#selectAll(Class)
     */
    public List selectAll(Class clazz) 
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
