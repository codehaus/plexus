package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class WorkflowException extends Exception {
	public WorkflowException(String msg) {
        super(msg);
    }

    public WorkflowException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
