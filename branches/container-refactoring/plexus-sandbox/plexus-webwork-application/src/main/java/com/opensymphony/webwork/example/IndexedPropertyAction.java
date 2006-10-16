package com.opensymphony.webwork.example;

import com.opensymphony.xwork.ActionSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * IndexedPropertyAction
 *
 * @author Jason Carreira
 *         Date: Nov 23, 2003 2:36:58 PM
 */
public class IndexedPropertyAction extends ActionSupport {
    private Map emails;
    private List urls;

    public IndexedPropertyAction() {
        // set these now, and they will be overridden by the parameter interceptor if the values are set in
        emails = new HashMap();
        emails.put("Bob", "bob@example.com");
        emails.put("Fred", "fred@example.com");
        emails.put("Tom", "tom@example.com");
        urls = new ArrayList();
        urls.add("http://www.opensymphony.com");
        urls.add("http://wiki.opensymphony.com");
        urls.add("http://jira.opensymphony.com");
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

    public Map getEmails() {
        return emails;
    }

    public void setEmails(Map emails) {
        this.emails = emails;
    }

    public List getUrls() {
        return urls;
    }

    public void setUrls(List urls) {
        this.urls = urls;
    }
}
