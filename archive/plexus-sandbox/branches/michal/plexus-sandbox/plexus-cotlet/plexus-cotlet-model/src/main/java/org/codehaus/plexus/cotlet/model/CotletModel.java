/*
 * $RCSfile$
 *
 * Copyright 2000 by Informatique-MTF, SA,
 * CH-1762 Givisiez/Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 */
package org.codehaus.plexus.cotlet.model;

import java.util.ArrayList;
import java.util.List;

public class CotletModel
{
    private List implementations = new ArrayList();

    private List specifications = new ArrayList();

    public void addImplementation( ComponentImplementation implementation )
    {
        implementations.add( implementation );
    }

    public void addSpecification( ComponentSpecification specification )
    {
        specifications.add( specification );
    }

    public List getImplementations()
    {
        return implementations;
    }

    public List getSpecifications()
    {
        return specifications;
    }

    public boolean hasSpecifications()
    {
        return specifications.size() > 0;

    }

    public boolean hasImplementations()
    {
        return implementations.size() > 0;
    }


}
