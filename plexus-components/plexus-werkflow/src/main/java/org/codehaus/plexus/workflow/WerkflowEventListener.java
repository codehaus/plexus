package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

import org.codehaus.werkflow.event.CaseInitiatedEvent;
import org.codehaus.werkflow.event.CaseTerminatedEvent;
import org.codehaus.werkflow.event.ProcessDeployedEvent;
import org.codehaus.werkflow.event.ProcessUndeployedEvent;
import org.codehaus.werkflow.event.TokensConsumedEvent;
import org.codehaus.werkflow.event.TokensProducedEvent;
import org.codehaus.werkflow.event.TokensRolledBackEvent;
import org.codehaus.werkflow.event.TransitionInitiatedEvent;
import org.codehaus.werkflow.event.TransitionTerminatedEvent;
import org.codehaus.werkflow.event.WfmsEventListener;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class WerkflowEventListener
    implements WfmsEventListener {

    public void processDeployed(ProcessDeployedEvent event) {
        System.err.println("Process deployed: " + event.getProcessDefinition().getPackageId() +
                ":" + event.getProcessDefinition().getId() +
                ", type=" + event.getProcessDefinition().getInitiationType());
    }

    public void processUndeployed(ProcessUndeployedEvent event) {
        System.err.println("Process undeployed: " + event.getProcessId());
    }

    public void caseInitiated(CaseInitiatedEvent event) {
        System.err.println("Case initiated: " + event.getProcessId() + ":" + event.getCaseId());
    }

    public void caseTerminated(CaseTerminatedEvent event) {
        System.err.println("Case initiated: " + event.getProcessId() + ":" + event.getCaseId());
    }

    public void tokensProduced(TokensProducedEvent event) {
        String[] placeIds;
        int i;

        System.err.println("Tokens produced: " + event.getProcessId() + ":" + event.getCaseId() + 
                ", transition id: " + event.getTransitionId());

        placeIds = event.getPlaceIds();
        for(i = 0; i < placeIds.length; i++)
            System.err.println("Place #" + i + ": " + placeIds[i]);
    }

    public void tokensConsumed(TokensConsumedEvent event) {
        String[] placeIds;
        int i;

        System.err.println("Tokens produced: " + event.getProcessId() + ":" + event.getCaseId() + 
                ", transition id: " + event.getTransitionId());

        placeIds = event.getPlaceIds();
        for(i = 0; i < placeIds.length; i++)
            System.err.println("Place #" + i + ": " + placeIds[i]);
    }

    public void tokensRolledBack(TokensRolledBackEvent event) {
        String[] placeIds;
        int i;

        System.err.println("Tokens produced: " + event.getProcessId() + ":" + event.getCaseId() + 
                ", transition id: " + event.getTransitionId());

        placeIds = event.getPlaceIds();
        for(i = 0; i < placeIds.length; i++)
            System.err.println("Place #" + i + ": " + placeIds[i]);
    }

    public void transitionInitiated(TransitionInitiatedEvent event) {
        System.err.println("Tokens produced: " + event.getProcessId() + ":" + event.getCaseId() + 
                ", transition id: " + event.getTransitionId());
    }

    public void transitionTerminated(TransitionTerminatedEvent event) {
        System.err.println("Tokens produced: " + event.getProcessId() + ":" + event.getCaseId() + 
                ", transition id: " + event.getTransitionId());
    }
}
