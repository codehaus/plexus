package org.codehaus.plexus.formica.action;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.action.Action;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class PersonApplicationTest
    extends PlexusTestCase
{
    public void testPersonApplication()
        throws Exception
    {
        PersonApplication pa = (PersonApplication) lookup( PersonApplication.ROLE );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        Action addAction = (Action) lookup( Action.ROLE, "addEntity" );

        assertNotNull( addAction );

        Map m = new HashMap();

        m.put( "id", "1" );

        m.put( "formId", "person" );

        m.put( "firstName", "jason" );

        m.put( "lastName", "van Zyl" );

        addAction.execute( m );

        Person p = pa.getPerson( "1" );

        assertNotNull( p );

        assertEquals( "jason", p.getFirstName() );

        assertEquals( "van Zyl", p.getLastName() );

        // ----------------------------------------------------------------------
        // Now update the entity
        // ----------------------------------------------------------------------

        Action updateAction = (Action) lookup( Action.ROLE, "updateEntity" );

        assertNotNull( updateAction );

        m = new HashMap();

        m.put( "id", "1" );

        m.put( "formId", "person" );

        m.put( "firstName", "sarel" );

        m.put( "lastName", "van Zyl" );

        updateAction.execute( m );

        p = pa.getPerson( "1" );

        assertNotNull( p );

        assertEquals( "sarel", p.getFirstName() );

        assertEquals( "van Zyl", p.getLastName() );
    }
}