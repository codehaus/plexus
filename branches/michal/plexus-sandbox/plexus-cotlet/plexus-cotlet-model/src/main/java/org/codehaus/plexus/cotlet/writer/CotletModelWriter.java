/*
 * $RCSfile$
 *
 * Copyright 2000 by Informatique-MTF, SA,
 * CH-1762 Givisiez/Fribourg, Switzerland
 * All rights reserved.
 *
 *========================================================================
 */
package org.codehaus.plexus.cotlet.writer;

import org.codehaus.plexus.cotlet.model.CotletModel;


public interface CotletModelWriter
{
    String ROLE = CotletModelWriter.class.getName();

    String writeModel( CotletModel model, String templateName ) throws Exception;
}
