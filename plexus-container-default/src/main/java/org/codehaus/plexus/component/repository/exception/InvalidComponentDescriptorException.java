package org.codehaus.plexus.component.repository.exception;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class InvalidComponentDescriptorException
    extends ComponentRepositoryException
{
    public InvalidComponentDescriptorException( String message )
    {
        super( message );
    }

    public InvalidComponentDescriptorException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
