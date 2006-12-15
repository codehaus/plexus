/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling;

/**
 * @author jdcasey
 */
public class CLIngLaunchException
    extends Exception
{

    private final int errorCode;
    
    public CLIngLaunchException( String message, int errorCode )
    {
        super(message);
        this.errorCode = errorCode;
    }

    public CLIngLaunchException( String message, int errorCode, Throwable cause )
    {
        super( message, cause );
        this.errorCode = errorCode;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

}