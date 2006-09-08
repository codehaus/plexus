package org.codehaus.plexus.security.example.web.action;

import org.codehaus.plexus.xwork.action.PlexusActionSupport;
/*
 * Copyright 2005 The Apache Software Foundation.
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
 * PlexusSecuritySystemAction:
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id:$
 *
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="status"
 */
public class StatusAction
    extends PlexusActionSupport
{
    /**
     * @ plexus.requirement
     */
    //private SecuritySystem securitySystem;

    public String status()
    {
        return SUCCESS;
    }
}
