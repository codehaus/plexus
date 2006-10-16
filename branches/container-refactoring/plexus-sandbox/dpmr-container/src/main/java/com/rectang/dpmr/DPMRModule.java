package com.rectang.dpmr;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import com.rectang.dpmr.stub.*;

public class DPMRModule {

  private DPMR dpmr;
  private String module;

  public DPMRModule(String dpmr, String module) {
    this(DPMR.getInstance(dpmr), module);
  }

  public DPMRModule(DPMR srv, String module) {
    this.dpmr = srv;
    this.module = module;
  }

  public DPMR getDPMR() {
    return dpmr;
  }

  public String getName() {
    return module;
  }

  public String getTitle() {
    try {
      return (String) getMethod("getTitle", DPMRMethod.NO_DECLARED_PARAMETERS)
          .runWait(DPMRMethod.NO_PARAMETERS);
    } catch (NoSuchMethodException e) {
      return "";
    }
  }

  protected DPMRModuleImpl locateModuleImpl() {
    return dpmr.getModuleImpl(module);
  }

  public DPMRMethod getMethod(String method, Class[] params) {
    return new DPMRMethod(this, method, params);
  }

  public Iterator getMethods() {
    /* TODO - this should be cached!!! (you know, until a reload) */
    Vector retList = new Vector();
    DPMRModuleImpl nextMod = dpmr.getModuleImpl(module);

    Method[] method = nextMod.getClass().getDeclaredMethods();
    for (int c = 0; c < method.length; c++) {
      if (!DPMR.isVisibleMethod(method[c]))
        continue;

      retList.add(new DPMRMethod(this, method[c].getName(),
          method[c].getParameterTypes()));
    }

    return retList.iterator();
  }
}
