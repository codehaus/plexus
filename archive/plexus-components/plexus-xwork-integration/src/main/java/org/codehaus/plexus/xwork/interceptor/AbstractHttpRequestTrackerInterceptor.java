package org.codehaus.plexus.xwork.interceptor;

/*
 * Copyright 2006-2007 The Codehaus Foundation.
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

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.xwork.PlexusLifecycleListener;

import java.util.Map;

public abstract class AbstractHttpRequestTrackerInterceptor
    extends AbstractLogEnabled
    implements Interceptor
{
    public static final String TRACKER_NAME = ActionInvocationTracker.ROLE + ":name";

    protected abstract String getTrackerName();

    protected synchronized ActionInvocationTracker addActionInvocation( ActionInvocation invocation )
        throws ComponentLookupException
    {
        Map sessionMap = invocation.getInvocationContext().getSession();

        ActionInvocationTracker tracker = (ActionInvocationTracker) sessionMap.get( ActionInvocationTracker.ROLE );

        if ( tracker == null )
        {
            ActionContext actionContext = invocation.getInvocationContext();
            PlexusContainer container =
                (PlexusContainer) actionContext.getApplication().get( PlexusLifecycleListener.KEY );

            //noinspection deprecation
            tracker = (ActionInvocationTracker) container.lookup( ActionInvocationTracker.ROLE, getTrackerName() );
            sessionMap.put( ActionInvocationTracker.ROLE, tracker );
        }

        tracker.addActionInvocation( invocation );

        return tracker;
    }
}
