package org.codehaus.plexus.hibernate.persister;

import java.io.Serializable;

/**
 * A class that is persistable into the database.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public interface Persistable
{
	public Serializable getId();
}
