/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.example.i18n;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class Delete extends Shop {
    // Attributes ----------------------------------------------------
    String album;

    // Public  ------------------------------------------------------
    public void setAlbum(String title) {
        this.album = title;
    }

    // Action implementation -----------------------------------------
    public String execute() throws Exception {
        Cart cart = getCart();
        CDList cdList = new CDList();
        cdList.execute();
        CD cd = cdList.getCD(album);

        cart.removeItem(cd);

        return SUCCESS;
    }
}