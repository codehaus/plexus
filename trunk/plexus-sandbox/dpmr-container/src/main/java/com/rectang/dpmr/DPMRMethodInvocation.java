package com.rectang.dpmr;

public class DPMRMethodInvocation {

  private Object ret;
  private boolean running;

  private DPMRMethod method;

  public DPMRMethodInvocation(DPMRMethod method) {
    this.method = method;
    method.getModule().getDPMR().getStats().methodStarted(method);

    running = true;
  }

  public DPMRMethod getMethod() {
    return method;
  }

  protected synchronized void setReturn(Object ret) {
    this.ret = ret;
    this.running = false;
    
    method.getModule().getDPMR().getStats().methodStopped(method);
    notifyAll();
  }

  public synchronized Object getReturn() {
    while (running) {
      try {
        wait();
      } catch (InterruptedException e) {
      }
    }
    return ret;
  }
}
