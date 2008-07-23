package com.rectang.dpmr;

import junit.framework.TestCase;

/**
 * TODO: Document Me
 *
 * @author Andrew Williams <andy@handyande.co.uk>
 * @since 01-Oct-2006
 */
public class DPMRTest extends TestCase {

  public void testMain() throws Exception {
    DPMR dpmr = DPMR.getInstance("test");

    assertFalse(dpmr.isModuleLoaded("notloaded"));

    // need more tests, but we need some module stubs for that...
  }
}
