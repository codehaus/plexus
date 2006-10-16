package org.codehaus.plexus.summit.renderer;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.exception.MethodInvocationException;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * <p>A <code>Renderer</code> that can process Velocity templates.</p>
 *
 * @plexus.component
 *  role-hint="velocity"
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @todo the encoding needs to be configurable.
 * @todo Valves need to have an initialization phase
 */
public class VelocityRenderer
    extends AbstractRenderer
{
    private String encoding = "ISO-8859-1";

    /**
     * @plexus.requirement
     */
    private VelocityComponent velocity;

    public void render( RunData data, String view, Writer writer )
        throws SummitException, Exception
    {
        ViewContext viewContext = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

        viewContext.put( "data", data );

        VelocityContextAdapter vca = new VelocityContextAdapter( viewContext );

        try
        {
            if ( !velocity.getEngine().templateExists( view ) )
            {
                getLogger().error( "Template " + view + " does note exist!" );

                throw new Exception( "Template " + view + " does note exist!" );
            }

            Template template = velocity.getEngine().getTemplate( view );

            template.merge( vca, writer );
        }
        catch ( Throwable e )
        {
            // if the Exception is a MethodInvocationException, the underlying
            // Exception is likely to be more informative, so rewrap that one.
            if ( e instanceof MethodInvocationException )
            {
                e = ( (MethodInvocationException) e ).getWrappedThrowable();
            }
            getLogger().error( "Error rendering template: ", e );
        }
        finally
        {
            try
            {
                if ( writer != null )
                {
                    // don't close to allow us to play
                    // nicely with others.
                    writer.flush();
                }
            }
            catch ( Exception e )
            {
                // do nothing
            }
        }
    }

    /**
     * Check the existence of a Velocity template.
     *
     * @param view Velocity view to check for existance.
     * @return boolean
     */
    public boolean viewExists( String view )
    {
        return velocity.getEngine().templateExists( view );
    }
}
