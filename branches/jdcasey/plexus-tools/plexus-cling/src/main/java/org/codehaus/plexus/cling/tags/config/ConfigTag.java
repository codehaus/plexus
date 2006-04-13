/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.tags.config;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.CLIngConstants;
import org.codehaus.plexus.cling.configuration.CLIngConfiguration;
import org.codehaus.plexus.cling.configuration.SupplementaryCLIngConfiguration;

/**
 * @author jdcasey
 */
public class ConfigTag
    extends AbstractMarmaladeTag
{

    private SupplementaryCLIngConfiguration config = new SupplementaryCLIngConfiguration();

    protected void doExecute( MarmaladeExecutionContext context ) throws MarmaladeExecutionException
    {
        processChildren( context );

        CLIngConfiguration appConfig = (CLIngConfiguration) context.getVariable(
            CLIngConstants.CLING_CONFIG_CONTEXT_KEY, getExpressionEvaluator() );

        appConfig.overrideWith( config );
    }

    public SupplementaryCLIngConfiguration getConfiguration()
    {
        return config;
    }

}