package com.opensymphony.webwork.example;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * List of days in month
 *
 * @author Rickard Öberg (<email>)
 * @version $Revision: 1.3 $
 * @see <related>
 */
public class MonthList
        extends MonthSelector {
    // Attributes ----------------------------------------------------
    List weeks;
    int today;
    String firstWeek;
    long firstDay;

    int day = 0;
    int thisMonth;

    // Public --------------------------------------------------------
    public void setDay(Integer aDay) {
        this.day = aDay.intValue();
    }

    public int getDay() {
        return day;
    }

    public List getWeeks() {
        return weeks;
    }

    public int getToday() {
        return today;
    }

    public int getThisMonth() {
        return thisMonth;
    }

    public String getFirstWeek() {
        return firstWeek;
    }

    public long getFirstDay() {
        return firstDay;
    }

    public String execute()
            throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        today = cal.get(Calendar.DATE);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        thisMonth = cal.get(Calendar.MONTH);

        if (month != -1) {
            cal.set(Calendar.MONTH, month);
        }

        int currentMonth = cal.get(Calendar.MONTH);
        cal.setTime(cal.getTime());
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        firstDay = cal.getTime().getTime();
        firstWeek = cal.get(Calendar.WEEK_OF_YEAR) + "";

        weeks = new ArrayList();
        Long[] days = null;
        do {
            days = new Long[7];
            for (int j = 0; j < 7; j++) {
                if (cal.get(Calendar.MONTH) == currentMonth)
                    days[j] = new Long(cal.get(Calendar.DATE));
                else
                    days[j] = new Long(0);

                cal.add(Calendar.DATE, 1);
            }
            weeks.add(days);
        } while (cal.get(Calendar.MONTH) == currentMonth);

        return SUCCESS;
    }
}
