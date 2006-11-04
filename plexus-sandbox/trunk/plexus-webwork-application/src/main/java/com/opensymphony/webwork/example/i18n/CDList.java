/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.example.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class CDList extends Shop {
    // Attributes ----------------------------------------------------
    static List cds;

    static {
        // This never changes, so we do it once only
        cds = new ArrayList();

        // We read the values from a file so that it is easy to change
        try {
            InputStream resource = CDList.class.getResourceAsStream("cdlist.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(resource));
            String cdInfo;
            while ((cdInfo = in.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(cdInfo, ",");
                CD cd = new CD();
                cd.setAlbum(tokens.nextToken());
                cd.setArtist(tokens.nextToken());
                cd.setCountry(tokens.nextToken());
                cd.setPrice(new Double(tokens.nextToken()).doubleValue());
                cds.add(cd);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.err.println("Could not read list of CD's");
        }
    }

    // Public --------------------------------------------------------
    public List getCDList() {
        return cds;
    }

    public CD getCD(String title) {
        List l = getCDList(); // Call getCDList so it can be replaced with database access if necessary
        for (int i = 0; i < l.size(); i++) {
            CD cd = (CD) l.get(i);
            if (cd.getAlbum().equals(title)) {
                return cd;
            }
        }

        throw new IllegalArgumentException("No such CD");
    }
}
