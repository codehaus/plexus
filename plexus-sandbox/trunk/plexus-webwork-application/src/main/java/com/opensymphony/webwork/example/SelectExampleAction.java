package com.opensymphony.webwork.example;

import com.opensymphony.xwork.ActionSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * SelectExampleAction
 *
 * @author Jason Carreira
 *         Date: Nov 23, 2003 12:20:02 AM
 */
public class SelectExampleAction extends ActionSupport {
    private Map selectMap;
    private Long selected;

    public String execute() throws Exception {
        selectMap = new HashMap();
        selectMap.put(new Long(1), "Value 1");
        selectMap.put(new Long(2), "Value 2");
        selectMap.put(new Long(3), "Value 3");
        return SUCCESS;
    }

    public Map getSelectMap() {
        return selectMap;
    }

    public void setSelectMap(Map selectMap) {
        this.selectMap = selectMap;
    }

    public Long getSelected() {
        return selected;
    }

    public void setSelected(Long selected) {
        this.selected = selected;
    }
}
