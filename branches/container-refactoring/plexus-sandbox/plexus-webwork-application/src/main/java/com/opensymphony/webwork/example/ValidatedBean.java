package com.opensymphony.webwork.example;

import java.util.Date;

/**
 * ValidatedBean
 *
 * @author Jason Carreira
 *         Created Sep 12, 2003 9:24:18 PM
 */
public class ValidatedBean {
    private String text;
    private Date date = new Date(System.currentTimeMillis());
    private int number;
    private int number2;
    public static final int MAX_TOTAL = 12;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber2() {
        return number2;
    }

    public void setNumber2(int number2) {
        this.number2 = number2;
    }
}
