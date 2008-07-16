package org.codehaus.plexus.interpolation;

/*
 * Copyright 2001-2008 Codehaus Foundation.
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

import java.util.List;

/**
 * Represents a {@link ValueSource} which provides information back to the caller
 * about what may have gone wrong while resolving the value for an expression.
 *
 * @author jdcasey
 *
 */
public interface FeedbackEnabledValueSource
    extends ValueSource
{

    /**
     * Return the feedback about resolution failures for a particular expression.
     *
     * @return a combination of String and Throwable instances, where strings
     * related to throwables are listed first.
     */
    List getFeedback();

    /**
     * Clear the feedback accumulated by a prior interpolation run.
     */
    void clearFeedback();

}
