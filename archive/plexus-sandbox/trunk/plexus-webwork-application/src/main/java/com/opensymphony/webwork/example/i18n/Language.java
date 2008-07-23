/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.example.i18n;

import com.opensymphony.xwork.ActionContext;

import java.util.Locale;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class Language extends Shop {
    // Language
    String lang;

    // Public  ------------------------------------------------------
    public void setLanguage(String lang) {
        this.lang = lang;
    }

    public String getLanguage() {
        return getLocale().getCountry();
    }

    // Action implementation -----------------------------------------
    public String execute() throws Exception {
        Locale locale;
        if (lang == null)
            return ERROR;
        else {
            if (lang.equals("de")) {
                locale = Locale.GERMANY;
            } else if (lang.equals("fr")) {
                locale = Locale.FRANCE;
            } else if (lang.equals("sv")) {
                locale = new Locale("sv", "SE");
            } else {
                locale = Locale.US;
            }
            ActionContext.getContext().getSession().put("locale", locale);

            return SUCCESS;
        }
    }
}
