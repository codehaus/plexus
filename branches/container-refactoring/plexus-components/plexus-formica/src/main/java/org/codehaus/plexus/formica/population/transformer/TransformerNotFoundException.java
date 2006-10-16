package org.codehaus.plexus.formica.population.transformer;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class TransformerNotFoundException
    extends Exception
{
    public TransformerNotFoundException( String message )
    {
        super( message );
    }

    public TransformerNotFoundException( Throwable cause )
    {
        super( cause );
    }

    public TransformerNotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
