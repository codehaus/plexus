package org.codehaus.plexus.util;

/**
  * Useful util to enable really low level debug logging, Useful for debug logging which
  * one does not even wish to be included with the system logger (ie log4j). 
  * 
  * 
  * <p>Created on 19/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class DebugUtils
{
	/** Switch debug logging on and off. */
    public static final boolean DEBUG = true;

	/**
	 * Log the given message.
	 * 
	 * @param caller the class where this method is invoked. Very important
	 * this is included so location of log statements can be found.
	 * 
	 * @param msg
	 */
    public static final void debug(Object caller, String msg)
    {
        if (DEBUG)
        {
            System.out.println(caller.getClass().getName() + ":" + msg);
        }
    }

	/**
	 * Log the given message.
	 * 
	 * @param caller the object from  where this method is invoked. Very important
	 * this is included so location of log statements can be found.
	 * 
	 * @param msg
	 */
    public static final void debug(Class caller, String msg)
    {
        if (DEBUG)
        {
            System.out.println(caller.getName() + ":" + msg);
        }
    }
}
