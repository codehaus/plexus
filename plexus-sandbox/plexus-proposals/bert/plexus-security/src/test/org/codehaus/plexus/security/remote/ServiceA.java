package org.codehaus.plexus.security.remote;

/**
  * 
  * <p>Created on 20/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface ServiceA
{
	public static final String ROLE =ServiceA.class.getName();
	
	
	public void doSomething();
	
	public void doSomething(String value);
	
	
}
