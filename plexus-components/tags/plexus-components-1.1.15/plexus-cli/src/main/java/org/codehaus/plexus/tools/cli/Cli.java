package org.codehaus.plexus.tools.cli;

/*
 * Copyright 2006 The Codehaus Foundation.
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

import org.codehaus.plexus.PlexusContainer;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;

/**
 * @author Jason van Zyl
 */
public interface Cli
{
    Options buildCliOptions( Options options );

    void invokePlexusComponent( CommandLine cli,
                                PlexusContainer container )
        throws Exception;

    // this can be calculated
    String getPomPropertiesPath();
}
