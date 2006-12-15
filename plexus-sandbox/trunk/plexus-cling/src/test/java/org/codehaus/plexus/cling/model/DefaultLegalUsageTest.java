/* Created on Sep 16, 2004 */
package org.codehaus.plexus.cling.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.cling.cli.InvocationTemplate;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class DefaultLegalUsageTest
    extends TestCase
{
    
    public void testShouldConstructWithNoArgs() {
        DefaultLegalUsage usage = new DefaultLegalUsage();
    }

    public void testShouldAddOneTemplateAndRetrieveCollectionOfSizeOne() {
        DefaultLegalUsage usage = new DefaultLegalUsage();
        
        InvocationTemplate template = new InvocationTemplate(Collections.EMPTY_SET);
        
        usage.addInvocationTemplate(template);
        
        Set templates = usage.getInvocationTemplates();
        
        assertNotNull("usage templates collection is null", templates);
        assertEquals("usage templates collection has wrong size", 1, templates.size());
        assertTrue("usage templates collection doesn't contain added template", templates.contains(template));
    }

}
