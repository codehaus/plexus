package org.codehaus.plexus.workflow;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.werkflow.MutableAttributes;
import org.codehaus.werkflow.ProcessCase;
import org.codehaus.werkflow.ProcessException;
import org.codehaus.werkflow.SimpleAttributes;
import org.codehaus.werkflow.admin.WfmsAdmin;
import org.codehaus.werkflow.definition.ProcessDefinition;
import org.codehaus.werkflow.engine.WorkflowEngine;
import org.codehaus.werkflow.personality.Personality;
import org.codehaus.werkflow.personality.basic.BasicPersonality;
import org.codehaus.werkflow.service.SimpleWfmsServices;
import org.codehaus.werkflow.service.messaging.simple.SimpleMessagingManager;
import org.codehaus.werkflow.service.persistence.PersistenceManager;
import org.codehaus.werkflow.service.persistence.fleeting.FleetingPersistenceManager;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 *
 * @version $Id$
 */
public class WerkflowWorkflowManager
    extends AbstractLogEnabled
    implements Initializable, WorkflowManager
{
    /** Workflow services. */
    private SimpleWfmsServices services;

    /** Workflow Persistence Manager. */
    private PersistenceManager persistenceManager;

    /** Workflow messaging manager. */
    private SimpleMessagingManager messagingManager;

    /** Workflow descriptor. */
    private String workflowDescriptor;

    /** */
    private WorkflowEngine engine;

    /** */
    private WorkflowActionManager actionManager;

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        Map beans;

        services = new SimpleWfmsServices();

        messagingManager = new SimpleMessagingManager();

        persistenceManager = new FleetingPersistenceManager();

        services.setMessagingManager( messagingManager );

        services.setPersistenceManager( persistenceManager );

        engine = new WorkflowEngine( services );

        beans = new HashMap();
        if(actionManager != null) {
            getLogger().info("Adding 'actionManager' to the process beans.");
            beans.put("actionManager", actionManager);
        }
        else {
            getLogger().info("Not adding 'actionManager' to the process beans.");
        }

        Personality bp = BasicPersonality.getInstance();

        URL url = new File( workflowDescriptor ).toURL();

        ProcessDefinition[] processDefs = bp.load( url, beans );

        WfmsAdmin admin = engine.getAdmin();

        admin.addEventListener(new WerkflowEventListener());

        for ( int i = 0; i < processDefs.length; ++i )
        {
            admin.deployProcess( processDefs[i] );
        }
    }
    
    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    public void acceptActivityMessage( Object message )
        throws WorkflowException
    {
        try {
            messagingManager.acceptMessage( message );
        }
        catch(Exception ex) {
            throw new WorkflowException("Exception while accepting message.", ex);
        }
    }

    public String callProcess(String packageId, String processId, Map attributes) 
        throws WorkflowException {
        MutableAttributes processAttributes;
        ProcessCase processCase;
        Iterator keys;
        Object key;

        processAttributes = new SimpleAttributes();
        keys = attributes.keySet().iterator();
        while(keys.hasNext()) {
            key = keys.next();
            processAttributes.setAttribute(key.toString(), attributes.get(key));
        }

        try {
            processCase = engine.callProcess(packageId, processId, processAttributes);
        }
        catch(ProcessException ex) {
            throw new WorkflowException("Exception while creating new process", ex);
        }

        return processCase.getId();
    }

    public void waitForProcess(String packageId, String processId, String id) 
        throws WorkflowException {
        try {
            Thread.sleep(100);
            Thread.sleep(100);
            Thread.sleep(100);
            Thread.sleep(100);
            Thread.sleep(100);
        }
        catch(InterruptedException ex) {
            // fuck it
        }
    }

    public Map getProcessAttributes(String packageId, String processId, String caseId)
        throws WorkflowException {
        ProcessCase processCase;
        Map process = new HashMap();
        String[] names;
        int i;

        try {
            processCase = engine.getProcessCase(packageId, processId, caseId);
        }
        catch(ProcessException ex) {
            throw new WorkflowException("Exception while creating new process", ex);
        }

        names = processCase.getAttributeNames();
        for(i = 0; i < names.length; i++)
            process.put(names[i], processCase.getAttribute(names[i]));

        return process;
    }
}
