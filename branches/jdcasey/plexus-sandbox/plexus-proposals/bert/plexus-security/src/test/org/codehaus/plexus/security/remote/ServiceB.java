package org.codehaus.plexus.security.remote;

/**
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface ServiceB
{
	public static final String ROLE = ServiceB.class.getName();
	
	public void doSomething();
}
