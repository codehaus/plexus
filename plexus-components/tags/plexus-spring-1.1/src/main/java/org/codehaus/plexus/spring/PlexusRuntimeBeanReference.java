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
package org.codehaus.plexus.spring;

import org.springframework.beans.factory.config.RuntimeBeanReference;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 26 mars 2008
 * @version $Id$
 */
public class PlexusRuntimeBeanReference
    extends RuntimeBeanReference
{

    private String role;
    
    private String roleHint;
    
    /**
     * @param beanName
     */
    public PlexusRuntimeBeanReference( String beanName, String role, String roleHint )
    {
        super( beanName );
        this.role = role;
        this.roleHint = roleHint;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole( String role )
    {
        this.role = role;
    }

    public String getRoleHint()
    {
        return roleHint;
    }

    public void setRoleHint( String roleHint )
    {
        this.roleHint = roleHint;
    }

    
}
