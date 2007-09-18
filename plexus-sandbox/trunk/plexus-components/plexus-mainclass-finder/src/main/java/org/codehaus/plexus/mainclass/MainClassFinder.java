package org.codehaus.plexus.mainclass;

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

import java.util.List;

/**
 * Component that will scan a list of paths {@link java.io.File}s for
 * classes containing a <code>public static void main(String[] args)</code> method.
 * The files can represent jar files or directories containing class files.
 */
public interface MainClassFinder
{
    public final static String ROLE = MainClassFinder.class.getName();

    /**
     * Find names of all classes containing a main method.
     *
     * @param classPath a {@link List} of jar files or directories where class files can be found
     * @return a {@link List} of class names (Strings)
     */
    List findMainClasses( List classPath );
}
