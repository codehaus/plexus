package org.codehaus.plexus.security.user.jdo;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;

/**
 * UsernamePrimaryKey - Support Object for use by JDO Implementation to track 
 * Username primary keys correctly.
 * 
 * See <a href="http://www.jpox.org/docs/1_1/primary_key.html">jpox 1.1 primary key documentation</a> for details.
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class UsernamePrimaryKey
    implements Serializable
{
    public String username;

    /**
     * Default constructor.
     */
    public UsernamePrimaryKey()
    {
        // Do nothing here.
    }

    /**
     * String constructor.
     */
    public UsernamePrimaryKey( String str )
    {
        username = str;
    }

    /**
     * Implementation of equals method.
     */
    public boolean equals( Object ob )
    {
        if ( this == ob )
            return true;

        if ( !( ob instanceof UsernamePrimaryKey ) )
            return false;

        UsernamePrimaryKey that = (UsernamePrimaryKey) ob;
        return ( ( this.username.equals( that.username ) ) );
    }

    /**
     * Implementation of hashCode method that supports the
     * equals-hashCode contract.
     */
    public int hashCode()
    {
        return this.username.hashCode();
    }

    /**
     * Implementation of toString that outputs this object id's
     * primary key values.
     */
    public String toString()
    {
        return this.username;
    }
}
