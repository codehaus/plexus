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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.cotlet.model.CotletModel;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.StringWriter;

public class DefaultCotletModelWriter implements CotletModelWriter
{
    private VelocityComponent velocityComponent;

    public String writeModel( CotletModel model, String templateName ) throws Exception
    {

        final StringWriter writer = new StringWriter();

        final Template template = velocityComponent.getEngine().getTemplate( templateName );

        VelocityContext context = new VelocityContext();

        context.put( "model", model );

        template.merge( context, writer );

        final String retValue = writer.toString();

        return retValue;

    }
}
