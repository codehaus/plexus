package org.codehaus.plexus.scheduler;
/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.util.StringUtils;
import org.quartz.CronTrigger;

import java.text.ParseException;

public class CronExpressionValidator
{
    public static final String ROLE = CronExpressionValidator.class.getName();

    public boolean validate( String cronExpression )
    {
        try
        {
            String[] cronParams = StringUtils.split( cronExpression );
            if ( cronParams.length < 6 || cronParams.length > 7 )
            {
                return false;
            }

            CronTrigger cronTrigger = new CronTrigger();

            cronTrigger.setCronExpression( cronExpression );

            if ( cronParams[3].equals( "?" ) || cronParams[5].equals( "?" ) )
            {
                return true;
            }

            return false;
        }
        catch ( ParseException e )
        {
            return false;
        }
    }
}
