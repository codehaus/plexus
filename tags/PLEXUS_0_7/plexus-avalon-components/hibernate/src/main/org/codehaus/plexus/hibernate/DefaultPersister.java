package org.codehaus.plexus.hibernate;

import java.util.List;

import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * TODO Document DefaultPersister
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since May 14, 2003
 */
public class DefaultPersister
    extends AbstractLogEnabled
    implements Persister
{
    Class clazz;
    
    /**
     * @see org.codehaus.plexus.hibernate.Persister#setClass(java.lang.Class)
     */
    public void setClass(Class clazz)
    {
        this.clazz = clazz;
    }

    /**
     * @see org.codehaus.plexus.hibernate.Persister#save(java.lang.Object)
     */
    public void save(Object o)
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.codehaus.plexus.hibernate.Persister#load(java.lang.Object)
     */
    public Object load(Object id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.codehaus.plexus.hibernate.Persister#delete(java.lang.Object)
     */
    public void delete(Object o)
    {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.codehaus.plexus.hibernate.Persister#selectAll()
     */
    public List selectAll()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
