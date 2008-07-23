package org.codehaus.plexus.spe.model;

import java.io.Serializable;

/**
 * @version $Id$
 */
public class ContextValue
    implements Serializable
{
    private byte[] value;

    public byte[] getValue()
    {
        return value;
    }

    public void setValue( byte[] value )
    {
        this.value = value;
    }
}
