package com.opensymphony.webwork.example;

import com.opensymphony.xwork.ActionSupport;

import java.util.Date;

public class JavascriptValidationAction extends ActionSupport {

    String requiredString;
    int intRange;
    String email;
    String url;
    Date date;
    ValidatedBean bean;

    public String execute() throws Exception {
        return SUCCESS;
    }

    public String doDefault() {
        return INPUT;
    }


    public String getRequiredString() {
        return requiredString;
    }

    public void setRequiredString(String requiredString) {
        this.requiredString = requiredString;
    }

    public int getIntRange() {
        return intRange;
    }

    public void setIntRange(int intRange) {
        this.intRange = intRange;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ValidatedBean getBean() {
        return bean;
    }

    public void setBean(ValidatedBean bean) {
        this.bean = bean;
    }
}
