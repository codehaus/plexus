package org.codehaus.plexus.builder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

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
 */
public class AbstractBuilderTest
    extends PlexusTestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();

        // ----------------------------------------------------------------------
        // Clean the output directory
        // ----------------------------------------------------------------------

        FileUtils.deleteDirectory( getWorkingDirectory() );
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected File getWorkingDirectory()
    {
        return getTestFile( "target/test-runtime" );
    }

    /**
     * <b>use default type jar</b>
     * @param groupId
     * @param artifactId
     * @param version
     * @return
     */
    protected Artifact makeArtifact( String groupId, String artifactId, String version )
        throws Exception
    {
        return makeArtifact( groupId, artifactId, version, "jar" );
    }

    protected Artifact makeArtifact( String groupId, String artifactId, String version, String type )
        throws Exception
    {

        return makeArtifact( groupId, artifactId, version, "jar", "jar" );
    }

    protected Artifact makeArtifact( String groupId, String artifactId, String version, String type, String fileType )
        throws Exception
    {
        ArtifactFactory artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );
        Artifact artifact = artifactFactory.createBuildArtifact( groupId, artifactId, version, type );

        artifact.setFile( getTestFile( "src/test/repository/" + StringUtils.replace( groupId, ".", "/" ) + "/"
            + artifactId + "/" + version + "/" + artifactId + "-" + version + "." + fileType ) );

        return artifact;
    }

    protected File getPlexusApplication()
        throws Exception
    {
        Artifact artifact = makeArtifact( "bar", "bar", "1.0", "plexus-application" );
        return artifact.getFile();
    }

    protected File getPlexusService()
        throws Exception
    {
        Artifact artifact = makeArtifact( "org.codehaus.plexus", "plexus-appserver-service-jetty",
                                          "2.0-alpha-8-SNAPSHOT", "plexus-service", "sar" );
        return artifact.getFile();
    }

    protected ArtifactRepository getLocalArtifactRepository()
        throws Exception
    {
        ArtifactRepositoryFactory artifactRepositoryFactory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.ROLE );
        ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE,
                                                                                       "default" );
        return artifactRepositoryFactory.createArtifactRepository( "local", "file://"
            + getTestFile( "src/test/repository" ).getAbsolutePath(), repositoryLayout, null, null );

    }

    protected Set getProjectArtifacts()
        throws Exception
    {
        Set projectArtifacts = new HashSet();

        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-classworlds", "1.2-alpha-7" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-component-api", "1.0-alpha-18" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-container-default", "1.0-alpha-18" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-appserver-host", "2.0-alpha-8-SNAPSHOT" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-utils", "1.4" ) );
        projectArtifacts.add( makeArtifact( "bar", "bar", "1.0", "plexus-application" ) );
        projectArtifacts.add( makeArtifact( "commons-logging", "commons-logging", "1.0.4" ) );
        projectArtifacts.add( makeArtifact( "log4j", "log4j", "1.2.8" ) );
        projectArtifacts.add( makeArtifact( "javax.mail", "mail", "1.4" ) );
        return projectArtifacts;
    }

    protected Set getAdditionalCoreArtifacts()
        throws Exception
    {
        Set additionalCoreArtifacts = new HashSet();

        additionalCoreArtifacts.add( "commons-logging:commons-logging" );
        additionalCoreArtifacts.add( "log4j:log4j" );
        additionalCoreArtifacts.add( "javax.mail:mail" );
        return additionalCoreArtifacts;
    }
}
