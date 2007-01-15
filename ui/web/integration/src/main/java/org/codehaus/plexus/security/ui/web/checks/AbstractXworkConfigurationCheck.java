package org.codehaus.plexus.security.ui.web.checks;

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

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import org.codehaus.plexus.security.ui.web.checks.xwork.XworkActionConfig;
import org.codehaus.plexus.security.ui.web.checks.xwork.XworkPackageConfig;
import org.codehaus.plexus.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * AbstractXworkConfigurationCheck
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class AbstractXworkConfigurationCheck
{

    protected void checkAction( List violations, XworkPackageConfig expectedPackage, XworkActionConfig expectedAction,
                                Map xwActionMap )
    {
        ActionConfig xwActionConfig = (ActionConfig) xwActionMap.get( expectedAction.name );
        if ( xwActionConfig != null )
        {
            if ( StringUtils.isNotEmpty( expectedAction.clazz ) )
            {
                if ( !StringUtils.equals( expectedAction.clazz, xwActionConfig.getClassName() ) )
                {
                    violations.add( "xwork.xml - Expected class attribute value of " + quote( expectedAction.clazz ) +
                        " but got " + quote( xwActionConfig.getClassName() ) + " instead, on action " +
                        quote( expectedAction.name ) + " in package " + quote( expectedPackage.name ) + "." );
                }
            }

            if ( StringUtils.isNotEmpty( expectedAction.method ) )
            {
                if ( !StringUtils.equals( expectedAction.method, xwActionConfig.getMethodName() ) )
                {
                    violations.add( "xwork.xml - Expected method attribute value of " + quote( expectedAction.method ) +
                        " but got " + quote( xwActionConfig.getMethodName() ) + " instead, on action " +
                        quote( expectedAction.name ) + " in package " + quote( expectedPackage.name ) + "." );
                }
            }

            Map xwResultMap = xwActionConfig.getResults();

            if ( expectedAction.results.isEmpty() )
            {
                // Check for single default result.
                if ( xwResultMap.size() < 1 )
                {
                    violations.add( "xwork.xml - Missing default result on action name " +
                        quote( expectedAction.name ) + " in package " + quote( expectedPackage.name ) + "." );
                }
            }
            else
            {
                // Check for named result names.
                Iterator it = expectedAction.results.iterator();
                while ( it.hasNext() )
                {
                    String resultName = (String) it.next();

                    if ( xwResultMap.get( resultName ) == null )
                    {
                        violations.add( "xwork.xml - Missing named result " + quote( resultName ) + " in action " +
                            quote( expectedAction.name ) + " in package " + quote( expectedPackage.name ) + "." );
                    }
                }
            }
        }
        else
        {
            violations.add( "xwork.xml - Missing action named " + quote( expectedAction.name ) + " in package " +
                quote( expectedPackage.name ) + "." );
        }
    }

    protected void checkPackage( List violations, XworkPackageConfig expectedPackage, Configuration xwConfig )
    {
        PackageConfig xwPackageConfig = findPackageNamespace( xwConfig, expectedPackage.name );

        if ( xwPackageConfig != null )
        {
            Map xwActionMap = xwPackageConfig.getActionConfigs();

            Iterator it = expectedPackage.actions.iterator();
            while ( it.hasNext() )
            {
                XworkActionConfig expectedAction = (XworkActionConfig) it.next();
                checkAction( violations, expectedPackage, expectedAction, xwActionMap );
            }
        }
        else
        {
            violations.add( "Missing " + quote( expectedPackage.name ) + " package namespace in xwork.xml" );
        }
    }

    protected PackageConfig findPackageNamespace( Configuration xwConfig, String name )
    {
        Map xwPackageConfigMap = xwConfig.getPackageConfigs();

        Iterator it = xwPackageConfigMap.values().iterator();
        while ( it.hasNext() )
        {
            PackageConfig xwPackageConfig = (PackageConfig) it.next();
            if ( StringUtils.equals( name, xwPackageConfig.getNamespace() ) )
            {
                return xwPackageConfig;
            }
        }

        return null;
    }

    protected String quote( Object o )
    {
        if ( o == null )
        {
            return "<null>";
        }
        return "\"" + o.toString() + "\"";
    }

}
