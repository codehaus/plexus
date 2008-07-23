package org.codehaus.plexus.ldap.helper.factory;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import java.util.Hashtable;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface LdapFactoryHelper
{
    String ROLE = LdapFactoryHelper.class.getName();

    ObjectClassDescriptor getObjectClass( String objectClassName )
        throws LdapFactoryHelperException;

    ObjectClassDescriptor getAggregatedObjectClass( String[] requiredObjectClasses )
        throws LdapFactoryHelperException;
}
