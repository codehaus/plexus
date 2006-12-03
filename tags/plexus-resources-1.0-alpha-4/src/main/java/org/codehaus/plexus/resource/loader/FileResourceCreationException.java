package org.codehaus.plexus.resource.loader;

/** @author Jason van Zyl */
public class FileResourceCreationException
    extends Exception
{

    public FileResourceCreationException( String string )
    {
        super( string );
    }

    public FileResourceCreationException( String string,
                                          Throwable throwable )
    {
        super( string, throwable );
    }

    public FileResourceCreationException( Throwable throwable )
    {
        super( throwable );
    }
}
