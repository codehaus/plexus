package org.codehaus.plexus.werkflow.continuum;

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

import java.util.Map;

import org.codehaus.plexus.action.Action;
import org.codehaus.werkflow.spi.Instance;

import junit.framework.Assert;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class RemoveProject
    extends Assert
    implements Action
{
    public void execute( Map context )
        throws Exception
    {
        Instance instance = (Instance) context.get( "instance" );

        instance.put( "action-remove-project", "done" );
    }
}
