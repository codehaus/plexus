package org.codehaus.plexus.formica.population;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class OgnlPopulatorTest
    extends TestCase
{
    OgnlPopulator populator;

    public OgnlPopulatorTest( final String s )
    {
        super( s );
    }

    public void setUp()
    {
        populator = new OgnlPopulator();
    }

    public void testOgnlPopulatorWithFlatObject()
        throws Exception
    {
        final Map expressionDataMap = new HashMap();

        expressionDataMap.put( "a", "A" );
        expressionDataMap.put( "b", "B" );
        expressionDataMap.put( "c", "C" );
        expressionDataMap.put( "d", "D" );
        expressionDataMap.put( "e", "E" );

        final FlatObject flatObject = new FlatObject();

        populator.populate( flatObject, expressionDataMap );

        assertEquals( "A", flatObject.getA() );
        assertEquals( "B", flatObject.getB() );
        assertEquals( "C", flatObject.getC() );
        assertEquals( "D", flatObject.getD() );
        assertEquals( "E", flatObject.getE() );
    }

    public void testOgnlPopulatorWithObjectGraph()
        throws Exception
    {
        final Map expressionDataMap = new HashMap();

        expressionDataMap.put( "address.street", "50 King" );
        expressionDataMap.put( "address.country", "CA" );

        final Person person = new Person();

        populator.populate( person, expressionDataMap );

        assertEquals( "50 King", person.getAddress().getStreet() );
        assertEquals( "CA", person.getAddress().getCountry() );
    }
}
