/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.cli;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class NoArgOptionTest
    extends TestCase
{

    public void testShouldConstructWithRequiredShortNameLongNameDescriptionAndObjectProperty() {
        NoArgOption option = new NoArgOption(true, new Character('t'), "test", "testDescription", "testProperty");
        
        assertTrue(option.isRequired());
        assertEquals(new Character('t'), option.getShortName());
        assertEquals("test", option.getLongName());
        assertEquals("testProperty", option.getObjectProperty());
    }
    
    public void testShouldReturnFalseForIsSatisfiedWhenRequiredAndNotSet() {
        NoArgOption option = new NoArgOption(true, new Character('t'), "test", "testDescription", "testProperty");
        
        assertFalse(option.isSatisfied());
    }
    
    public void testShouldReturnTrueForIsSatisfiedWhenRequiredAndSet() {
        NoArgOption option = new NoArgOption(true, new Character('t'), "test", "testDescription", "testProperty");
        
        option.set();
        
        assertTrue(option.isSatisfied());
    }
    
}
