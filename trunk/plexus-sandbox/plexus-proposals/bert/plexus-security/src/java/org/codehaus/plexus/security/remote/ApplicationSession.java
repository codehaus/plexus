package org.codehaus.plexus.security.remote;
import java.util.Set;

import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.codehaus.plexus.security.Agent;

/**
  * Sessions given to clients. Clients use this to access business components. 
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface ApplicationSession extends ServiceManager
{
    public boolean contains(String key);

    public Object get(String key);

    public Agent getAgent();

    public long getCreationTime();

    public String getId();

    public long getLastAccessTime();

    public long getTimeout();

    public void invalidate();

    public boolean isValid();

    public Set keys();

    public void put(String key, Object value);

    public Object remove(String key);

    public boolean hasService(String role);

    public Object lookup(String role) throws ServiceException;
    
	public Object lookup(String role,String id) throws ServiceException;

    public void release(Object arg0);
}
