package org.codehaus.plexus.hibernate.persister;

import java.io.Serializable;
import java.util.List;

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

    public void save( Object p );

    public void update( Persistable p );
    
    public void update( Object o, Serializable id );
    
    public Object load( Serializable id, Class clazz ) 
        throws ObjectNotFoundException;
    
    public void delete( Object p );
    
    public List selectAll(Class clazz);

    public void saveOrUpdate( Object p );
}
