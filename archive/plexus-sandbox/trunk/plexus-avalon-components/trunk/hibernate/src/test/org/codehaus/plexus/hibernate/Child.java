package org.codehaus.plexus.hibernate;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:mattis@inamo.no">Mathias Bjerke</a>
 * @version $Id$
 */
public class Child {

	private long id;
	private String name;
	private long hufse;

	public Child() {
	}

	public Child(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getHufse() {
		return hufse;
	}

	public void setHufse(long hufse) {
		this.hufse = hufse;
	}
}
