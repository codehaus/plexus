/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.example;

import com.opensymphony.xwork.ActionSupport;


/**
 * FormAction
 *
 * @author Jason Carreira
 *         Created Apr 2, 2003 10:22:36 PM
 */
public class FormAction extends ActionSupport {
    //~ Instance fields ////////////////////////////////////////////////////////

    private String foo;
    private String status = "Unprocessed.";

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String processForm() {
        try {
            Thread.sleep(2000 * 3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        status = "Processed Form.";

        return SUCCESS;
    }
}
