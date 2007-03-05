package org.codehaus.plexus.maven.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.codehaus.plexus.maven.plugin.testutils.ProjectStub;
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
 * @since 4 mars 07
 * @version $Id$
 */
public abstract class AbstractAppServerMojoTest
    extends AbstractMojoTestCase
{

    protected void setUp()
        throws Exception
    {
        super.setUp();
        File runtimeDirectory = new File( getBasedir(), "target/plexus-runtime" );
        FileUtils.deleteDirectory( runtimeDirectory );
    }

    public Mojo getAppServerMojo( String pomXml, String goal )
        throws Exception
    {
        File testPom = new File( getBasedir(), pomXml );
        Mojo mojo = lookupMojo( goal, testPom );
        //setVariableValueToObject( mojo, "log", new DebugEnabledLog() );
        MavenProjectStub project = new ProjectStub();
        project.setDependencies( new ArrayList() );
        Set projectArtifacts = new HashSet();
        project.setArtifacts( projectArtifacts );

        projectArtifacts.add( makeArtifact( "commons-logging", "commons-logging-api", "1.0.4" ) );
        project.getDependencies().add( makeDependency( "commons-logging", "commons-logging", "1.0.4" ) );

        project.getDependencies().add( makeDependency( "log4j", "log4j", "1.2.8" ) );
        projectArtifacts.add( makeArtifact( "log4j", "log4j", "1.2.8" ) );
        project.getDependencies().add( makeDependency( "javax.mail", "mail", "1.4" ) );
        projectArtifacts.add( makeArtifact( "javax.mail", "mail", "1.4" ) );

        project.getDependencies().add( makeDependency( "org.codehaus.plexus", "plexus-classworlds", "1.2-alpha-7" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-classworlds", "1.2-alpha-7" ) );

        project.getDependencies().add(
                                       makeDependency( "org.codehaus.plexus", "plexus-container-default",
                                                       "1.0-alpha-18" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-container-default", "1.0-alpha-18" ) );

        project.getDependencies().add( makeDependency( "org.codehaus.plexus", "plexus-component-api", "1.0-alpha-18" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-component-api", "1.0-alpha-18" ) );

        project.getDependencies().add(
                                       makeDependency( "org.codehaus.plexus", "plexus-appserver-host",
                                                       "2.0-alpha-8-SNAPSHOT" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-appserver-host", "2.0-alpha-8-SNAPSHOT" ) );

        project.getDependencies().add( makeDependency( "org.codehaus.plexus", "plexus-utils", "1.4" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-utils", "1.4" ) );

        project.getDependencies().add( makeDependency( "foo", "bar", "1.0", "plexus-application" ) );
        projectArtifacts.add( makeArtifact( "bar", "bar", "1.0", "plexus-application" ) );

        project.getDependencies().add(
                                       makeDependency( "org.codehaus.plexus", "plexus-appserver-service-jetty",
                                                       "2.0-alpha-8-SNAPSHOT", "plexus-service" ) );
        projectArtifacts.add( makeArtifact( "org.codehaus.plexus", "plexus-appserver-service-jetty",
                                            "2.0-alpha-8-SNAPSHOT", "plexus-service" ) );

        setVariableValueToObject( mojo, "project", project );
        setVariableValueToObject( mojo, "projectArtifacts", projectArtifacts );
        setVariableValueToObject( mojo, "remoteRepositories", new ArrayList() );

        ArtifactRepositoryFactory artifactRepositoryFactory = (ArtifactRepositoryFactory) lookup( ArtifactRepositoryFactory.ROLE );

        ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) lookup( ArtifactRepositoryLayout.ROLE,
                                                                                       "default" );

        ArtifactRepository localRepository = artifactRepositoryFactory.createArtifactRepository( "local", "file://"
            + getTestFile( "src/test/repository" ).getAbsolutePath(), repositoryLayout, null, null );
        setVariableValueToObject( mojo, "localRepository", localRepository );
        return mojo;

    }

    private Dependency makeDependency( String groupId, String artifactId, String version )
    {
        return makeDependency( groupId, artifactId, version, "jar" );
    }

    private Dependency makeDependency( String groupId, String artifactId, String version, String type )
    {
        Dependency dependency = new Dependency();
        dependency.setGroupId( groupId );
        dependency.setArtifactId( artifactId );
        dependency.setVersion( version );
        dependency.setType( type );

        return dependency;
    }

    private Artifact makeArtifact( String groupId, String artifactId, String version )
        throws Exception
    {
        return makeArtifact( groupId, artifactId, version, "jar" );
    }

    private Artifact makeArtifact( String groupId, String artifactId, String version, String type )
        throws Exception
    {
        ArtifactFactory artifactFactory = (ArtifactFactory) lookup( ArtifactFactory.ROLE );
        Artifact artifact = artifactFactory.createBuildArtifact( groupId, artifactId, version, type );

        artifact.setFile( getTestFile( "src/test/repository/" + StringUtils.replace( groupId, ".", "/" ) + "/"
            + artifactId + "/" + version + "/" + artifactId + "-" + version + ".jar" ) );

        return artifact;
    }

}
