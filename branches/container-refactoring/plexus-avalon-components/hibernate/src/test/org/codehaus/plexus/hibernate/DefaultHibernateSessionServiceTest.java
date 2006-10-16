package org.codehaus.plexus.hibernate;

/*
 * LICENSE
 */

import org.codehaus.plexus.PlexusTestCase;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.Session;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultHibernateSessionServiceTest extends PlexusTestCase {
    public DefaultHibernateSessionServiceTest(String name) {
        super(name);
    }

    public void testBasic() throws Exception {
        HibernateSessionService sessionService;
        Configuration config;
        Session session;
        
        sessionService = (HibernateSessionService)lookup(HibernateSessionService.ROLE);
        session = sessionService.getSession();

        session.save(new Parent());

        sessionService.closeSession();
        release(sessionService);
    }
}
