package org.codehaus.bacon.component;

public class DuplicateComponentDescriptorException
    extends ComponentException
{
    static final long serialVersionUID = 1;

    public DuplicateComponentDescriptorException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public DuplicateComponentDescriptorException( String message )
    {
        super( message );
    }

}
