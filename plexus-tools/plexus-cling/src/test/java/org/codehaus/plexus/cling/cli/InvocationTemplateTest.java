/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.cli;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class InvocationTemplateTest
    extends MockObjectTestCase
{
    
    public void testShouldConstructWithListOfOptions() {
        List options = new LinkedList();
        
        InvocationTemplate template = new InvocationTemplate(options);
    }

    public void testShouldReturnFalseForRequiredOptionsWhenNoneAreRequired() {
        List options = new LinkedList();
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        assertFalse(template.hasRequiredOptions());
    }

    public void testShouldReturnTrueForRequiredOptionsWhenAnyAreRequired() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(once()).method("isRequired").withNoArguments().will(returnValue(true));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        assertTrue(template.hasRequiredOptions());
    }

    public void testShouldReturnTrueForIsSatisfiedWhenNoneAreRequired() {
        List options = new LinkedList();
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        assertTrue(template.isSatisfied());
    }

    public void testShouldReturnFalseForIsSatisfiedWhenAnyAreRequiredAndNoneAreSet() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(once()).method("isRequired").withNoArguments().will(returnValue(true));
        optionMock.expects(once()).method("isSatisfied").withNoArguments().will(returnValue(false));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        assertFalse(template.isSatisfied());
    }

    public void testShouldReturnIntOneForScoreRequirementsWhenOneIsRequiredAndNoneAreSet() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(atLeastOnce()).method("isRequired").withNoArguments().will(returnValue(true));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        assertEquals(1, template.scoreRequirements(Collections.EMPTY_MAP));
    }

    public void testShouldReturnIntZeroForScoreRequirementsWhenNoneAreRequired() {
        List options = new LinkedList();
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        assertEquals(0, template.scoreRequirements(Collections.EMPTY_MAP));
    }

    public void testShouldReturnUnsatisfiedSizeOneWhenOneIsRequiredAndNoneAreSet() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(atLeastOnce()).method("isRequired").withNoArguments().will(returnValue(true));
        optionMock.expects(once()).method("isSatisfied").withNoArguments().will(returnValue(false));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Set unsat = template.getUnsatisfiedOptions();
        assertEquals(1, unsat.size());
        assertTrue(unsat.contains(optionMock.proxy()));
    }

    public void testShouldReturnUnsatisfiedSizeZeroWhenAllRequiredAreSet() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(atLeastOnce()).method("isRequired").withNoArguments().will(returnValue(true));
        optionMock.expects(atLeastOnce()).method("isSatisfied").withNoArguments().will(returnValue(true));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Set unsat = template.getUnsatisfiedOptions();
        assertEquals(0, unsat.size());
        assertFalse(unsat.contains(optionMock.proxy()));
    }

    public void testShouldReturnOptionByShortName() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(atLeastOnce()).method("isRequired").withNoArguments().will(returnValue(true));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Option option = template.getOption(new Character('t'));
        
        assertEquals(optionMock.proxy(), option);
    }

    public void testShouldReturnOptionByLongName() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(atLeastOnce()).method("isRequired").withNoArguments().will(returnValue(true));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Option option = template.getOption("test");
        
        assertEquals(optionMock.proxy(), option);
    }

    public void testShouldReturnSizeOfOneWhenOneOptionSupplied() {
        List options = new LinkedList();
        
        Mock optionMock = mock(Option.class);
        optionMock.expects(atLeastOnce()).method("getShortName").withNoArguments().will(returnValue(new Character('t')));
        optionMock.expects(atLeastOnce()).method("getLongName").withNoArguments().will(returnValue("test"));
        optionMock.expects(atLeastOnce()).method("isRequired").withNoArguments().will(returnValue(true));
        
        options.add(optionMock.proxy());
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        assertEquals(1, template.size());
    }
    
    public void testShouldSetSuppliedOptionWhenSetValuesCalled() {
        List options = new LinkedList();
        
        NoArgOption option = new NoArgOption(true, new Character('t'), "test", "description", "property");
        
        options.add(option);
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Map values = new TreeMap();
        values.put("test", Boolean.TRUE);
        
        template.setValues(values);
        
        assertTrue(option.isSet());
    }

    public void testShouldReturnMappingWithOptionPropertiesToValues() {
        List options = new LinkedList();
        
        NoArgOption option = new NoArgOption(true, new Character('t'), "test", "description", "property");
        
        options.add(option);
        
        InvocationTemplate template = new InvocationTemplate(options);
        
        Map values = new TreeMap();
        values.put("test", Boolean.TRUE);
        
        template.setValues(values);
        
        Map propMappings = template.getOptionPropertyMappings();
        
        assertTrue(Boolean.TRUE == propMappings.get("property"));
    }

}
