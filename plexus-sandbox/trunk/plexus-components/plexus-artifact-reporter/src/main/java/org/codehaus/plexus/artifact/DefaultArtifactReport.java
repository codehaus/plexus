/**
 *
 * Copyright 2006
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.codehaus.plexus.artifact;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author John Tolentino
 */
public class DefaultArtifactReport
    extends AbstractLogEnabled
    implements ArtifactReport, Startable
{
    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void start()
    {
        getLogger().info( "Starting DefaultArtifactReport component." );
    }

    public void stop()
    {
        getLogger().info( "Stopping DefaultArtifactReport component." );
    }

    // ----------------------------------------------------------------------
    // ArtifactReport Implementation
    // ----------------------------------------------------------------------

}
