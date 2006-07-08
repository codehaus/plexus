package org.codehaus.plexus.component.repository;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentConfigurationDescriptor
{
    private boolean synthetic;

    // packages to search for when looking up beans

    List fields;

    public boolean isSynthetic()
    {
        return synthetic;
    }

    public void setSynthetic( boolean synthetic )
    {
        this.synthetic = synthetic;
    }

    public List getFields()
    {
        if ( fields == null )
        {
            fields = new ArrayList();
        }

        return fields;
    }

    public void setFields( List fields )
    {
        this.fields = fields;
    }
}
