package org.codehaus.plexus.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
  * Generate a unique id for exceptions for  tracking purposes.
  * 
  * <p>Created on 12/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ExceptionIDGenerator
{
    /**
     * 
     */
    private  ExceptionIDGenerator()
    {
        super();
    }

	/**
	 * Generate a unique id based on the current host and time. Not sure if two calls to 
	 *	<code>System.currentTimeMillis()</code> ever returns the same value, if so then
	 * there is a chance that two identical ids can be generated
	 * 
	 * @todo possibly add a static counter to ensure unique ids.
	 * 
	 * @return a unique id
	 */
	public static final String generateId()
	{
		try
        {
            return InetAddress.getLocalHost().getHostAddress() + "-" + Long.toString(System.currentTimeMillis());
        }
        catch (UnknownHostException e)
        {
            // use the localhost address
            return "1.0.0.127-" + Long.toString(System.currentTimeMillis());
        }
		
	}
}
