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
}
