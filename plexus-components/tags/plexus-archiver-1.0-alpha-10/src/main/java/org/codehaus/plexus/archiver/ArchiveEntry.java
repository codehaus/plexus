package org.codehaus.plexus.archiver;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.plexus.components.io.resources.PlexusIoFileResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

/**
 * @version $Revision: 1502 $ $Date$
 */
public class ArchiveEntry
{
    public static final String ROLE = ArchiveEntry.class.getName();

    public static final int FILE = 1;

    public static final int DIRECTORY = 2;

    private PlexusIoResource resource;

    private String name;

    private int type;

    private int mode;

    /**
     * @param name     the filename as it will appear in the archive
     * @param original original filename
     * @param type     FILE or DIRECTORY
     * @param mode     octal unix style permissions
     */
    private ArchiveEntry( String name, PlexusIoResource resource, int type, int mode )
    {
        this.name = name;
        this.resource = resource;
        this.type = type;
        this.mode = ( mode & UnixStat.PERM_MASK ) |
                    ( type == FILE ? UnixStat.FILE_FLAG : UnixStat.DIR_FLAG );
    }

    /**
     * @return the filename of this entry in the archive.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return The original file that will be stored in the archive.
     * @deprecated As of 1.0-alpha-10, file entries are no longer backed
     *   by files, but by instances of {@link PlexusIoResource}.
     *   Consequently, you should use {@link #getInputStream()}-
     */
    public File getFile()
    {
        if ( resource instanceof PlexusIoFileResource )
        {
            return ((PlexusIoFileResource) resource).getFile();
        }
        return null;
    }

    /**
     * @return The resource contents.
     */
    public InputStream getInputStream() throws IOException
    {
        return resource.getContents();
    }
    
    /**
     * TODO: support for SYMLINK?
     *
     * @return FILE or DIRECTORY
     */
    public int getType()
    {
        return type;
    }

    /**
     * @return octal user/group/other unix like permissions.
     */
    public int getMode()
    {
        return mode;
    }

    public static ArchiveEntry createFileEntry( String target, PlexusIoResource resource, int permissions )
        throws ArchiverException
    {
        if ( resource.isDirectory() )
        {
            throw new ArchiverException( "Not a file: " + resource.getName() );
        }
        return new ArchiveEntry( target, resource, FILE, permissions );
    }

    public static ArchiveEntry createFileEntry( String target, File file, int permissions )
        throws ArchiverException
    {
        if ( ! file.isFile() )
        {
            throw new ArchiverException( "Not a file: " + file );
        }
        final PlexusIoFileResource res =  new PlexusIoFileResource( file );
        return new ArchiveEntry( target, res, FILE, permissions );
    }

    public static ArchiveEntry createDirectoryEntry( String target, PlexusIoResource resource, int permissions )
        throws ArchiverException
    {
        if ( ! resource.isDirectory() )
        {
            throw new ArchiverException( "Not a directory: " + resource.getName() );
        }
        return new ArchiveEntry( target, resource, DIRECTORY, permissions );
    }

    public static ArchiveEntry createDirectoryEntry( String target, final File file, int permissions )
        throws ArchiverException
    {
        if ( ! file.isDirectory() )
        {
            throw new ArchiverException( "Not a directory: " + file );
        }
        final PlexusIoFileResource res = new PlexusIoFileResource( file );
        return new ArchiveEntry( target, res, DIRECTORY, permissions );
    }

    public static ArchiveEntry createEntry( String target, File file, int filePerm, int dirPerm )
        throws ArchiverException
    {
        if ( file.isDirectory() )
        {
            return createDirectoryEntry( target, file, dirPerm );
        }
        else if ( file.isFile() )
        {
            return createFileEntry( target, file, filePerm );
        }
        else // FIXME: handle symlinks?
        {
            throw new ArchiverException( "Neither a file nor a directory: " + file );
        }
    }

    public PlexusIoResource getResource()
    {
        return resource;
    }
}
