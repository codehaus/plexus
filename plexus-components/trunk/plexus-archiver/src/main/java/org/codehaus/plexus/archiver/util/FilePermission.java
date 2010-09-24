package org.codehaus.plexus.archiver.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @author Olivier Lamy
 * @since 1.0.1
 *
 */
public class FilePermission
{
    
    private boolean executable;
    
    private boolean ownerOnlyExecutable;
    
    private boolean ownerOnlyReadable;
    
    private boolean readable;
    
    private boolean ownerOnlyWritable;
    
    private boolean writable;

    public FilePermission( boolean executable, boolean ownerOnlyExecutable, boolean ownerOnlyReadable,
                           boolean readable, boolean ownerOnlyWritable, boolean writable )
    {
        this.executable = executable;
        this.ownerOnlyExecutable = ownerOnlyExecutable;
        this.ownerOnlyReadable = ownerOnlyReadable;
        this.readable = readable;
        this.ownerOnlyWritable = ownerOnlyWritable;
        this.writable = writable;
    }

    public boolean isExecutable()
    {
        return executable;
    }

    public void setExecutable( boolean executable )
    {
        this.executable = executable;
    }

    public boolean isOwnerOnlyExecutable()
    {
        return ownerOnlyExecutable;
    }

    public void setOwnerOnlyExecutable( boolean ownerOnlyExecutable )
    {
        this.ownerOnlyExecutable = ownerOnlyExecutable;
    }

    public boolean isOwnerOnlyReadable()
    {
        return ownerOnlyReadable;
    }

    public void setOwnerOnlyReadable( boolean ownerOnlyReadable )
    {
        this.ownerOnlyReadable = ownerOnlyReadable;
    }

    public boolean isReadable()
    {
        return readable;
    }

    public void setReadable( boolean readable )
    {
        this.readable = readable;
    }

    public boolean isOwnerOnlyWritable()
    {
        return ownerOnlyWritable;
    }

    public void setOwnerOnlyWritable( boolean ownerOnlyWritable )
    {
        this.ownerOnlyWritable = ownerOnlyWritable;
    }

    public boolean isWritable()
    {
        return writable;
    }

    public void setWritable( boolean writable )
    {
        this.writable = writable;
    }

    public String toString()
    {
        return "FilePermission [executable=" + executable + ", ownerOnlyExecutable=" + ownerOnlyExecutable
            + ", ownerOnlyReadable=" + ownerOnlyReadable + ", readable=" + readable + ", ownerOnlyWritable="
            + ownerOnlyWritable + ", writable=" + writable + "]";
    }
    
}
