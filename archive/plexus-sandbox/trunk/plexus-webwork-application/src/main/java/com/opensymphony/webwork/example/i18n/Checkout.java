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
public class Checkout extends Shop {
    // Checkout
    double totalPrice;

    // Public  ------------------------------------------------------
    public double getTotalPrice() {
        return totalPrice;
    }

    // Action implementation -----------------------------------------
    public String execute() throws Exception {
        Cart cart = getCart();

        // Calculate total
        ComputePrice cp = new ComputePrice();
        //cp.setSession(session);
        cp.setPrice(cart.getTotal());
        totalPrice = cp.getRealPrice();

        return SUCCESS;
    }
}