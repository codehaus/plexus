/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling.cli;

/**
 * @author jdcasey
 */
public class InvocationException
    extends Exception
{

    public InvocationException( String message )
    {
        super( message );
    }

    public InvocationException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
