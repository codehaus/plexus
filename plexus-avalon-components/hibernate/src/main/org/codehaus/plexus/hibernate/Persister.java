package org.codehaus.plexus.hibernate;

import java.util.List;

/**
 * A Persister for objects that isn't tied to Hibernate.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 12, 2003
 */
public interface Persister
{
    public void setClass( Class clazz );
    
    public void save( Object o );
    
    public Object load( Object id );
    
    public void delete( Object o );
    
    public List selectAll();
}
