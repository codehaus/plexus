/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.example.ui;

import com.opensymphony.xwork.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Matt Ho <a href="mailto:matt@indigoegg.com">&lt;matt@indigoegg.com&gt;</a>
 * @version $Id$
 */
public class SimpleAction implements Action {
    //~ Instance fields ////////////////////////////////////////////////////////

    private List list;
    private Map map;
    private String scalar;
    private String[] array;
    private String[] multiValues;
    private List multiList;

    public String[] getMultiValues() {
        return multiValues;
    }

    public void setMultiValues(String[] multiValues) {
        this.multiValues = multiValues;
    }

    public List getMultiList() {
        return multiList;
    }

    public void setMultiList(List multiList) {
        this.multiList = multiList;
    }

    //~ Constructors ///////////////////////////////////////////////////////////

    public SimpleAction() {
        super();
        init();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setArray(String[] array) {
        this.array = array;
    }

    public String[] getArray() {
        return array;
    }

    public void setList(List list) {
        this.list = list;
    }

    public List getList() {
        return list;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void setScalar(String scalar) {
        this.scalar = scalar;
    }

    public String getScalar() {
        return scalar;
    }

    public String execute() throws Exception {
        return Action.SUCCESS;
    }

    private void init() {
        this.scalar = "a scalar value with <&>\"' magic characters";

        this.list = new ArrayList();
        this.list.add(new Node("hello", "world"));
        this.list.add(new Node("foo", "bar"));
        this.list.add(new Node("another", "test"));

        this.array = new String[3];
        this.array[0] = "adam";
        this.array[1] = "betty";
        this.array[2] = "craig";

        this.multiList = new ArrayList();
        this.multiList.add("foo");
        this.multiList.add("bar");
        this.multiList.add("baz");
        this.multiList.add("biz");
        this.multiList.add("pop");

        this.multiValues = new String[2];
        this.multiValues[0] = "bar";
        this.multiValues[1] = "biz";

        this.map = new HashMap();
        this.map.put("ABC", "123");
        this.map.put("XYZ", "789");
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    public class Node {
        String key;
        String value;
        List children = new ArrayList();

        public Node(String key, String value) {
            this.key = key;
            this.value = value;
            children.add(key + " - " + value + " :: child1");
            children.add(key + " - " + value + " :: child2");
            children.add(key + " - " + value + " :: child3");
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public List getChildren() {
            return children;
        }

        public String toString() {
            return key + "/" + value;
        }
    }
}
