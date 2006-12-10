package org.codehaus.plexus.ibatis;

import com.ibatis.sqlmap.client.SqlMapClient;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface PlexusIbatisHelper
{
    String ROLE = PlexusIbatisHelper.class.getName();

    SqlMapClient getSqlMapClient();
}
