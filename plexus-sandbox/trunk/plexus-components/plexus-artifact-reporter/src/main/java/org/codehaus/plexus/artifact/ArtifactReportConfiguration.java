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

    private static final String SVN = "svn";

    private static final String EMPTY_STRING = "";

    public ArtifactReportConfiguration()
    {
    }

    public ArtifactReportConfiguration( MavenProject project )
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
            if ( !EMPTY_STRING.equals(getScmRevisionId()) )
            {
                scmCheckoutCommand.append( "-r " + getScmRevisionId() + " " );
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
}
