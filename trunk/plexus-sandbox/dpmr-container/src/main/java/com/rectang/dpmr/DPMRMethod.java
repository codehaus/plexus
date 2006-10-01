package com.rectang.dpmr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.rectang.dpmr.stub.*;

public class DPMRMethod {

  public static Class[] NO_DECLARED_PARAMETERS = new Class[0];
  public static Object[] NO_PARAMETERS = new Object[0];

  private DPMRModule module;
  private String method;
  private Class[] params;

  public DPMRMethod(String dpmr, String module, String method, Class[] params) {
    this(DPMR.getInstance(dpmr).getModule(module), method, params);
  }

  public DPMRMethod(DPMRModule module, String method, Class[] params) {
    this.module = module;
    this.method = method;
    this.params = params;
  }

  public DPMRModule getModule() {
    return module;
  }

  public String getName() {
    return method;
  }

  public Class[] getParameterTypes() {
    return params;
  }

  private Method locateMethodImpl() throws NoSuchMethodException {
    DPMRModuleImpl moduleImpl = module.locateModuleImpl();
    return DPMR.getModuleMethodImpl(moduleImpl, method, params);
  }

  public Object runWait(Object[] params) throws NoSuchMethodException {
    /* FIXME - do not block, but fork and wait??? */
    DPMRMethodInvocation inv = run(params);
    return inv.getReturn();
  }

  public DPMRMethodInvocation run(Object[] params)
      throws NoSuchMethodException {
    Method method = locateMethodImpl();
    DPMRMethodInvocation inv = new DPMRMethodInvocation(this);
    (new InvThread(inv, method, params)).start();
    return inv;
  }

  private class InvThread extends Thread {
    DPMRMethodInvocation inv;
    Object[] params;
    Method method;

    public InvThread(DPMRMethodInvocation inv, Method method, Object[] params) {
      this.inv = inv;
      this.method = method;
      this.params = params;
    }

    public void run() {
      try {
        inv.setReturn(method.invoke(module.locateModuleImpl(),
            params));
// TODO - pass these back or deal with them well
      } catch (IllegalArgumentException e) {
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }
  }
}
