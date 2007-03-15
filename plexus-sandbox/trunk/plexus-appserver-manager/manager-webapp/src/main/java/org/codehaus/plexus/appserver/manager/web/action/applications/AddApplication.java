package org.codehaus.plexus.appserver.manager.web.action.applications;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.codehaus.plexus.appserver.ApplicationServerException;
import org.codehaus.plexus.appserver.PlexusApplicationConstants;
import org.codehaus.plexus.appserver.PlexusRuntimeConstants;
import org.codehaus.plexus.appserver.manager.web.action.AbstractManagerAction;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

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
 * 
 * @plexus.component
 *   role="com.opensymphony.xwork.Action"
 *   role-hint="addApplication"
 * 
 */
public class AddApplication
    extends AbstractManagerAction
{

    private File applicationFile;

    private String applicationId;

    public String execute()
        throws Exception
    {

        Reader reader;

        JarFile jarFile = null;
        this.getLogger().info( "deploying file " + applicationFile.getPath() );
        jarFile = new JarFile( applicationFile );

        ZipEntry entry = jarFile.getEntry( PlexusApplicationConstants.METADATA_FILE );

        if ( entry == null )
        {
            this.addActionError( getText( "page.addApplication.file.mising.metadata",
                                          new String[] { PlexusApplicationConstants.METADATA_FILE } ) );
            return INPUT;

        }

        reader = new InputStreamReader( jarFile.getInputStream( entry ) );

        Xpp3Dom dom = null;

        try
        {
            dom = Xpp3DomBuilder.build( reader );
        }
        catch ( XmlPullParserException e )
        {
            this.addActionError( getText( "error.parsing.xml.file", new String[] {
                PlexusApplicationConstants.METADATA_FILE,
                e.getMessage() } ) );
            return INPUT;
        }
        catch ( IOException e )
        {
            this.addActionError( getText( "error.reading.file", new String[] {
                PlexusApplicationConstants.METADATA_FILE,
                e.getMessage() } ) );
            return INPUT;
        }

        String appId = dom.getChild( "name" ).getValue();

        if ( StringUtils.isEmpty( appId ) )
        {
            this.addActionError( getText( "page.addApplication.file.missing.appname" ) );
            return INPUT;
        }

        // TODO check if applicationId already exits 
        // if true ask user for remove and deploy        

        this.setApplicationId( appId );
        File appsDirectory = new File( getApplicationServer().getAppServerBase(),
                                       PlexusRuntimeConstants.APPLICATIONS_DIRECTORY );
        // we copy the file to the apps directory for loaded in next restart
        // the file must be named with *.jar 
        File destinationAppFile = new File( appsDirectory, applicationFile.getName() + ".jar" );
        FileUtils.copyFile( applicationFile, destinationAppFile );
        try
        {

            this.getApplicationServer().deploy( appId, destinationAppFile );
        }
        catch ( ApplicationServerException e )
        {
            this.addActionError( getText( "page.addApplication.add.error", new String[] { appId, e.getMessage() } ) );
            return INPUT;
        }
        this.addActionMessage( getText( "page.addApplication.add.success", new String[] { appId } ) );
        return SUCCESS;
    }

    public File getApplicationFile()
    {
        return applicationFile;
    }

    public void setApplicationFile( File applicationFile )
    {
        this.applicationFile = applicationFile;
    }

    public String getApplicationId()
    {
        return applicationId;
    }

    public void setApplicationId( String applicationId )
    {
        this.applicationId = applicationId;
    }

}
