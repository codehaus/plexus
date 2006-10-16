/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.cli;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class ListOptionFormatTest
    extends TestCase
{
    
    public void testShouldConstructWithSplitPatternAndOptionFormatForSubs() {
        ListOptionFormat fmt = new ListOptionFormat(",", OptionFormat.BOOLEAN_FORMAT);
    }

    public void testShouldReturnTrueForIsValidWhenInvalidValueComponentExists_AssumeBooleanFalse() {
        ListOptionFormat fmt = new ListOptionFormat(",", OptionFormat.BOOLEAN_FORMAT);
        
        assertTrue(fmt.isValid("true,xxx"));
    }
    
    public void testShouldReturnTrueForIsValidWhenAllValueComponentsAreValid() {
        ListOptionFormat fmt = new ListOptionFormat(",", OptionFormat.BOOLEAN_FORMAT);
        
        assertTrue(fmt.isValid("true,false"));
    }
    
    public void testShouldRetrieveComponentArrayWhenAllValueComponentsAreValid() {
        ListOptionFormat fmt = new ListOptionFormat(",", OptionFormat.BOOLEAN_FORMAT);
        
        Object[] result = (Object[])fmt.getValue("true,false");
        assertNotNull(result);
        for ( int i = 0; i < result.length; i++ )
        {
            Object r = result[i];
            assertTrue(r instanceof Boolean);
        }
    }
    
}
