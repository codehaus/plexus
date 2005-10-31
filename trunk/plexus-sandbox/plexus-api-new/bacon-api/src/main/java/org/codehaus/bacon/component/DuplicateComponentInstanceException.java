package org.codehaus.bacon.component;

public class DuplicateComponentInstanceException
    extends ComponentException
{
    private static final long serialVersionUID = 1L;

    public DuplicateComponentInstanceException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public DuplicateComponentInstanceException( String message )
    {
        super( message );
    }

}
