/**
 * 
 */
package org.codehaus.plexus.cdc.merge;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public class MergeException
    extends Exception
{

    public MergeException()
    {
        super();
    }

    public MergeException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public MergeException( String message )
    {
        super( message );
    }

    public MergeException( Throwable cause )
    {
        super( cause );
    }

}
