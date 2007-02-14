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

import com.opensymphony.xwork.ActionInvocation;

import java.util.Stack;

/**
 * @plexus.component role="org.codehaus.plexus.webwork.interceptor.ActionInvocationTracker"
 * role-hint="simple"
 * instantiation-strategy="per-lookup"
 */
public class SimpleActionInvocationTracker
    implements ActionInvocationTracker
{
    /**
     * @plexus.configuration default-value="5"
     */
    private int historySize;

    private boolean backTrack;

    private Stack actionInvocationStack = new Stack();

    public void setHistorySize( int size )
    {
        this.historySize = size;
    }

    public int getHistorySize()
    {
        return this.historySize;
    }

    public int getHistoryCount()
    {
        return actionInvocationStack.size();
    }

    /**
     * returns the previous actioninvocation and dropping the current one
     */
    public SavedActionInvocation getPrevious()
    {
        if ( actionInvocationStack.size() > 1 )
        {
            // drop the current SavedActionInvocation
            actionInvocationStack.pop();
            return (SavedActionInvocation) actionInvocationStack.pop();
        }

        return null;
    }

    /**
     * return the current action invocation
     */
    public SavedActionInvocation getCurrent()
    {
        if ( actionInvocationStack.size() > 0 )
        {
            return (SavedActionInvocation) actionInvocationStack.pop();
        }

        return null;
    }

    /**
     * returns the actioninvocation at the specified index, preserving
     * the actioninvocation list
     */
    public SavedActionInvocation getActionInvocationAt( int index )
    {
        if ( actionInvocationStack.size() >= index )
        {
            return (SavedActionInvocation) actionInvocationStack.get( index );
        }

        return null;
    }

    public void addActionInvocation( ActionInvocation invocation )
    {
        actionInvocationStack.push( new SavedActionInvocation( invocation ) );

        // remove oldest action invocation
        if ( actionInvocationStack.size() > historySize )
        {
            actionInvocationStack.remove( 0 );
        }
    }

    public void setBackTrack()
    {
        backTrack = true;
    }

    public void unsetBackTrack()
    {
        backTrack = false;
    }

    public boolean isBackTracked()
    {
        return backTrack;
    }
}
