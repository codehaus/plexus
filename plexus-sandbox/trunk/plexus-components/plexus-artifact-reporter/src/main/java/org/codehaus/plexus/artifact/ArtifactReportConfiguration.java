/**
 *
 * Copyright 2007
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.codehaus.plexus.artifact;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author John Tolentino
 */
public class ArtifactReportConfiguration
{
    private String groupId;

    private String artifactId;

    private String version;

    private String scmUrl;

    private String scmRevisionId;

    private String downloadUrl;

    private String stagingSiteUrl;

    private boolean docckPassed;

    private File docckResultDetails;

    private boolean licenseCheckPassed;

    private File licenseCheckResultDetails;

    private static final String SVN = "svn";

    private static final String EMPTY_STRING = "";

    public ArtifactReportConfiguration()
    {
    }

    public ArtifactReportConfiguration( MavenProject project, String scmRevisionId, boolean docckPassed,
                                        String docckResultDetails, boolean licenseCheckPassed,
                                        String licenseCheckResultDetails )
    {
        loadValuesFromMavenProject( project );
        setScmRevisionId( scmRevisionId );
        setDocckPassed( docckPassed );
        setDocckResultDetails( docckResultDetails );
        setLicenseCheckPassed( licenseCheckPassed );
        setLicenseCheckResultDetails( licenseCheckResultDetails );
    }

    private void loadValuesFromMavenProject( MavenProject project )
    {
        groupId = project.getGroupId();
        artifactId = project.getArtifactId();
        version = project.getVersion();
        scmUrl = project.getScm().getUrl();
        downloadUrl = project.getDistributionManagement().getDownloadUrl();
        stagingSiteUrl = project.getDistributionManagement().getSite().getUrl();
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId( String groupId )
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId( String artifactId )
    {
        this.artifactId = artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    public String getScmUrl()
    {
        return scmUrl;
    }

    public void setScmUrl( String scmUrl )
    {
        this.scmUrl = scmUrl;
    }


    public String getScmRevisionId()
    {
        return ( null == scmRevisionId ? EMPTY_STRING : scmRevisionId );
    }

    public void setScmRevisionId( String scmRevisionId )
    {
        this.scmRevisionId = scmRevisionId;
    }

    public String getScmType()
    {
        String scmType = "UNKNOWN";

        if ( scmUrl.contains( SVN ) )
        {
            scmType = SVN;
        }

        return scmType;
    }

    public String getScmCheckoutCommand()
    {
        StringBuffer scmCheckoutCommand = new StringBuffer( "" );

        if ( SVN.equals( getScmType() ) )
        {
            scmCheckoutCommand.append( "svn co " );
            if ( !EMPTY_STRING.equals( getScmRevisionId() ) )
            {
                scmCheckoutCommand.append( "-r " );
                scmCheckoutCommand.append( getScmRevisionId() );
                scmCheckoutCommand.append( " " );
            }
            scmCheckoutCommand.append( getScmUrl() );
        }
        return scmCheckoutCommand.toString();
    }

    public String getDownloadUrl()
    {
        return downloadUrl;
    }

    public void setDownloadUrl( String downloadUrl )
    {
        this.downloadUrl = downloadUrl;
    }

    public String getStagingSiteUrl()
    {
        return stagingSiteUrl;
    }

    public void setStagingSiteUrl( String stagingSiteUrl )
    {
        this.stagingSiteUrl = stagingSiteUrl;
    }

    public Boolean isDocckPassed()
    {
        return new Boolean( docckPassed );
    }

    public void setDocckPassed( boolean docckPassed )
    {
        this.docckPassed = docckPassed;
    }

    public File getDocckResultDetails()
    {
        return docckResultDetails;
    }

    public void setDocckResultDetails( String docckResultDetails )
    {
        this.docckResultDetails = new File( docckResultDetails );
    }

    public void setDocckResultDetails( File docckResultDetails )
    {
        this.docckResultDetails = docckResultDetails;
    }

    public String getDocckResultContents()
    {
        return "";
    }

    public Boolean isLicenseCheckPassed()
    {
        return new Boolean( licenseCheckPassed );
    }

    public void setLicenseCheckPassed( boolean licenseCheckPassed )
    {
        this.licenseCheckPassed = licenseCheckPassed;
    }

    public File getLicenseCheckResultDetails()
    {
        return licenseCheckResultDetails;
    }

    public void setLicenseCheckResultDetails( String licenseCheckResultDetails )
    {
        this.licenseCheckResultDetails = new File( licenseCheckResultDetails );
    }

    public void setLicenseCheckResultDetails( File licenseCheckResultDetails )
    {
        this.licenseCheckResultDetails = licenseCheckResultDetails;
    }

    public String getLicenseCheckResultContents()
    {
        String contents;

        FileInputStream resource = null;
        try
        {
            resource = new FileInputStream( licenseCheckResultDetails );
            contents = Utils.streamToString( resource );
        }
        catch ( FileNotFoundException e )
        {
            contents = "Can't find License Header results file: " + licenseCheckResultDetails.getAbsolutePath() +
                e.getMessage();
        }
        catch ( IOException e )
        {
            contents = "Can't read License Header results file: " + licenseCheckResultDetails.getAbsolutePath() +
                e.getMessage();
        }
        return contents;
    }

}
