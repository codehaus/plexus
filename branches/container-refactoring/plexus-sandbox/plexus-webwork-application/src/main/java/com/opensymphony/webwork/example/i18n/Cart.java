package com.opensymphony.webwork.example.i18n;

import java.util.ArrayList;
import java.util.List;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class Cart {
    List items;

    public void addItem(CD cd, int quantity) {
        if (items == null)
            items = new ArrayList();

        for (int i = 0; i < items.size(); i++) {
            CartItem ci = (CartItem) items.get(i);
            if (cd.equals(ci.cd)) {
                ci.addQuantity(quantity);
                return;
            }
        }

        items.add(new CartItem(cd, quantity));
    }

    public void removeItem(CD cd) {
        for (int i = 0; i < items.size(); i++) {
            CartItem ci = (CartItem) items.get(i);
            if (cd.equals(ci.cd)) {
                items.remove(ci);

                if (items.size() == 0)
                    items = null;

                return;
            }
        }
    }

    public List getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            CartItem ci = (CartItem) items.get(i);
            total += ci.getCd().getPrice() * ci.getQuantity();
        }
        return total;
    }

    static public class CartItem {
        int qty;
        CD cd;

        CartItem(CD cd, int qty) {
            this.cd = cd;
            addQuantity(qty);
        }

        public void addQuantity(int qty) {
            this.qty += qty;
        }

        public int getQuantity() {
            return qty;
        }

        public CD getCd() {
            return cd;
        }
    }
}
