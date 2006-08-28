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


/**
 * A default implementation of the IPermissionEntry interface.
 */
public class DefaultOperation extends AbstractOperation
{
    /**
     * The target object.
     */
    public final Object obj;
    /**
     * The operation that can be performed on the target object.
     */
    public final String op;

    /**
     * Constructs with the given target object and operation.
     */
    public DefaultOperation( Object obj, String op )
    {
        this.obj = obj;
        this.op = op;
    }

    /**
     * Returns the operation of this permission entry.
     */
    public String getOperation()
    {
        return op;
    }

    /**
     * Returns the target object of this permission entry.
     */
    public Object getObject()
    {
        return obj;
    }

    public String toString()
    {
        return "\n{obj:" + obj + ", op:" + op + "}";
    }
}
