/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.cling.cli.InvocationTemplate;

/**
 * @author jdcasey
 */
public class DefaultLegalUsage implements LegalUsage
{
    private Set invocationTemplates = new HashSet();

    public Set getInvocationTemplates()
    {
        return Collections.unmodifiableSet(invocationTemplates);
    }
    
    public void addInvocationTemplate(InvocationTemplate template) {
        invocationTemplates.add(template);
    }

}
