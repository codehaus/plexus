package org.codehaus.plexus.hibernate.persister;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public abstract class AbstractPersistable
    implements Persistable
{
    private long id;
    
    public AbstractPersistable()
    {
        setId(-1);
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
	public boolean equals(Object o)
	{
        if ( !(o instanceof Persistable) )
        {
            return false;
        }
        
		Persistable p = (Persistable) o;
        
        if ( p.getId() == this.getId() )
        {
            return true;
        }
        
        return false;
	}
    
	public int hashCode()
	{
        int hash = 7;
        int var_code = (int)(id ^ (id >>> 32));

        hash = 31 * hash + var_code;
        
		return hash;
	}
}
