package org.codehaus.plexus.security.authorization.rbac.web.interceptor;

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

import java.util.List;

/**
 * SecureAction
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id: SecureAction.java 4035 2006-09-14 12:59:40Z joakime $
 */
public interface SecureAction
{


    /**
     * the list of the operations required for this secured action to execute
     *
     * NOTE: any _one_ of these operations is required
     * @return
     */
    public List getRequiredOperations()
        throws SecureActionException;

    /**
     * the resource that that the operations are tests against for authz
     *
     * either a resource string or Resource.GLOBAL
     */
    public String getRequiredResource()
        throws SecureActionException;


    /**
     * return true of this secured action requires authentication
     * @return
     */
    public boolean authenticationRequired()
        throws SecureActionException;
}
