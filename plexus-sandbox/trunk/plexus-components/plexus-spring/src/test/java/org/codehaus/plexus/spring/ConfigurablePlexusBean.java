/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.codehaus.plexus.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.project.MavenProjectBuilder;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Configurable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * A plexus component implements Configurable
 *
 * @author <a href="mailto:olamy@apache.org">Olivier Lamy</a>
 */
public class ConfigurablePlexusBean
    extends AbstractConfigurablePlexusBean
    implements Configurable, Contextualizable
{
    private List taskEntryEvaluators;
    
    private List wines;
    
    private Wine wine;

    private PlexusContainer container;
   
    private String toMailbox;
    
    private String fromMailbox;
    
    /**
     * @plexus.requirement
     */
    private MavenProjectBuilder projectBuilder;

    /**
     * @plexus.requirement
     */
    private ArtifactRepositoryFactory artifactRepositoryFactory;

    /**
     * @plexus.requirement
     */
    private ArtifactRepositoryLayout repositoryLayout;


    
    /**
     * @plexus.configuration default-value="${plexus.home}/local-repository"
     */
    private String localRepository;

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );

    }

    public void configure( PlexusConfiguration plexusConfiguration )
        throws PlexusConfigurationException
    {
        PlexusConfiguration child = plexusConfiguration.getChild( "task-entry-evaluators" );
        PlexusConfiguration[] entryEvaluators = child.getChildren( "task-entry-evaluator" );
        taskEntryEvaluators = new ArrayList();
        for ( int i = 0, size = entryEvaluators.length; i < size; i++ )
        {
            taskEntryEvaluators.add( entryEvaluators[i].getValue() );
        }

        child = plexusConfiguration.getChild( "wines" );
        String roleHint = child.getChild( 0 ).getValue();
        try
        {
            wine = (Wine) container.lookup( Wine.ROLE, roleHint );
        }
        catch ( ComponentLookupException e )
        {
            throw new PlexusConfigurationException( e.getMessage(), e );
        }
    }

    public List getTaskEntryEvaluators()
    {
        return taskEntryEvaluators == null ? Collections.EMPTY_LIST : taskEntryEvaluators;
    }

    public PlexusContainer getContainer()
    {
        return container;
    }

    public Wine getWine()
    {
        return wine;
    }

    public String getFromMailbox()
    {
        return fromMailbox;
    }

    public String getLocalRepository()
    {
        return localRepository;
    }

    public String getToMailbox()
    {
        return toMailbox;
    }

}
