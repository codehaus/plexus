package org.codehaus.plexus.xwork.action;

/*
 * Copyright 2005 The Codehaus.
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
import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.ActionSupport;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.util.Map;

/**
 * PlexusActionSupport
 *
 * @author Jesse McConnell <jesse@codehaus.org>
 * @version $Id:$
 */
public abstract class PlexusActionSupport
    extends ActionSupport
    implements Contextualizable, LogEnabled, SessionAware
{
    protected PlexusContainer container;

    protected Map session;

    private Logger logger;

    public void setSession( Map map )
    {
       this.session = map;
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    protected Logger getLogger()
    {
        return logger;
    }

    public void contextualize( Context context )
        throws ContextException
    {
        // this ought to work, but we could also get the context from the ActionContext this way
        // container = (PlexusContainer) ActionContext.getContext().getApplication().get( PlexusLifecycleListener.KEY );
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }


}
