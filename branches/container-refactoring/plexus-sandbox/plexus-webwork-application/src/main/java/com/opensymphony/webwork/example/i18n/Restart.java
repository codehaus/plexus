/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.example.i18n;

import com.opensymphony.xwork.ActionContext;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class Restart extends Shop {
    // Action implementation -----------------------------------------
    public String execute() throws Exception {
        ActionContext.getContext().getSession().remove("cart");
        return SUCCESS;
    }
}