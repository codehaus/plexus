package org.codehaus.plexus.hibernate.persister;

import java.io.Serializable;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class LongPersistable
    implements Persistable
{
    private Long id;
    
    public LongPersistable()
    {
        setId(new Long(-1));
    }
    
    public Serializable getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public Long getIdAsLong()
    {
        return id;
    }
    
	public boolean equals(Object o)
	{
        if ( !(o instanceof Persistable) )
        {
            return false;
        }
        
		Persistable p = (Persistable) o;
        
        return id.equals( p.getId() );
	}
    
	public int hashCode()
	{
        return id.hashCode();
	}
}
