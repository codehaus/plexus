/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.example.i18n;

import com.opensymphony.xwork.ActionSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class LanguageList extends ActionSupport {
    // Attributes ----------------------------------------------------
    static Properties languages;

    static {
        // This never changes, so we do it once only
        languages = new Properties();

        // We read the values from a file so that it is easy to change
        try {
            InputStream in = LanguageList.class.getResourceAsStream("languages.properties");
            languages.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.err.println("Could not read list of languages");
            languages.put("en", "English"); // Default so we at least get something
        }
    }

    // Public --------------------------------------------------------
    public Map getLanguages() {
        return languages;
    }
}
