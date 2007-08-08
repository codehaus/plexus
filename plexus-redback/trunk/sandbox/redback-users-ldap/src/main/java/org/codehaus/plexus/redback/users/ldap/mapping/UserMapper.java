package org.codehaus.plexus.redback.users.ldap.mapping;

import org.codehaus.plexus.redback.users.User;
import org.codehaus.plexus.redback.users.ldap.BasicUser;

import javax.naming.directory.Attributes;

public interface UserMapper
{

    BasicUser getUser( Attributes attributes )
        throws MappingException;

    Attributes getCreationAttributes( User user, boolean encodePasswordIfChanged )
        throws MappingException;

    UserUpdate getUpdate( BasicUser user )
        throws MappingException;

    String[] getUserAttributeNames();

    String getEmailAddressAttribute();

    String getUserFullNameAttribute();

    String getPasswordAttribute();

    String getUserIdAttribute();

    String getEmailAttribute();

    String getUserBaseDn();

    String getUserObjectClass();

    BasicUser newUserInstance( String username, String fullName, String email );

    BasicUser newTemplateUserInstance();

}
