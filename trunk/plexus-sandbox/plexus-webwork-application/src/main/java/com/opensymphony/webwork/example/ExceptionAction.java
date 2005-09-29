package com.opensymphony.webwork.example;

import com.opensymphony.xwork.Action;

/**
 * @author $Author: plightbo $
 * @version $Revision: 1.2 $
 */
public class ExceptionAction implements Action {
    public String execute() throws Exception {
        throw new Exception("This is expected");
    }
}
