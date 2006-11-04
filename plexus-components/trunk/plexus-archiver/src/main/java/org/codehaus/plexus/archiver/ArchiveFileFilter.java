package org.codehaus.plexus.archiver;

import java.io.InputStream;

public interface ArchiveFileFilter
{
    
    boolean include( InputStream dataStream, String entryName )
        throws ArchiveFilterException;

}
