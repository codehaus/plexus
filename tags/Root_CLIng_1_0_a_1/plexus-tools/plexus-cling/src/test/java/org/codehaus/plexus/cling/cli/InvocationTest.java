/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.cli;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class InvocationTest
    extends TestCase
{

    public void testShouldConstructWithDescriptionOnly() {
        Invocation inv = new Invocation("description");
    }
    
    public void testShouldConstructWithDescriptionAndArgumentDescription() {
        Invocation inv = new Invocation("description", "<args>");
    }
    
    public void testShouldParseSingleLongOptionAndHaveNoArgsAndOneOptionSet() 
    throws InvocationException 
    {
        SingleArgOption option = new SingleArgOption(true, new Character('t'), "test", OptionFormat.BOOLEAN_FORMAT, "description", "property");
        
        Set options = new HashSet();
        options.add(option);
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Invocation inv = new Invocation("description");
        inv.addInvocationTemplate(template);
        
        String[] args = {
            "--test=true"
        };
        
        inv.parseArgs(args);
        
        assertEquals(0, inv.getArgumentCount());
        assertTrue(option.isSatisfied());
        assertEquals(Boolean.TRUE, option.getValue());
    }
    
    public void testShouldParseSingleShortOptionAndHaveNoArgsAndOneOptionSet() 
    throws InvocationException 
    {
        SingleArgOption option = new SingleArgOption(true, new Character('t'), "test", OptionFormat.BOOLEAN_FORMAT, "description", "property");
        
        Set options = new HashSet();
        options.add(option);
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Invocation inv = new Invocation("description");
        inv.addInvocationTemplate(template);
        
        String[] args = {
            "-t", "true"
        };
        
        inv.parseArgs(args);
        
        assertEquals(0, inv.getArgumentCount());
        assertTrue(option.isSatisfied());
        assertEquals(Boolean.TRUE, option.getValue());
    }
    
    public void testShouldParseSingleShortOptionAndOneArgumentAndHaveOneArgsAndOneOptionSet() 
    throws InvocationException 
    {
        SingleArgOption option = new SingleArgOption(true, new Character('t'), "test", OptionFormat.BOOLEAN_FORMAT, "description", "property");
        
        Set options = new HashSet();
        options.add(option);
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Invocation inv = new Invocation("description");
        inv.addInvocationTemplate(template);
        
        String[] args = {
            "-t", "true", "argument"
        };
        
        inv.parseArgs(args);
        
        assertEquals(1, inv.getArgumentCount());
        assertTrue(inv.getArguments().contains("argument"));
        assertTrue(option.isSatisfied());
        assertEquals(Boolean.TRUE, option.getValue());
    }
    
    public void testShouldFailWhenNoTemplatesWereAdded()
    throws InvocationException 
    {
        Invocation inv = new Invocation("description");
        
        try {
            inv.parseArgs(new String[0]);
            fail("should fail because of empty invocation templates list");
        }
        catch(IllegalStateException e) {
            // should fail
        }
    }
    
    public void testShouldFailWhenOneTemplateWasAddedThenRemoved()
    throws InvocationException 
    {
        InvocationTemplate template = new InvocationTemplate(Collections.EMPTY_SET);
        
        Invocation inv = new Invocation("description");
        inv.addInvocationTemplate(template);
        inv.removeInvocationTemplate(template);
        
        try {
            inv.parseArgs(new String[0]);
            fail("should fail because of empty invocation templates list");
        }
        catch(IllegalStateException e) {
            // should fail
        }
    }
    
    public void testShouldParseEmptyArgsAndHaveNoArgsAndNoOptionsSet() 
    throws InvocationException 
    {
        SingleArgOption option = new SingleArgOption(false, new Character('t'), "test", OptionFormat.BOOLEAN_FORMAT, "description", "property");
        
        Set options = new HashSet();
        options.add(option);
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Invocation inv = new Invocation("description");
        inv.addInvocationTemplate(template);
        
        inv.parseArgs(new String[0]);
        
        assertEquals(0, inv.getArgumentCount());
        assertFalse(option.hasValue());
    }
    
}
