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
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.security.configuration.UserConfiguration;
import org.codehaus.plexus.security.rbac.RBACManager;
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
import java.util.Set;
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

    /**
     * @plexus.requirement
     */
    private UserConfiguration configuration;

    /**
     * @plexus.requirement
     */
    private RBACManager rbacManager;

    private static final List ignoredReaders;

    private static final String NULL = "&lt;null&gt;";

    private static final char LN = Character.LINE_SEPARATOR;

    private static final String INDENT = "  ";

    private static final int MAXDEPTH = 10;

    static
    {
        ignoredReaders = new ArrayList();
        // Ignoring Class.getClass()
        ignoredReaders.add( "class" );
        // Found in some jpox classes.
        ignoredReaders.add( "copy" );
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

        details.append( "Configuration: " );
        dumpObject( details, configuration, INDENT );
        configuration.dumpState( details );
        details.append( LN );

        details.append( LN ).append( "<hr/>" ).append( LN );
        details.append( "RBAC Manager: " );
        dumpObject( details, rbacManager, INDENT );

        details.append( LN ).append( "<hr/>" ).append( LN );
        details.append( "SecuritySystem: " );
        dumpObject( details, securitySystem, INDENT );

        return SUCCESS;
    }

    private void dumpObject( StringBuffer sb, Object obj, String indent )
    {
        dumpObjectSwitchboard( new ArrayList(), sb, obj, indent, 0 );
    }

    /**
     * The recursive object dumping switchboard.
     * 
     * @param seenObjects objects already seen (to prevent cycles)
     * @param sb the stringbuffer to populate
     * @param obj the object to dump
     * @param indent the current indent string.
     * @param depth the depth in the tree.
     */
    private void dumpObjectSwitchboard( List seenObjects, StringBuffer sb, Object obj, String indent, int depth )
    {
        if ( obj == null )
        {
            sb.append( NULL ).append( LN );
            return;
        }

        if( depth > MAXDEPTH )
        {
            sb.append( StringEscapeUtils.escapeHtml( "<MAX DEPTH>" ) );
            sb.append( LN );
            return;
        }
        
        depth++;
        
        String className = obj.getClass().getName();

        sb.append( "(" ).append( className ).append( ") " );

        if ( obj instanceof List )
        {
            dumpIterator( seenObjects, sb, ( (List) obj ).iterator(), indent, depth );
        }
        else if ( obj instanceof Set )
        {
            dumpIterator( seenObjects, sb, ( (Set) obj ).iterator(), indent, depth );
        }
        else if ( obj instanceof Map )
        {
            dumpIterator( seenObjects, sb, ( (Map) obj ).entrySet().iterator(), indent, depth );
        }
        else if ( obj instanceof Iterator )
        {
            dumpIterator( seenObjects, sb, (Iterator) obj, indent, depth );
        }
        else
        {
            // Filter classes that start with java or javax
            if ( className.startsWith( "java." ) || className.startsWith( "javax." ) )
            {
                sb.append( StringEscapeUtils.escapeHtml( obj.toString() ) ).append( LN );
                return;
            }

            // prevent cycles
            if ( seenObjects.contains( obj ) )
            {
                // No need to dump.
                sb.append( StringEscapeUtils.escapeHtml( "<seen already preventing cycle in dump> " ) );
                sb.append( LN );
                return;
            }

            // Adding object to seen list (to prevent cycles)
            seenObjects.add( obj );

            dumpObjectReaders( seenObjects, sb, obj, indent, depth );
        }
        depth--;
    }

    private void dumpObjectReaders( List seenObjects, StringBuffer sb, Object obj, String indent, int depth )
    {
        sb.append( obj.toString() ).append( LN );
        String name = null;

        try
        {
            Map readers = PropertyUtils.describe( obj );
            Iterator it = readers.entrySet().iterator();
            while ( it.hasNext() )
            {
                Map.Entry readerEntry = (Entry) it.next();
                name = (String) readerEntry.getKey();

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
                    dumpObjectSwitchboard( seenObjects, sb, value, INDENT + indent, depth );
                }
            }
        }
        catch ( Throwable e )
        {
            sb.append( LN ).append( indent );
            sb.append( "Unable to read bean [" ).append( obj.getClass().getName() );
            if ( StringUtils.isNotBlank( name ) )
            {
                sb.append( ".get" ).append( StringUtils.capitalize( name ) ).append( "()" );
            }
            sb.append( "]: " ).append( "(" ).append( e.getClass().getName() ).append( ") ");
            sb.append( e.getMessage() ).append( LN );
        }
    }

    private void dumpIterator( List seenObjects, StringBuffer sb, Iterator iterator, String indent, int depth )
    {
        sb.append( LN );
        while ( iterator.hasNext() )
        {
            Object entry = iterator.next();
            sb.append( indent );
            dumpObjectSwitchboard( seenObjects, sb, entry, indent + " | ", depth );
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
