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

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Abstract implementation of {@link MainClassFinder} handling all
 * the directory and Jar scanning. Actual implementations are called with the
 * {@link AbstractMainClassFinder#containsMainClass(java.io.InputStream)} method.
 */
public abstract class AbstractMainClassFinder
    implements MainClassFinder, LogEnabled
{
    private static final int DOTCLASSLENGTH = ".class".length();
    private Logger logger;

    public List findMainClasses( List classPath )
    {
        List classes = new ArrayList();
        for ( int i = 0; i < classPath.size(); i++ )
        {
            File file = (File) classPath.get( i );
            if ( file.isFile() )
            {
                if(getLogger().isDebugEnabled()) {
                    getLogger().debug( "Checking for main-classes in jar: " + file);
                }
                parseJar( file, classes );

            }
            else if ( file.isDirectory() )
            {
                if(getLogger().isDebugEnabled()) {
                    getLogger().debug( "Checking for main-classes in directory: " + file);
                }
                parseDirectory( file, file, classes );
            }
        }
        return classes;
    }

    /**
     * Looks for main classes in a directory.
     *
     * @param directory
     * @param rootDiretory
     * @param classes
     */
    private void parseDirectory( File directory, File rootDiretory, List classes )
    {
        File[] files = directory.listFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            File file = files[i];
            if ( file.isFile() && file.getName().endsWith( ".class" ) )
            {
                try
                {
                    FileInputStream inputStream = new FileInputStream( file );

                    if ( containsMainClass( inputStream ) )
                    {
                        String classname = getClassname( file, rootDiretory );

                        if(getLogger().isDebugEnabled()) {
                            getLogger().debug( "Found main method in class " + classname);
                        }
                        classes.add( new MainClassImpl(classname, rootDiretory));
                    }
                }
                catch ( FileNotFoundException e )
                {
                    String message = "File does not exist: " + file.toString();
                    getLogger().error( message, e);
                    throw new RuntimeException( message, e );
                }
            }
            else if ( file.isFile() &&
                ( file.getName().toLowerCase().endsWith( ".jar" ) || file.getName().toLowerCase().endsWith( ".zip" ) ) )
            {
                parseJar( file, classes );
            }
            else if ( file.isDirectory() )
            {
                parseDirectory( file, rootDiretory, classes );

            }
        }

    }

    /**
     * Returns the class name of a class in a classfile <code>file</code> inside a directory <code>rootDirectory</code>.
     *
     * @param file
     * @param rootDirectory
     * @return
     */
    private String getClassname( File file, File rootDirectory )
    {
        String path = file.getAbsolutePath();
        String rootPath = rootDirectory.getAbsolutePath();
        return path.substring( rootPath.length() + 1, path.length() - DOTCLASSLENGTH ).replace( '/', '.' );
    }

    /**
     * Implemented by subclasses.
     *
     * @param inputStream
     * @return
     */
    protected abstract boolean containsMainClass( InputStream inputStream );

    /**
     * Locate main classes within the given Jar file.
     *
     * @param file
     * @param classes
     */
    private void parseJar( File file, List classes )
    {
        try
        {
            JarFile jar = new JarFile( file );
            Enumeration entries = jar.entries();
            while ( entries.hasMoreElements() )
            {
                JarEntry entry = (JarEntry) entries.nextElement();
                if ( !entry.isDirectory() && entry.getName().endsWith( ".class" ) )
                {
                    if ( containsMainClass( jar.getInputStream( entry ) ) )
                    {
                        String className = entry.getName().substring(0, entry.getName().length() - DOTCLASSLENGTH).replace(
                                '/', '.');
                        classes.add(new MainClassImpl(className, file));
                    }
                }
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    class MainClassImpl implements MainClass {
        private String className;
        private File classPathEntry;

        MainClassImpl(String className, File classPathEntry) {
            this.className = className;
            this.classPathEntry = classPathEntry;
        }

        public String getClassName() {
            return className;
        }

        public File getClassLocation() {
            return classPathEntry;
        }

        public String toString() {
            return getClassName() +" in " + getClassLocation();
        }
    }
    public void enableLogging(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }
}
