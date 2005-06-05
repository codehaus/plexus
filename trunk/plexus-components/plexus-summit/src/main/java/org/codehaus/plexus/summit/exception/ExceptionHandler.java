package org.codehaus.plexus.summit.exception;

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

import org.codehaus.plexus.summit.rundata.RunData;

/**
 * Interface that defines an exception handler for the Summit servlet. If a
 * throwable is thrown by the pipeline, the servlet will pass the Throwable
 * and RunData to the handleException method of the configured ExceptionHandler
 * to deal with.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @version $Id$
 */
public interface ExceptionHandler
{
    public static final String ROLE = ExceptionHandler.class.getName();

    /**
     * This method will be called to handle a throwable thrown by the pipeline
     *
     * @param data      RunData for the request where the error occured
     * @param throwable The throwable that was thrown
     * @throw Exception if an error occurs handling the throwable. Turbine will
     * then attempt to provide some minimal handling.
     */
    public void handleException( RunData data, Throwable throwable )
        throws Exception;
}
