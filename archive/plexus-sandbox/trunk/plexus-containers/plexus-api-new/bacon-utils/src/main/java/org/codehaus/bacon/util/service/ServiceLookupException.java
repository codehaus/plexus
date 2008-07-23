package org.codehaus.bacon.util.service;

public class ServiceLookupException
    extends Exception
{
    private static final long serialVersionUID = 1L;

    public ServiceLookupException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public ServiceLookupException( String message )
    {
        super( message );
    }

}
