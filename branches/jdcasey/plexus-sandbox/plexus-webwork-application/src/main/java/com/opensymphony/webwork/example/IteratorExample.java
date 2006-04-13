/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.example;

import com.opensymphony.xwork.Action;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author $author$
 * @author Rick Salsa (rsal@mb.sympatico.ca)
 * @version $Revision: 1.2 $
 */
public class IteratorExample implements Action {
    //~ Instance fields ////////////////////////////////////////////////////////

    private Collection daysOfWeeek = new ArrayList(7);

    //~ Constructors ///////////////////////////////////////////////////////////

    public IteratorExample() {
        daysOfWeeek.add("Sunday");
        daysOfWeeek.add("Monday");
        daysOfWeeek.add("Tuesday");
        daysOfWeeek.add("Wednesday");
        daysOfWeeek.add("Thursday");
        daysOfWeeek.add("Friday");
        daysOfWeeek.add("Saturday");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setDay(String day) {
        this.daysOfWeeek.add(day);
    }

    public Collection getDays() {
        return this.daysOfWeeek;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
