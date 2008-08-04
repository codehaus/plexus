package org.codehaus.plexus.xwork;

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
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.interceptor.PreResultListener;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * @noinspection ProhibitedExceptionDeclared
 */
public class ActionInvocationStub
    implements ActionInvocation
{
    private ActionContext actionContext = new ActionContextStub();

    private ActionProxy actionProxy = new ActionProxyStub();

    public ActionInvocationStub()
    {
        actionContext.setActionInvocation( this );
    }

    public Object getAction()
    {
        return null;
    }

    public boolean isExecuted()
    {
        return false;
    }

    public ActionContext getInvocationContext()
    {
        return actionContext;
    }

    public ActionProxy getProxy()
    {
        return actionProxy;
    }

    public Result getResult()
        throws Exception
    {
        return null;
    }

    public String getResultCode()
    {
        return null;
    }

    public void setResultCode( String code )
    {

    }

    public OgnlValueStack getStack()
    {
        return null;
    }

    public void addPreResultListener( PreResultListener listener )
    {

    }

    public String invoke()
        throws Exception
    {
        return null;
    }

    public String invokeActionOnly()
        throws Exception
    {
        return null;
    }

}
