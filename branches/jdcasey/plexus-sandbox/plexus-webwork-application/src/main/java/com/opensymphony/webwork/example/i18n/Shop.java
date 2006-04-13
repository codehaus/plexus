/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.example.i18n;

import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

import java.util.Locale;
import java.util.Map;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class Shop extends ActionSupport {
    // Attributes ---------------------------------------------------
    protected Map application;

    // Public  -------------------------------------------------------
    public Cart getCart() {
        Map session = ActionContext.getContext().getSession();
        Cart cart = (Cart) session.get("cart");
        if (cart == null) {
            cart = new Cart();
            session.put("cart", cart);
        }

        return cart;
    }

    // ActionSupport overrides ---------------------------------------
    public Locale getLocale() {
        Locale l = (Locale) ActionContext.getContext().getSession().get("locale");
        return (l == null) ? Locale.getDefault() : l;
    }

}