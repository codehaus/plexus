package org.codehaus.plexus.drools;

import java.util.HashMap;

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.TransactionalWorkingMemory;

/**
 * This service is used to retrieve and manage rules
 * definitions, and able to fire them, e.g. using the rules to
 * modify objects and execute functions.
 *
 * @author Bart Selders
 * @version $Id$
 */
public interface DroolsComponent
{
    /** Role of this component */
    static String ROLE = DroolsComponent.class.getName();
    
    /**
     * Retrieve a WorkingMemory
     */		
	WorkingMemory getWorkingMemory(String ruleBaseIdentifier)
         throws Exception;
	
	/**
     * Retrieve a TransactionalWorkingMemory
     */		
	TransactionalWorkingMemory getTransactionalWorkingMemory(String ruleBaseIdentifier)
         throws Exception;
	
	RuleBase getRuleBase(String identifier);
	
	void setRuleBase(String identifier, RuleBase ruleBase);
	
	HashMap getRuleBaseStore();
}
