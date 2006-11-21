package org.codehaus.classworlds.strategy;

/*
 * Copyright 2001-2006 Codehaus Foundation.
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

import org.codehaus.classworlds.ClassRealm;

/**
 * StrategyFactory loads a strategy, either default or from a given hint.
 *
 * @uthor: Andrew Williams
 * @since: Nov 19, 2006
 * @version: $Id$
 */
public class StrategyFactory
{

    public static Strategy getStrategy( ClassRealm realm )
    {
        return getStrategy( realm, null );
    }

    public static Strategy getStrategy( ClassRealm realm,
                                        String hint )
    {
        // Here we shall check hint to load non-default strategies

        Strategy ret = new DefaultStrategy();
        
        return ret;
    }

    // TODO might need to add variants that take a ClassLoader as a parameter?
}
