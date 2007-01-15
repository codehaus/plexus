package org.codehaus.plexus.security.ui.web.util;

/*
 * Copyright 2005-2006 The Codehaus.
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

import org.codehaus.plexus.security.rbac.Role;

import java.util.Comparator;

/**
 * RoleSorter
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class RoleSorter
    implements Comparator
{
    public int compare( Object o1, Object o2 )
    {
        if ( !( o1 instanceof Role ) )
        {
            return 0;
        }

        if ( !( o2 instanceof Role ) )
        {
            return 0;
        }

        if ( ( o1 == null ) && ( o2 == null ) )
        {
            return 0;
        }

        if ( ( o1 == null ) && ( o2 != null ) )
        {
            return -1;
        }

        if ( ( o1 != null ) && ( o2 != null ) )
        {
            return 1;
        }

        Role r1 = (Role) o1;
        Role r2 = (Role) o2;

        return r1.getName().compareTo( r2.getName() );
    }
}
