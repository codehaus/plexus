package org.codehaus.plexus.contextualizer;

import java.util.List;
import java.util.Map;

/*
 * Copyright 2007 The Codehaus Foundation.
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
/**
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 22 feb. 07
 * @version $Id$
 */
public interface Contextualizer
{
    /** Plexus Identifier */
    String ROLE = Contextualizer.class.getName();

    
    /**
     * Values to add in the Plexus Context
     * 
     * @return Map 
     */
    public Map getContextValues();

    /**
     * set the values to add in the Plexus Context 
     * 
     * @param contextValues
     */
    public void setContextValues( Map contextValues );

    /**
     * adding all system properties in the Plexus Context
     * 
     * @return boolean
     */
    public boolean isAddAllSystemProperties();

    /**
     * @param addAllSystemProperties
     */
    public void setAddAllSystemProperties( boolean addAllSystemProperties );

    /**
     * defined sysprops to add in the Plexus Context
     * 
     * @return List
     */
    public List getDefinedSystemProperties();

    /**
     * @param definedSystemProperties
     */
    public void setDefinedSystemProperties( List definedSystemProperties );
}
