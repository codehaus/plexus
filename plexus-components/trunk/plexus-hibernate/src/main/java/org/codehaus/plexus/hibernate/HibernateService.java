package org.codehaus.plexus.hibernate;

import net.sf.hibernate.SessionFactory;

/**
 * Service for Hibernate persistance manager.
 *  
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Nov 7, 2003
 */
public interface HibernateService
{
    String ROLE = HibernateService.class.getName();
    
    SessionFactory getSessionFactory();
}
