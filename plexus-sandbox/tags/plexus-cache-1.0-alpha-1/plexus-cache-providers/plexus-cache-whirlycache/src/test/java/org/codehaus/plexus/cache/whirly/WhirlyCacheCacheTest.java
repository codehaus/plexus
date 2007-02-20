/**
 * Copyright @2007 - Accor - All Rights Reserved
 * www.accorhotels.com
 */
package org.codehaus.plexus.cache.whirly;

import org.codehaus.plexus.cache.test.AbstractCacheTestCase;

/**
 * @since 5 févr. 07
 * @version $Id$
 * @author <a href="mailto:Olivier.LAMY@accor.com">Olivier Lamy</a>
 */
public class WhirlyCacheCacheTest
extends AbstractCacheTestCase
{
    public String getProviderHint()
    {
        return "whirlycache";
    }
}
