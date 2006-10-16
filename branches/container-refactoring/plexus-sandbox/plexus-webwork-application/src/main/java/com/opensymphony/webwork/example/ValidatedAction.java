package com.opensymphony.webwork.example;

import com.opensymphony.xwork.ActionSupport;

/**
 * ValidatedAction
 *
 * @author Jason Carreira
 *         Created Sep 12, 2003 9:23:38 PM
 */
public class ValidatedAction extends ActionSupport {
    private ValidatedBean bean = new ValidatedBean();
    private String name;
    private String validationAction = "basicValidation.action";

    public ValidatedBean getBean() {
        return bean;
    }

    public void setBean(ValidatedBean bean) {
        this.bean = bean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValidationAction() {
        return validationAction;
    }

    public void setValidationAction(String validationAction) {
        this.validationAction = validationAction;
    }
}
