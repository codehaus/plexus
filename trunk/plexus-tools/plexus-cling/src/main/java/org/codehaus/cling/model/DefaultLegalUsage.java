/* Created on Sep 14, 2004 */
package org.codehaus.cling.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.cling.cli.InvocationTemplate;

/**
 * @author jdcasey
 */
public class DefaultLegalUsage implements LegalUsage
{
    private List invocationTemplates = new LinkedList();

    public List getInvocationTemplates()
    {
        return Collections.unmodifiableList(invocationTemplates);
    }
    
    public void addInvocationTemplate(InvocationTemplate template) {
        invocationTemplates.add(template);
    }

}
