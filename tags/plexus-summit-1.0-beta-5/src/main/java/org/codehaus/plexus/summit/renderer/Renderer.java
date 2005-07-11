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

import org.codehaus.plexus.summit.exception.SummitException;
import org.codehaus.plexus.summit.rundata.RunData;

/**
 * <p>A <code>Renderer</code> is a
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Renderer
{
    String ROLE = Renderer.class.getName();

    String render( RunData data, String view )
        throws SummitException, Exception;

    void render( RunData data, String view, Writer writer )
        throws SummitException, Exception;

    boolean viewExists( String view );
}
