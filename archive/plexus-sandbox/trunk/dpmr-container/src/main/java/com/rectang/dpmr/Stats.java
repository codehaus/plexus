package com.rectang.dpmr;

import com.rectang.dpmr.event.InvocationListener;

import java.util.*;

public class Stats {

  private Hashtable calledMethods;
  private Hashtable runningMethods;
  private Date startTime;

  private Vector methodListeners;

  Stats() {
    this.calledMethods = new Hashtable();
    this.runningMethods = new Hashtable();
    this.startTime = new Date();

    this.methodListeners = new Vector();
  }

  public void addInvocationListener(InvocationListener listener) {
    methodListeners.add(listener);
  }

  public boolean removeInvocationListener(InvocationListener listener) {
    return methodListeners.remove(listener);
  }

  void methodStarted(DPMRMethod method) {
    calledMethods.put(method, increment(calledMethods, method));
    runningMethods.put(method, increment(runningMethods, method));

    Iterator listeners = methodListeners.iterator();
    while (listeners.hasNext())
      ((InvocationListener) listeners.next()).invocationStarted(method);
  }

  public Date getStartTime() {
    return startTime;
  }

  void methodStopped(DPMRMethod method) {
    runningMethods.put(method, decrement(runningMethods, method));

    int instances = ((Integer) runningMethods.get(method)).intValue();
    if (instances == 0)
      runningMethods.remove(method);

    Iterator listeners = methodListeners.iterator();
    while (listeners.hasNext())
      ((InvocationListener) listeners.next()).invocationStopped(method);
  }

  private Integer increment(Hashtable hash, Object key) {
    Integer exists = (Integer) hash.get(key);
    int count = 0;
    if (exists != null)
      count = exists.intValue();
    count++;
    return new Integer(count);
  }

  private Integer decrement(Hashtable hash, Object key) {
    Integer exists = (Integer) hash.get(key);
    int count = 0;
    if (exists != null)
      count = exists.intValue();
    count--;
    return new Integer(count);
  }

  public Enumeration listInvokedMethods() {
    return calledMethods.keys();
  }

  public Enumeration listRunningMethods() {
    return runningMethods.keys();
  }

  public int getInvokedMethodCount(DPMRMethod method) {
    if (calledMethods.containsKey(method))
      return ((Integer) calledMethods.get(method)).intValue();
    return 0;
  }

  public int getRunningMethodCount(DPMRMethod method) {
    if (runningMethods.containsKey(method))
      return ((Integer) runningMethods.get(method)).intValue();
    return 0;
  }

  public int getInvokedMethodCount() {
    int total = 0;

    Enumeration counts = calledMethods.elements();
    while (counts.hasMoreElements()) {
      total += ((Integer) counts.nextElement()).intValue();
    }
    return total;
  }

  public int getRunningMethodCount() {
    int total = 0;

    Enumeration counts = runningMethods.elements();
    while (counts.hasMoreElements()) {
      total += ((Integer) counts.nextElement()).intValue();
    }
    return total;
  }
}

