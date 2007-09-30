/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.maven.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.cdc.ComponentDescriptorWriter;
import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;
import org.codehaus.plexus.component.repository.cdc.ComponentSetDescriptor;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

/**
 * ???
 *
 * @phase process-classes
 * @goal descriptor
 * @requiresDependencyResolution compile
 * 
 * @version $Id$
 */
public class DescriptorMojo
    extends AbstractMojo
{
    /**
     * ???
     *
     * @parameter expression="${project.build.outputDirectory}
     */
    private File classesDirectory;

    /**
     * ???
     *
     * @parameter expression="${project.compileClasspathElements}
     * @readonly
     */
    private List<String> classpathElements;

    /**
     * ???
     *
     * @parameter
     */
    private boolean containerDescriptor;

    /**
     * ???
     *
     * @parameter expression="${project.build.outputDirectory}"
     */
    private File outputDirectory;

    /**
     * ???
     * 
     * @parameter expression="META-INF/plexus/components.xml"
     * @required
     */
    private String fileName;

    /**
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     * @readonly
     */
    private ComponentDescriptorWriter writer;

    private URL[] getClasspath() {
        List<URL> list = new ArrayList<URL>();

        // Add the projects dependencies
        for (String filename : classpathElements) {
            try {
                list.add(new File(filename).toURI().toURL());
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        getLog().debug("Classpath:");
        for (Object obj : list) {
            getLog().debug("    " + obj);
        }

        return list.toArray(new URL[list.size()]);
    }

    public void execute() throws MojoExecutionException {
        ClassLoader cl = new URLClassLoader(getClasspath(), getClass().getClassLoader());

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(classesDirectory);
        scanner.setIncludes(new String[]{ "**/*.class" });
        scanner.scan();

        ComponentSetDescriptor desc = new ComponentSetDescriptor();
        AnnotationComponentGleaner gleaner = new AnnotationComponentGleaner();

        for (String file : scanner.getIncludedFiles()) {
            String className = file.substring(0, file.lastIndexOf(".class")).replace('\\', '.').replace('/', '.');
            Class<?> type;

            try {
                type = cl.loadClass(className);
            }
            catch (ClassNotFoundException e) {
                throw new MojoExecutionException("Failed to load class: " + className, e);
            }

            ComponentDescriptor component = gleaner.glean(type);

            if (component != null) {
                getLog().debug("Discovered: " + component.getHumanReadableKey());
                
                desc.addComponentDescriptor(component);
            }
        }

        List discovered = desc.getComponents();

        if (discovered == null) {
            getLog().debug("No Plexus components were discovered");
        }
        else {
            getLog().info("Discovered " + discovered.size() + " Plexus component(s)");
            
            try {
                writeDescriptor(desc);
            }
            catch (Exception e) {
                throw new MojoExecutionException("Failed to write descriptor file", e);
            }
        }
    }

    private void writeDescriptor(final ComponentSetDescriptor desc) throws Exception {
        assert desc != null;

        File outputFile = new File(outputDirectory, fileName);
        FileUtils.forceMkdir(outputFile.getParentFile());

        getLog().debug("Writing: " + outputFile);

        BufferedWriter output = new BufferedWriter(new FileWriter(outputFile));

        try {
            writer.writeDescriptorSet(output, desc, containerDescriptor);
        }
        finally {
            IOUtil.close(output);
        }
    }
}
