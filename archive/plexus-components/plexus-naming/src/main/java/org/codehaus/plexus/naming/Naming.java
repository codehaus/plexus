package org.codehaus.plexus.naming;

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

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * A naming component for establishing a JNDI tree. This is useful to <code>load-on-start</code> in your plexus
 * applications.
 *
 * @author Brett Porter
 */
public interface Naming
{
    String ROLE = Naming.class.getName();

    Context createInitialContext()
        throws NamingException;
}
