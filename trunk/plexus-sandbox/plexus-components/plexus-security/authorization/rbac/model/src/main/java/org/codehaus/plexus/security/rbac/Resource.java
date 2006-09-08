package org.codehaus.plexus.security.rbac;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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
 * Resource 
 *
 * @author Jesse McConnell <jmcconnell@apache.org>
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * @todo expand on javadoc
 */
public interface Resource
{
    /**
     * Resource refering to all objects.
     */
    public static final Resource GLOBAL = new DefaultGlobalResource();
    
    /**
     * Resource refering to no objects.
     */
    public static final Resource NULL = new DefaultNullResource();

    /**
     * Get The string identifier for an operation.
     *           
     */
    public String getIdentifier();

    /**
     * true if the identifer is a pattern that is to be evaluated, for 
     * example x.* could match x.a or x.b and x.** could match x.foo 
     * 
     * TODO is this even a good idea?
     *           
     */
    public boolean isPattern();

    /**
     * Set The string identifier for an operation.
     *           
     * 
     * @param identifier
     */
    public void setIdentifier( String identifier );

    /**
     * true if the identifer is a pattern that is to be evaluated, for 
     * example x.* could match x.a or x.b and x.** could match x.foo 
     * 
     * TODO is this even a good idea?
     * 
     * @param pattern
     */
    public void setPattern( boolean pattern );

}