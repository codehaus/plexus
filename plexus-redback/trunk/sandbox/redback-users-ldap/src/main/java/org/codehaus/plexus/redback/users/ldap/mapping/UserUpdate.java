package org.codehaus.plexus.redback.users.ldap.mapping;

import javax.naming.directory.Attributes;

public class UserUpdate
{

    private final Attributes created;

    private final Attributes modified;

    private final Attributes removed;

    public UserUpdate( Attributes created, Attributes modified, Attributes removed )
    {
        this.created = created;
        this.modified = modified;
        this.removed = removed;
    }

    public Attributes getAddedAttributes()
    {
        return created;
    }

    public Attributes getModifiedAttributes()
    {
        return modified;
    }

    public Attributes getRemovedAttributes()
    {
        return removed;
    }

    public boolean hasAdditions()
    {
        return ( created != null ) && ( created.size() > 0 );
    }

    public boolean hasModifications()
    {
        return ( modified != null ) && ( modified.size() > 0 );
    }



}
