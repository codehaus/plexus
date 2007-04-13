package org.codehaus.plexus.appserver;

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
 * @since 7 mars 07
 * @version $Id$
 */
public abstract class ApplicationServerConstants
{
    /** each app context have parent container with this key */
    public static final String APP_SERVER_CONTEXT_KEY = "plexus.appserver";

    /**  */
    public static final String APP_SERVER_HOME_KEY = "appserver.home";
    
    public static final String APP_SERVER_BASE_KEY = "appserver.base";
}
