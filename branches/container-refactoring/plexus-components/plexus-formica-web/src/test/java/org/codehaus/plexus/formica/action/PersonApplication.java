/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.codehaus.plexus.formica.action;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface PersonApplication
{
    String ROLE = PersonApplication.class.getName();

    void addPerson( Person person );

    void updatePerson( Person person );

    void deletePerson( String id );
    
    Person getPerson( String id );
}
