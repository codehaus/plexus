/*
 * $RCSfile$
 *
 * Copyright 2000 by Informatique-MTF, SA,
 * CH-1762 Givisiez/Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 */
package org.codehaus.plexus.cotlet;

import org.codehaus.plexus.cotlet.model.CotletModel;

import java.io.File;


public interface Cotlet
{
    String ROLE = Cotlet.class.getName();

    CotletModel buildModel( File[] directories );
}
