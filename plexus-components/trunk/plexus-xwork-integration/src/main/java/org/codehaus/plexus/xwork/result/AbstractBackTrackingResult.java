package org.codehaus.plexus.xwork.result;

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

import com.opensymphony.webwork.dispatcher.ServletActionRedirectResult;
import com.opensymphony.xwork.ActionInvocation;
import org.codehaus.plexus.xwork.interceptor.ActionInvocationTracker;
import org.codehaus.plexus.xwork.interceptor.SavedActionInvocation;

import java.util.Map;

public class AbstractBackTrackingResult
    extends ServletActionRedirectResult
{
    public static final int PREVIOUS = 1;

    public static final int CURRENT = 2;

    protected boolean setupBackTrackPrevious( ActionInvocation invocation )
    {
        return setupBackTrack( invocation, PREVIOUS );
    }

    protected boolean setupBackTrackCurrent( ActionInvocation invocation )
    {
        return setupBackTrack( invocation, CURRENT );
    }

    protected boolean setupBackTrack( ActionInvocation invocation, int order )
    {
        Map session = invocation.getInvocationContext().getSession();
        ActionInvocationTracker tracker = (ActionInvocationTracker) session.get( ActionInvocationTracker.ROLE );

        if ( tracker != null && tracker.isBackTracked() )
        {
            SavedActionInvocation savedInvocation;

            if ( order == PREVIOUS )
            {
                savedInvocation = tracker.getPrevious();
            }
            else
            {
                savedInvocation = tracker.getCurrent();
            }

            if ( savedInvocation != null )
            {
                setActionName( savedInvocation.getActionName() );
                setMethod( savedInvocation.getMethodName() );
                invocation.getInvocationContext().getParameters().putAll( savedInvocation.getParametersMap() );
                tracker.unsetBackTrack();
            }

            return true;
        }

        return false;
    }
}
