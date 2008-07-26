package org.codehaus.plexus.pipeline;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PipelineException
    extends Exception
{
    public PipelineException( String message )
    {
        super( message );
    }

    public PipelineException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
