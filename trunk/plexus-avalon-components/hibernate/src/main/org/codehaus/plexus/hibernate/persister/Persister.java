package org.codehaus.plexus.hibernate.persister;

import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;

/**
 * A Persister for objects that aren't tied to Hibernate.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 12, 2003
 */
public interface Persister
{
    final public static String ROLE = Persister.class.getName();

    public void setClass( Class clazz );
    
    public void save( Object p ) 
        throws HibernateException;

    public void update( Persistable p ) 
        throws HibernateException;
    
    public void update( Object o, long id ) 
        throws HibernateException;
    
    public Object load( long id ) 
        throws HibernateException, ObjectNotFoundException;
    
    public void delete( Object p )
        throws HibernateException;
    
    public List selectAll()
        throws HibernateException;

    public void saveOrUpdate( Object p )
        throws HibernateException;
}
