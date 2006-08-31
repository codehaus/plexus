package org.codehaus.plexus.security.authorization;

import org.codehaus.plexus.security.user.User;

/**
 * @author Jason van Zyl
 */
public interface AuthorizationDataSource
{
    String ROLE = AuthorizationDataSource.class.getName();

    Object getPrincipal();

    User getUser();
}
