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

import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.plexus.summit.AbstractSummitComponent;
import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * <p>Base class from which all <code>Renderer</code>s are
 * derived.</p>
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractRenderer
    extends AbstractSummitComponent
    implements Renderer
{
    public String render( RunData data, String view )
        throws SummitException, Exception
    {
        StringWriter writer = new StringWriter();

        render( data, view, writer );

        return writer.toString();
    }

    public abstract void render( RunData data, String view, Writer writer )
        throws SummitException, Exception;

    public abstract boolean viewExists( String view );
}
