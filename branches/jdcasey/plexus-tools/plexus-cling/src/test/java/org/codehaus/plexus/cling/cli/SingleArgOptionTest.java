/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.cli;

import junit.framework.TestCase;

public class SingleArgOptionTest
    extends TestCase
{

    public void testShouldConstructWithRequiredShortNameLongNameValueFormatObjectPropertyAndDescription()
    {
        SingleArgOption option = new SingleArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT,
            "test description", "testProperty" );

        assertEquals("test", option.getLongName());
        assertEquals(new Character('t'), option.getShortName());
        assertEquals("testProperty", option.getObjectProperty());
        assertTrue(option.isRequired());
    }
    
    public void testShouldSetValidValueAndReturnIsValidEqualTrue() {
        SingleArgOption option = new SingleArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT,
            "test description", "testProperty" );

        assertTrue(option.isValueValid("true"));
    }
    
    public void testShouldReturnTrueForSatisfiedWhenNotRequiredWithNoValue() {
        SingleArgOption option = new SingleArgOption( false, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT,
            "test description", "testProperty" );

        assertTrue(option.isSatisfied());
    }
    
    public void testShouldReturnFalseForSatisfiedWhenRequiredWithNoValue() {
        SingleArgOption option = new SingleArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT,
            "test description", "testProperty" );

        assertFalse(option.isSatisfied());
    }
    
    public void testShouldReturnObjectBooleanWhenBooleanFormatSpecified() {
        SingleArgOption option = new SingleArgOption( true, new Character( 't' ), "test", OptionFormat.BOOLEAN_FORMAT,
            "test description", "testProperty" );

        option.setValue("true");
        
        Object result = option.getValue();
        assertTrue(result instanceof Boolean);
    }
    
}