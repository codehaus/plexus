/*
 * WebWork, Web Application Framework
 *
 * Distributable under Apache license.
 * See terms of license at opensource.org
 */
package com.opensymphony.webwork.example.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This code is an adaptation of the I18N example from the JavaWorld article by Govind Seshadri.
 * http://www.javaworld.com/javaworld/jw-03-2000/jw-03-ssj-jsp_p.html
 */
public class ComputePrice extends Shop {
    // Static --------------------------------------------------------
    static Properties exchangeRates;

    static {
        // This never changes, so we do it once only
        exchangeRates = new Properties();

        // We read the values from a file so that it is easy to change
        try {
            InputStream in = ComputePrice.class.getResourceAsStream("exchangerates.properties");
            exchangeRates.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            System.err.println("Could not read list of exchange rates");
        }
    }

    // Attributes ----------------------------------------------------
    double price;

    // Public --------------------------------------------------------
    public void setPrice(double price) {
        this.price = price;
    }

    public double getRealPrice() {
        return computePrice(price);
    }

    /*
       public String computePrice(double price)
       {
          String exchangeRate = (String)exchangeRates.getProperty(getLocale().getCountry());
	
          price *= new Double(exchangeRate).doubleValue();
	
          NumberFormat form = NumberFormat.getCurrencyInstance(getLocale());
          return form.format(price);
       }
    */

    public double computePrice(double price) {
        String exchangeRate = (String) exchangeRates.getProperty(getLocale().getCountry());

        price *= new Double(exchangeRate).doubleValue();

        return price;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

}
