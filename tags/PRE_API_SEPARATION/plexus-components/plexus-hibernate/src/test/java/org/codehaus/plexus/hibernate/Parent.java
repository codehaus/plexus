package org.codehaus.plexus.hibernate;

/*
 * LICENSE
 */

/**
 * @author <a href="mailto:mattis@inamo.no">Mathias Bjerke</a>
 * @version $Id$
 */
public class Parent {

	private long id;
    private Child[] children;

    public Parent() {
	}

    public long getId() {
		return id;
	}

    public void setId(long id) {
		this.id = id;
	}

    public Child[] getChildren() {
		return children;
	}

	public void setChildren(Child[] children) {
		this.children = children;
	}
}
