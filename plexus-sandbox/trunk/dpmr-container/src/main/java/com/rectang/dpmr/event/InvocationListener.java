package com.rectang.dpmr.event;

import com.rectang.dpmr.DPMRMethod;

public interface InvocationListener {

  public void invocationStarted(DPMRMethod method);

  public void invocationStopped(DPMRMethod method);
}
