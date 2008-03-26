/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.plexus.spring;

import java.util.List;

import org.apache.maven.settings.MavenSettingsBuilder;
import org.codehaus.plexus.commandline.ExecutableResolver;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * @author <a href="mailto:olamy@apache.org">olamy</a>
 * @since 25 mars 2008
 * @version $Id$
 */
public abstract class AbstractConfigurablePlexusBean
  implements Initializable
{
    
    /**
     * @plexus.requirement
     */
    private ExecutableResolver executableResolver; 
    
    /**
     * @plexus.requirement
     */
    private MavenSettingsBuilder mavenSettingsBuilder;    

    public void initialize()
        throws InitializationException
    {
        List path = executableResolver.getDefaultPath();
        
    }
    
    public ExecutableResolver getExecutableResolver()
    {
        return executableResolver;
    }

    public MavenSettingsBuilder getMavenSettingsBuilder()
    {
        return mavenSettingsBuilder;
    }

}
