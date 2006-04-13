package org.codehaus.bacon;

public class LookupException
    extends ContainerException
{
    static final long serialVersionUID = 1;

    public LookupException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public LookupException( String message )
    {
        super( message );
    }

}
