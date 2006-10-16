package com.opensymphony.webwork.example;

import com.opensymphony.xwork.ActionSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:meier@meisterbohne.de">Philipp Meier</a>
 *         Date: 15.10.2003
 *         Time: 18:27:34
 */
public class LoanCalc extends ActionSupport {
    private float presentValue = 1000;
    private float interestRate = 0.08f;
    private float instalment = 100;
    protected static final int MAXTERMS = 100;

    private List monthlyDues;

    public void setPresentValue(float presentValue) {
        this.presentValue = presentValue;
    }

    public float getPresentValue() {
        return presentValue;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInstalment(float instalment) {
        this.instalment = instalment;
    }

    public float getInstalment() {
        return instalment;
    }

    public List getMonthlyDues() {
        return monthlyDues;
    }

    protected void doValidation() {
        if (presentValue <= 0) {
            addFieldError("presentValue", "must be > 0");
        }

        if (interestRate <= 0) {
            addFieldError("interestRate", "must be > 0");
        }

        if (instalment <= 0) {
            addFieldError("instalment", "must be > 0");
        }
    }

    protected String doExecute() {
        monthlyDues = new ArrayList();
        int month = 0;
        for (float rest = presentValue; rest > 0.0001f && month <= MAXTERMS; month++) {
            float interest = rest * interestRate / 12;
            float redemption = instalment - interest;
            float newRest = rest - redemption;

            if (newRest < 0) {
                monthlyDues.add(new MonthlyDue(month, rest, interest, rest, rest + interest));
            } else {
                monthlyDues.add(new MonthlyDue(month, rest, interest, redemption, instalment));
            }
            rest = newRest;
        }

        if (month > MAXTERMS) {
            addActionError("Term is longer than " + MAXTERMS + " months!");
            return ERROR;
        }

        return SUCCESS;
    }

    public String execute() throws Exception {
        doValidation();
        if (hasErrors()) {
            return INPUT;
        } else {
            return doExecute();
        }
    }

    public class MonthlyDue {
        MonthlyDue(int month, float presentValue, float interest, float redemption, float instalment) {
            this.month = month;
            this.presentValue = presentValue;
            this.interest = interest;
            this.redemption = redemption;
            this.instalment = instalment;
        }

        public int getMonth() {
            return month;
        }

        public float getPresentValue() {
            return presentValue;
        }

        public float getInterest() {
            return interest;
        }

        public float getRedemption() {
            return redemption;
        }

        public float getInstalment() {
            return instalment;
        }

        private int month;
        private float presentValue;
        private float interest;
        private float redemption;
        private float instalment;
    }

}
