/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.example.jasperreports;

import com.opensymphony.xwork.ActionSupport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * A list of orders each with a list of line items
 * <p/>
 * Ported to WebWork2:
 *
 * @author Peter Kelley (peterk@moveit.com.au)
 * @author &lt;a href="hermanns@aixcept.de"&gt;Rainer Hermanns&lt;/a&gt;
 * @version $Id$
 */
public class OrderListAction extends ActionSupport {
    //~ Static fields/initializers /////////////////////////////////////////////

    // Attributes ----------------------------------------------------
    static List orders;

    static {
        // This never changes, so we do it once only
        orders = new ArrayList();
        class Order {
            String customerName;
            String address1;
            String address2;
            String city;
            String state;
            String postcode;
            List lineItems;

            public String getCustomerName() {
                return customerName;
            }

            public void setCustomerName(String newCustomerName) {
                customerName = newCustomerName;
            }

            public String getAddress1() {
                return address1;
            }

            public void setAddress1(String newAddress1) {
                address1 = newAddress1;
            }

            public String getAddress2() {
                return address2;
            }

            public void setAddress2(String newAddress2) {
                address2 = newAddress2;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String newCity) {
                city = newCity;
            }

            public String getState() {
                return state;
            }

            public void setState(String newState) {
                state = newState;
            }

            public String getPostcode() {
                return postcode;
            }

            public void setPostcode(String newPostcode) {
                postcode = newPostcode;
            }

            public List getLineItems() {
                return lineItems;
            }

            public void setLineItems(List newLineItems) {
                lineItems = newLineItems;
            }
        }

        class LineItem {
            String productName;
            Integer quantity;
            Float unitCost;

            public String getProductName() {
                return productName;
            }

            public void setProductName(String newProductName) {
                productName = newProductName;
            }

            public Integer getQuantity() {
                return quantity;
            }

            public void setQuantity(Integer newQuantity) {
                quantity = newQuantity;
            }

            public Float getUnitCost() {
                return unitCost;
            }

            public void setUnitCost(Float newUnitCost) {
                unitCost = newUnitCost;
            }
        }

        // We read the values from a file so that it is easy to change
        try {
            InputStream resource = OrderListAction.class.getResourceAsStream("orders.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(resource));
            String thisLine;
            List lineItems = null;
            Order thisOrder = null;

            while ((thisLine = in.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(thisLine, ",");

                if ("order".equals(tokens.nextToken())) {
                    if (thisOrder != null) {
                        thisOrder.setLineItems(lineItems);
                        orders.add(thisOrder);
                    }

                    thisOrder = new Order();
                    lineItems = new ArrayList();
                    thisOrder.setCustomerName(tokens.nextToken());
                    thisOrder.setAddress1(tokens.nextToken());
                    thisOrder.setAddress2(tokens.nextToken());
                    thisOrder.setCity(tokens.nextToken());
                    thisOrder.setState(tokens.nextToken());
                    thisOrder.setPostcode(tokens.nextToken());
                } else {
                    //Line Item
                    LineItem lineItem = new LineItem();
                    lineItem.setProductName(tokens.nextToken());
                    lineItem.setQuantity(new Integer(tokens.nextToken()));
                    lineItem.setUnitCost(new Float(tokens.nextToken()));
                    lineItems.add(lineItem);
                }
            }

            thisOrder.setLineItems(lineItems);
            orders.add(thisOrder);
            in.close();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.err.println("Could not read list of orders");
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object[] getOrderArray() {
        return orders.toArray();
    }

    // Public --------------------------------------------------------
    public List getOrders() {
        return orders;
    }

    public String getTitle() {
        return "Dynamic Order Report Title";
    }

    public String getNullpx() {
        return "here";
    }
}
