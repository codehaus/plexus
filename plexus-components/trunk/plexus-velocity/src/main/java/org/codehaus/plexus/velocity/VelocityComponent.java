package org.codehaus.plexus.velocity;

import org.apache.velocity.app.VelocityEngine;

public interface VelocityComponent
{
    public final static String ROLE = VelocityComponent.class.getName();

    VelocityEngine getEngine();
}
