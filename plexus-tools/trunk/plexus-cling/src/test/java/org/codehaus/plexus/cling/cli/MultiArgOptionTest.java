/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.cli;

import junit.framework.TestCase;

public class MultiArgOptionTest
    extends TestCase
{

    public void testShouldConstructWithRequiredShortNameLongNameValueFormatObjectPropertyAndDescription()
    {
        MultiArgOption option = new MultiArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT, ",", 
            "test description", "testProperty" );

        assertEquals("test", option.getLongName());
        assertEquals(new Character('t'), option.getShortName());
        assertEquals("testProperty", option.getObjectProperty());
        assertTrue(option.isRequired());
    }
    
    public void testShouldSetValidValueAndReturnIsValidEqualTrue() {
        MultiArgOption option = new MultiArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT, ",", 
            "test description", "testProperty" );

        assertTrue(option.isValueValid("true"));
    }
    
    public void testShouldReturnTrueForSatisfiedWhenNotRequiredWithNoValue() {
        MultiArgOption option = new MultiArgOption( false, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT, ",", 
            "test description", "testProperty" );

        assertTrue(option.isSatisfied());
    }
    
    public void testShouldReturnFalseForSatisfiedWhenRequiredWithNoValue() {
        MultiArgOption option = new MultiArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT, ",", 
            "test description", "testProperty" );

        assertFalse(option.isSatisfied());
    }
    
    public void testShouldReturnObjectBooleanListWhenBooleanFormatSpecified() {
        MultiArgOption option = new MultiArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT, ",", 
            "test description", "testProperty" );

        option.setValue("true,false");
        
        Object[] result = option.getValues();
        System.out.println("Results: " + result);
        
        assertNotNull(result);
        for ( int i = 0; i < result.length; i++ )
        {
            Object r = result[i];
            System.out.println("Result[" + i + "]: " + r.getClass().getName());
            assertTrue(r instanceof Boolean);
        }
    }
    
}