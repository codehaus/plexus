/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.example.counter;

import java.io.Serializable;


/**
 * @author $Author: plightbo $
 * @version $Revision: 1.3 $
 */
public class Counter implements Serializable {
    //~ Instance fields ////////////////////////////////////////////////////////

    private int count;

    //~ Methods ////////////////////////////////////////////////////////////////

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
    }
}
