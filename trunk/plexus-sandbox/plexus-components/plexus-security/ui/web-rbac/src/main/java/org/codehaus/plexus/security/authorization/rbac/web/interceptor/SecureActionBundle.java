package org.codehaus.plexus.security.authorization.rbac.web.interceptor;

import java.util.List;
import java.util.ArrayList;
/*
 * Copyright 2005 The Codehaus.
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
 * SecureActionBundle:
 *
 * @author: Jesse McConnell <jesse@codehaus.org>
 * @version: $ID:$
 */
public class SecureActionBundle
{
    private boolean requiresAuthentication;

    private List authorizationTuples = new ArrayList();


    public void requiresAuthorization( String operation, String resource )
        throws SecureActionException
    {
        if ( operation != null && resource != null )
        {
            authorizationTuples.add( new AuthorizationTuple( operation, resource ) );
        }
        else
        {
            throw new SecureActionException( "operation and resource are required to be non-null" );
        }
    }


    public List getAuthorizationTuples()
    {
        return authorizationTuples;
    }

    public boolean requiresAuthentication()
    {
        return requiresAuthentication;
    }

    public void setRequiresAuthentication( boolean requiresAuthentication )
    {
        this.requiresAuthentication = requiresAuthentication;
    }


    public class AuthorizationTuple
    {
        private String operation;

        private String resource;

        public AuthorizationTuple( String operation, String resource )
        {
            this.operation = operation;
            this.resource = resource;
        }

        public String getOperation()
        {
            return operation;
        }

        public String getResource()
        {
            return resource;
        }


        public String toString()
        {
            return "AuthorizationTuple[" + operation + "," + resource + "]";
        }
    }
}