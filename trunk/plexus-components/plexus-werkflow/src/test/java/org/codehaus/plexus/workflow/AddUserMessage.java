package org.codehaus.plexus.workflow;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AddUserMessage {
    private String payload;

    /**
     * @return Returns the payload.
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @param payload The payload to set.
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
