package org.codehaus.plexus.component.manager;

/**
  * 
  * <p>Created on 22/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public interface ServiceA
{
	public static final String ROLE = ServiceA.class.getName();
	
	public void doSomething();

}
