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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.xwork.ActionContextStub;
import org.codehaus.plexus.xwork.ActionInvocationStub;
import org.codehaus.plexus.xwork.ActionProxyStub;
import org.codehaus.plexus.xwork.interceptor.ActionInvocationTracker;
import org.codehaus.plexus.xwork.interceptor.SimpleActionInvocationTracker;

import java.util.Map;

public class BackTrackingResultTest
    extends PlexusTestCase
{
    public static final int HISTORY_SIZE = 2;

    public void testBackTrackPrevious()
        throws Exception
    {
        // first http request
        ActionInvocationStub actionInvocation1 = new ActionInvocationStub();
        SimpleBackTrackingResult backtrackingResult = new SimpleBackTrackingResult( actionInvocation1 );

        // second http request
        ActionInvocationStub previousActionInvocation = new ActionInvocationStub();
        ActionProxyStub previousProxyStub = (ActionProxyStub) previousActionInvocation.getProxy();
        previousProxyStub.setActionName( "previous_action" );
        previousProxyStub.setMethod( "previous_method" );

        ActionContextStub previousActionContext = (ActionContextStub) previousActionInvocation.getInvocationContext();
        previousActionContext.getParameters().put( "previous_parameter", "previous_value" );

        // third http request
        ActionInvocationStub currentActionInvocation = new ActionInvocationStub();
        ActionProxyStub currentProxyStub = (ActionProxyStub) currentActionInvocation.getProxy();
        currentProxyStub.setActionName( "current_action" );
        currentProxyStub.setMethod( "current_method" );

        ActionContextStub currentActionContext = (ActionContextStub) currentActionInvocation.getInvocationContext();
        currentActionContext.getParameters().put( "current_parameter", "current_value" );

        SimpleActionInvocationTracker tracker = new SimpleActionInvocationTracker();

        // save the second request and third request to the stack
        tracker.setHistorySize( HISTORY_SIZE );
        tracker.addActionInvocation( previousActionInvocation );
        tracker.addActionInvocation( currentActionInvocation );
        tracker.setBackTrack();
        // add the tracker to the session
        actionInvocation1.getInvocationContext().getSession().put( ActionInvocationTracker.ROLE, tracker );

        // before backtrack
        Map parametersMap = actionInvocation1.getInvocationContext().getParameters();

        assertEquals( ActionProxyStub.ACTION_NAME, backtrackingResult.getActionName() );
        assertEquals( ActionProxyStub.METHOD, backtrackingResult.getMethod() );
        assertEquals( ActionContextStub.VALUE_1, parametersMap.get( ActionContextStub.PARAMETER_1 ) );
        assertEquals( ActionContextStub.VALUE_2, parametersMap.get( ActionContextStub.PARAMETER_2 ) );
        assertEquals( ActionContextStub.VALUE_3, parametersMap.get( ActionContextStub.PARAMETER_3 ) );

        backtrackingResult.setupBackTrackPrevious( actionInvocation1 );

        // after backtrack
        parametersMap = actionInvocation1.getInvocationContext().getParameters();

        assertEquals( "previous_action", backtrackingResult.getActionName() );
        assertEquals( "previous_method", backtrackingResult.getMethod() );
        assertEquals( ActionContextStub.VALUE_1, parametersMap.get( ActionContextStub.PARAMETER_1 ) );
        assertEquals( ActionContextStub.VALUE_2, parametersMap.get( ActionContextStub.PARAMETER_2 ) );
        assertEquals( ActionContextStub.VALUE_3, parametersMap.get( ActionContextStub.PARAMETER_3 ) );
        assertEquals( "previous_value", parametersMap.get( "previous_parameter" ) );

    }

    public void testBackTrackCurrent()
        throws Exception
    {
        // first http request
        ActionInvocationStub actionInvocation1 = new ActionInvocationStub();
        SimpleBackTrackingResult backtrackingResult = new SimpleBackTrackingResult( actionInvocation1 );

        // second http request
        ActionInvocationStub previousActionInvocation = new ActionInvocationStub();
        ActionProxyStub previousProxyStub = (ActionProxyStub) previousActionInvocation.getProxy();
        previousProxyStub.setActionName( "previous_action" );
        previousProxyStub.setMethod( "previous_method" );

        ActionContextStub previousActionContext = (ActionContextStub) previousActionInvocation.getInvocationContext();
        previousActionContext.getParameters().put( "previous_parameter", "previous_value" );

        // third http request
        ActionInvocationStub currentActionInvocation = new ActionInvocationStub();
        ActionProxyStub currentProxyStub = (ActionProxyStub) currentActionInvocation.getProxy();
        currentProxyStub.setActionName( "current_action" );
        currentProxyStub.setMethod( "current_method" );

        ActionContextStub currentActionContext = (ActionContextStub) currentActionInvocation.getInvocationContext();
        currentActionContext.getParameters().put( "current_parameter", "current_value" );

        SimpleActionInvocationTracker tracker = new SimpleActionInvocationTracker();

        // save the second request and third request to the stack
        tracker.setHistorySize( HISTORY_SIZE );
        tracker.addActionInvocation( previousActionInvocation );
        tracker.addActionInvocation( currentActionInvocation );
        tracker.setBackTrack();
        // add the tracker to the session
        actionInvocation1.getInvocationContext().getSession().put( ActionInvocationTracker.ROLE, tracker );

        // before backtrack
        Map parametersMap = actionInvocation1.getInvocationContext().getParameters();

        assertEquals( ActionProxyStub.ACTION_NAME, backtrackingResult.getActionName() );
        assertEquals( ActionProxyStub.METHOD, backtrackingResult.getMethod() );
        assertEquals( ActionContextStub.VALUE_1, parametersMap.get( ActionContextStub.PARAMETER_1 ) );
        assertEquals( ActionContextStub.VALUE_2, parametersMap.get( ActionContextStub.PARAMETER_2 ) );
        assertEquals( ActionContextStub.VALUE_3, parametersMap.get( ActionContextStub.PARAMETER_3 ) );

        backtrackingResult.setupBackTrackCurrent( actionInvocation1 );

        // after backtrack
        assertEquals( "current_action", backtrackingResult.getActionName() );
        assertEquals( "current_method", backtrackingResult.getMethod() );
        assertEquals( ActionContextStub.VALUE_1, parametersMap.get( ActionContextStub.PARAMETER_1 ) );
        assertEquals( ActionContextStub.VALUE_2, parametersMap.get( ActionContextStub.PARAMETER_2 ) );
        assertEquals( ActionContextStub.VALUE_3, parametersMap.get( ActionContextStub.PARAMETER_3 ) );
        assertEquals( "current_value", parametersMap.get( "current_parameter" ) );
    }
}
