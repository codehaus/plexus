package org.codehaus.plexus.hibernate;

/*
 * LICENSE
 */

import net.sf.hibernate.Session;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultHibernateSessionServiceTest 
    extends PlexusTestCase 
{
    public void testBasic() throws Exception 
    {
        HibernateSessionService sessionService;
        Session session;

        sessionService = (HibernateSessionService)lookup(HibernateSessionService.ROLE);
        session = sessionService.getSession();

        session.save(new Parent());

        sessionService.closeSession();
        release(sessionService);
    }
}
