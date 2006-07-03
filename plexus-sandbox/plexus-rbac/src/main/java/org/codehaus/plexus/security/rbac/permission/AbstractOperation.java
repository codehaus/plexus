package org.codehaus.plexus.security.rbac.permission;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 * A permission entry base class that provides generic implemenation with no internal data set.
 */
public abstract class AbstractOperation 
    implements Operation, Serializable
{
    /**
     * Returns an empty string.
     */
    public String getOperation()
    {
        return "";
    }

    /**
     * Returns a dummy object.
     */
    public Object getObject()
    {
        return new Object();
    }

    /**
     * Returns true iff this permission entry is greater than or equal to
     * the given permission entry in terms of access privileges.
     */
    public boolean ge( Operation pe )
    {
        if ( pe == null )
        {
            return true;
        }
        if ( pe == this )
        {
            return true;
        }
        String op = getOperation();
        Object obj = getObject();
        String to_op = pe.getOperation();
        Object to_obj = pe.getObject();

        return ( op.equals( ALL_OPERATION ) || op.equals( to_op ) )
            && ( obj.equals( ALL_OBJECT ) || obj.equals( to_obj ) );
    }

    /**
     * Two permission entires are equal if they have the same operation and target object.
     */
    public boolean equals( Object o )
    {
        if ( o == null || !( o instanceof Operation ) )
        {
            return false;
        }
        if ( o == this )
        {
            return true;
        }
        Operation p = (Operation) o;
        return getOperation().equals( p.getOperation() ) && getObject().equals( p.getObject() );
    }

    public int hashCode()
    {
        return getOperation().hashCode() ^ getObject().hashCode();
    }
}



