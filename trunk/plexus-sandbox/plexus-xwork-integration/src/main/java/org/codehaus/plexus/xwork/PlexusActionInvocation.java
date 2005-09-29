package org.codehaus.plexus.xwork;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.Result;
import com.opensymphony.xwork.interceptor.PreResultListener;
import com.opensymphony.xwork.util.OgnlValueStack;

import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusActionInvocation
//    implements ActionInvocation
{
    private ActionProxy actionProxy;

    private Map extraContext;

    private boolean pushAction;

    public PlexusActionInvocation( ActionProxy actionProxy, Map extraContext, boolean pushAction )
    {
        this.actionProxy = actionProxy;

        this.extraContext = extraContext;

        this.pushAction = pushAction;
    }

    // ----------------------------------------------------------------------
    // ActionInvocation Implementation
    // ----------------------------------------------------------------------
/*
    public Action getAction()
    {
    }

    public boolean isExecuted()
    {
    }

    public ActionContext getInvocationContext()
    {
    }

    public ActionProxy getProxy()
    {
    }

    public Result getResult()
        throws Exception
    {
    }

    public String getResultCode()
    {
    }

    public OgnlValueStack getStack()
    {
    }

    public void addPreResultListener( PreResultListener listener )
    {
    }

    public String invoke()
        throws Exception
    {
    }
*/
}
