package org.codehaus.plexus.security.session;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

/**
  * Provides unique session ids. Probably not very secure.
  * 
  * <p>Created on 16/08/2003</p>
  *
  * @author <a href="mailto:bertvanbrakel@sourceforge.net">Bert van Brakel</a>
  * @revision $Revision$
  */
public class SessionIdGenerator
{
	private SecureRandom random = new SecureRandom();
    /**
     * 
     */
    public SessionIdGenerator()
    {
        super();
    }

	public String timeAndHostBasedId()
	{
		try
		{
			return InetAddress.getLocalHost().getHostAddress() + ":" + Long.toString(System.currentTimeMillis());
		}
		catch (UnknownHostException e)
		{
			// use the localhost address
			return "1.0.0.127:" + Long.toString(System.currentTimeMillis());
		}
		
	}
	
	/**
	 * Generates a unique session id based on the host ip, current time, and a random number.
	 * 
	 * @return
	 */
	public String generateUniqueId()
	{
		StringBuffer buff = new StringBuffer();
		
		try
		{
			buff.append( InetAddress.getLocalHost().getHostAddress()  );
		}
		catch (UnknownHostException e)
		{
			// use the localhost address
			buff.append("1.0.0.127" );
		}
		
		buff.append(':').append(System.currentTimeMillis() ).append(':');
		byte[] bytes = new byte[ 30 ];
		random.nextBytes(bytes);
		buff.append( bytes );
		return buff.toString();
	}
	
}
