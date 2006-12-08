package org.codehaus.plexus.security.ui.web.action.admin;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.plexus.security.rbac.Resource;
import org.codehaus.plexus.security.system.SecuritySystem;
import org.codehaus.plexus.security.ui.web.action.AbstractSecurityAction;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionBundle;
import org.codehaus.plexus.security.ui.web.interceptor.SecureActionException;
import org.codehaus.plexus.security.ui.web.role.profile.RoleConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * SystemInfoAction 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="com.opensymphony.xwork.Action"
 *                   role-hint="pss-sysinfo"
 *                   instantiation-strategy="per-lookup"
 */
public class SystemInfoAction
    extends AbstractSecurityAction
{
    // ------------------------------------------------------------------
    // Plexus Component Requirements
    // ------------------------------------------------------------------

    /**
     * @plexus.requirement
     */
    private SecuritySystem securitySystem;

    private static final List ignoredReaders;

    private static final String NULL = "&lt;null&gt;";

    private static final char LN = Character.LINE_SEPARATOR;

    static
    {
        ignoredReaders = new ArrayList();
        ignoredReaders.add( "class" );
    }

    // ------------------------------------------------------------------
    // Action Parameters
    // ------------------------------------------------------------------

    private StringBuffer details;

    // ------------------------------------------------------------------
    // Action Entry Points - (aka Names)
    // ------------------------------------------------------------------

    public String show()
    {
        details = new StringBuffer();

        details.append( "SecuritySystem: " );
        dumpObject( details, securitySystem, "  " );

        return SUCCESS;
    }

    private void dumpObject( StringBuffer sb, Object obj, String indent )
    {
        dumpObject( new ArrayList(), sb, obj, indent );
    }

    private void dumpObject( List seenObjects, StringBuffer sb, Object obj, String indent )
    {
        if ( obj == null )
        {
            sb.append( NULL ).append( LN );
            return;
        }

        sb.append( indent ).append( "\\ " ).append( obj.getClass().getName() ).append( LN );

        try
        {
            Map readers = PropertyUtils.describe( obj );
            Iterator it = readers.entrySet().iterator();
            while ( it.hasNext() )
            {
                Map.Entry readerEntry = (Entry) it.next();
                String name = (String) readerEntry.getKey();

                if ( ignoredReaders.contains( name ) )
                {
                    // skip this reader.
                    continue;
                }

                sb.append( indent );
                sb.append( name ).append( ":" );

                Object value = readerEntry.getValue();
                if ( value == null )
                {
                    sb.append( NULL ).append( LN );
                }
                else
                {
                    String className = value.getClass().getName();
                    sb.append( "(" ).append( className ).append( ") " );
                    sb.append( StringEscapeUtils.escapeHtml( value.toString() ) ).append( LN );

                    if ( !className.startsWith( "java." ) && !className.startsWith( "javax." ) )
                    {
                        // prevent cycles
                        if ( !seenObjects.contains( value ) )
                        {
                            dumpObject( seenObjects, sb, value, indent + "  " );
                        }
                        else
                        {
                            seenObjects.add( value );
                        }
                    }
                }
            }
        }
        catch ( Throwable e )
        {
            sb.append( "Unable to read bean [" + obj.getClass().getName() + "]: " + e.getMessage() + " ("
                + e.getClass().getName() + ")" );
            getLogger().warn( "dumpobject", e );
        }
    }

    // ------------------------------------------------------------------
    // Parameter Accessor Methods
    // ------------------------------------------------------------------

    public String getDetails()
    {
        return details.toString();
    }

    public SecureActionBundle initSecureActionBundle()
        throws SecureActionException
    {
        SecureActionBundle bundle = new SecureActionBundle();
        bundle.setRequiresAuthentication( true );
        bundle.addRequiredAuthorization( RoleConstants.CONFIGURATION_EDIT_OPERATION, Resource.GLOBAL );
        return bundle;
    }
}
