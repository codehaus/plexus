package org.codehaus.plexus.summit;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
 * <p>A set of constants used by Summit. </p>
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface SummitConstants
{
    /**
     * Configuration key for the ExceptionHandler that Turbine will use.
     */
    public final static String EXCEPTION_HANDLER = "defaultExceptionHandler";

    /**
     * What resolver Turbine will use
     */
    public final static String RESOLVER = "defaultResolver";

    /**
     * Application root.
     */
    public final static String APPLICATION_ROOT = "applicationRoot";

    /**
     * Description of the Field
     */
    public final static String WEBAPP_ROOT = "webappRoot";

    /**
     * Description of the Field
     */
    public final static String WEB_CONTEXT = "webContext";

    /**
     * Description of the Field
     */
    public final static String LOGGING_ROOT = "loggingRoot";

    /**
     * Tag for view context.
     */
    public final static String VIEW_CONTEXT = "viewContext";

    /**
     * Tag for stack trace.
     */
    public final static String STACK_TRACE = "stackTrace";

    /**
     * Tag for result messages.
     */
    public final static String RESULT_MESSAGES = "resultMessages";

    /**
     * Tag for the default application view.
     */
    public final static String DEFAULT_APPLICATION_VIEW = "defaultApplicationView";

    /**
     * Key used to store the summit view in the context. *
     */
    public static final String SUMMIT_VIEW_KEY = "summit:view";

    /**
     * Key used to store the summit view's id in the context. *
     */
    public static final String SUMMIT_VIEW_ID_KEY = "summit:view:id";

    /**
     * Key used to store the summit view's name in the context. *
     */
    public static final String SUMMIT_VIEW_NAME_KEY = "summit:view:name";

    /**
     * The action for a request.
     */
    public static final String ACTION = "action";

    /**
     * The target for a request.
     */
    public static final String TARGET = "target";

    /** Rundata */
    public static final String RUNDATA = "data";
}
