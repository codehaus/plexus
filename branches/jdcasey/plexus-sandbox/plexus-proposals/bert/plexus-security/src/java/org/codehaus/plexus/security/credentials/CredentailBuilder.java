package org.codehaus.plexus.security.credentials;

/**
  * Build the credentials for a given agent
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface CredentailBuilder
{
	public static final String ROLE = CredentailBuilder.class.getName();
	
	
	public Object buildCredentials(String agentId);
}
