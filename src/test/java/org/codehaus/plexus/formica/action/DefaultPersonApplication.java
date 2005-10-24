/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.formica.action;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DefaultPersonApplication
    implements PersonApplication
{
    private Map people = new HashMap();

    public void addPerson( Person person )
    {
        people.put( person.getId(), person );
    }

    public void updatePerson( Person person )
    {
        people.put( person.getId(), person );
    }

    public void deletePerson( String id )
    {
        people.remove( id );
    }

    public Person getPerson( String id )
    {
        return (Person) people.get( id );
    }
}
