package org.codehaus.plexus.redback.users.ldap;

/*
 * Copyright 2001-2007 The Codehaus.
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

import org.codehaus.plexus.redback.users.UserQuery;
import org.codehaus.plexus.redback.users.ldap.mapping.UserMapper;

public class LdapUserQuery
    implements UserQuery
{

    private final UserMapper mapper;

    private BasicUser template;

    private String orderBy;

    private Integer maxResults;

    private Integer firstResult;

    private Boolean ascending;

    public LdapUserQuery( UserMapper mapper )
    {
        this.mapper = mapper;
        template = mapper.newTemplateUserInstance();
    }

    public long getFirstResult()
    {
        return firstResult;
    }

    public long getMaxResults()
    {
        return maxResults;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public boolean isAscending()
    {
        return ascending;
    }

    public void setAscending( boolean ascending )
    {
        this.ascending = ascending;
    }

    public void setFirstResult( int firstResult )
    {
        this.firstResult = firstResult;
        throw new UnsupportedOperationException( "Result limiting is not yet supported for LDAP." );
    }

    public void setMaxResults( int maxResults )
    {
        this.maxResults = maxResults;
        throw new UnsupportedOperationException( "Result limiting is not yet supported for LDAP." );
    }

    public void setOrderBy( String orderBy )
    {
        this.orderBy = orderBy;
        throw new UnsupportedOperationException( "Free-form ordering is not yet supported for LDAP." );
    }

    public String getEmail()
    {
        return template.getEmail();
    }

    public String getFullName()
    {
        return template.getFullName();
    }

    public String getUsername()
    {
        return template.getUsername();
    }

    public void setEmail( String address )
    {
        template.setEmail( address );
    }

    public void setFullName( String name )
    {
        template.setFullName( name );
    }

    public void setUsername( String name )
    {
        template.setUsername( name );
    }

    public String[] getUserAttributeNames()
    {
        return mapper.getUserAttributeNames();
    }

}
