package com.opensymphony.webwork.example;

import com.opensymphony.xwork.ActionSupport;

/**
 * Month selector
 *
 * @author Rickard Öberg (<email>)
 * @version $Revision: 1.3 $
 * @see <related>
 */
public class MonthSelector
        extends ActionSupport {
    // Attributes ----------------------------------------------------
    int month = -1;

    // Public --------------------------------------------------------
    public void setMonth(int aMonth) {
        this.month = aMonth;
    }

    public int getMonth() {
        return month;
    }
}
