package org.codehaus.plexus.component.repository;

import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ComponentConfigurationDescriptor
{
    // packages to search for when looking up beans

    List fields;

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
