package org.codehaus.plexus.security.remote;

import org.codehaus.plexus.security.authentication.AuthenticationException;

/**
  * Front end given to clients
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface Application
{
	public static final String ROLE = Application.class.getName();
	
	public String getName();
	
	public String getVersion();
	
	public String getBuild();
	
	public String getRegisteredTo();
	
	public ApplicationSession authenticate(Object token) throws AuthenticationException;
		
}
