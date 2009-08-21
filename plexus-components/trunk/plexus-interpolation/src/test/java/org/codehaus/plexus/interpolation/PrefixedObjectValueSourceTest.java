package org.codehaus.plexus.interpolation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class PrefixedObjectValueSourceTest
    extends TestCase
{
    
    public void testEmptyExpressionResultsInNullReturn_NoPrefixUsed()
    {
        String target = "Target object";
        
        List prefixes = new ArrayList();
        prefixes.add( "target" );
        prefixes.add( "object" );
        
        PrefixedObjectValueSource vs = new PrefixedObjectValueSource( prefixes, target, true );
        Object result = vs.getValue( "" );
        
        assertNull( result );
    }

    public void testEmptyExpressionResultsInNullReturn_PrefixUsedWithDot()
    {
        String target = "Target object";
        
        List prefixes = new ArrayList();
        prefixes.add( "target" );
        prefixes.add( "object" );
        
        PrefixedObjectValueSource vs = new PrefixedObjectValueSource( prefixes, target, true );
        Object result = vs.getValue( "target." );
        
        assertNull( result );
    }

    public void testEmptyExpressionResultsInNullReturn_PrefixUsedWithoutDot()
    {
        String target = "Target object";
        
        List prefixes = new ArrayList();
        prefixes.add( "target" );
        prefixes.add( "object" );
        
        PrefixedObjectValueSource vs = new PrefixedObjectValueSource( prefixes, target, true );
        Object result = vs.getValue( "target" );
        
        assertNull( result );
    }

}
