package org.codehaus.plexus.maven.plugin.webapp;

/*
 * Licensed to The Codehaus ( www.codehaus.org ) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Codehaus licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.maven.plugin.AbstractAppServerMojo;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

/**
 * The base for the appserver web application -> plexus-application wrapper
 *
 * @author Andrew Williams
 * @version $Id$
 * @since 1.0
 */
public abstract class AbstractWebApplicationRuntimePopulatorMojo
    extends AbstractAppServerMojo
{
    /**
     * @parameter expression="${webappPort}"
     */
    protected int webappPort = 8080;

    /**
     * @parameter expression="${webappMappings}"
     */
    protected Properties webappMappings;
    
    /**
     * Will be interpolated Map containing :
     * <ul>
     *   <li>webappPort = webappPort</li>
     *   <li>warFilePath = ${plexus.home}/lib/ + war.getName</li>
     *   <li>contextPath= webAppMappings groupId:artifactId -> value webApp context</li>
     *   <li>all applicationProperties</li>
     * </ul>
     * @parameter expression="${applicationFile}"
     */
    protected File applicationFile;
    
    /**
     * Application properties.
     * @parameter expression="${applicationProperties}"
     */
    protected Properties applicationProperties;

    
    public File wrapWebApplication( File war, String context, int port )
        throws MojoExecutionException
    {
        String warName = war.getName().substring( 0, war.getName().lastIndexOf( '.' ) );

        BufferedWriter out = null;
        //TODO handle it properly - not just wrapping in an application bundle?
        try
        {
            File appDir = new File( target, warName + "-plexus-application" );

            File libDir = new File( appDir, "lib" );
            libDir.mkdirs();
            FileUtils.copyFileToDirectory( war, libDir );

            File logDir = new File( appDir, "logs" );
            logDir.mkdir();

            File confDir = new File( appDir, "conf" );
            confDir.mkdir();
            File plexusConf = new File( confDir, "plexus.xml" );
            out = new BufferedWriter( new FileWriter( plexusConf ) );
            out.write( "<plexus>\n</plexus>\n" );
            out.close();
            if(this.applicationFile == null)
            {
                generateApplicationFile( confDir, war, context, port );
            }
            else
            {
                generateApplicationFileWithInterpolation( confDir, war, context );
            }

            File metaDir = new File( appDir, "META-INF/plexus" );
            metaDir.mkdirs();
            File appMeta = new File( metaDir, "application.xml" );
            out = new BufferedWriter( new FileWriter( appMeta ) );
            out.write( "<plexus-appserver><name>" + warName + "</name></plexus-appserver>\n" );
            out.close();

            out = null;

            try
            {
                JarArchiver jarArchiver = new JarArchiver();
                File applicationJar = new File( target, warName + "-plexus-application" + ".jar" );
                jarArchiver.addDirectory( appDir );
                jarArchiver.setDestFile( applicationJar );
                jarArchiver.createArchive();

                return applicationJar;
            }
            catch ( ArchiverException e )
            {
                throw new MojoExecutionException( "Error while packaging the web application into the runtime.", e );
            }
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Error while copying the appserver into the runtime.", e );
        }
        finally
        {
            IOUtil.close( out );
        }
    }
    
    private void generateApplicationFile(File confDir, File war, String context, int port)
      throws IOException
    {
        BufferedWriter out = null;
        try
        {
        
            File appConf = new File( confDir, "application.xml" );
            out = new BufferedWriter( new FileWriter( appConf ) );
            out.write( "<application>\n" +
                "  <services>\n" +
                "    <service>\n" +
                "      <id>jetty</id>\n" +
                "      <configuration>\n" +
                "        <webapps>\n" +
                "          <webapp>\n" +
                "            <file>${plexus.home}/lib/" + war.getName() + "</file>\n" +
                "            <context>" + context + "</context>\n" +
                "            <extraction-path>${plexus.home}/webapp</extraction-path>\n" +
                "            <standardWebappClassloader>true</standardWebappClassloader>\n" +
                "            <listeners>\n" +
                "              <http-listener>\n" +
                "                <port>" + port + "</port>\n" +
                "              </http-listener>\n" +
                "            </listeners>\n" +
                "          </webapp>\n" +
                "        </webapps>\n" +
                "      </configuration>\n" +
                "    </service>\n" +
                "  </services>\n" +
                "</application>\n" );
            out.close();
        } finally
        {
            IOUtil.close( out );
        }   
    }
    
    private void generateApplicationFileWithInterpolation(File confDir, File war, String context)
        throws IOException
    {
        
        BufferedWriter out = null;
        
        try
        {        
            Map interpolationDatas = new HashMap();
            interpolationDatas.put( "webappPort", Integer.toString( webappPort ) );
            interpolationDatas.put( "warFilePath", "${plexus.home}/lib/" + war.getName() );
            interpolationDatas.put( "contextPath", context );
            getLog().info( "Use applicationProperties " +  applicationProperties == null ? "empty" : applicationProperties.toString() );
            interpolationDatas.putAll( this.applicationProperties == null ? Collections.EMPTY_MAP : applicationProperties );
            InterpolationFilterReader interpolationFilterReader = 
                new InterpolationFilterReader( new FileReader( this.applicationFile), interpolationDatas );
            File appConf = new File( confDir, "application.xml" );
            out = new BufferedWriter( new FileWriter( appConf ) );
            out.write( IOUtil.toString( interpolationFilterReader ) );
            out.close();
        } finally
        {
            IOUtil.close( out );
        }
        
    }
}
