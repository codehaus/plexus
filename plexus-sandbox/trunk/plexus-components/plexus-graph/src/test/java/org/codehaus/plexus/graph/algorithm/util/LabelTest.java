package org.codehaus.plexus.graph.algorithm.util;

import junit.framework.*;

public class LabelTest
  extends TestCase
{
  private String testName = null;

  public LabelTest( String testName ) {
    super( testName );
    this.testName = testName;
  }

  private Label L1;
  private Label L1_;
  private Label L1__;
  private Label L2;
  private Label L3;
  private Label L4;
  private Label L5;
  private Label L6;

  public void setUp() {
    L1 = new Label();
    L2 = new Label();
    L3 = new Label();
    L4 = new Label();
    L5 = new Label();
    L6 = new Label();

    L1_ = new Label( L1 );

    L1__ = new Label( L1_ );
  }

  public void testDefaultConfig()
    throws Throwable
  {
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L1_.getRoot(), L1 );
    assertEquals( L1__.getRoot(), L1 );
  }

  public void testSetRoot1() 
    throws Throwable
  {
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L2 );

    L2.setRoot( L1 );
    assertEquals( L2.getRoot(), L1 );
  }

  public void testSetRoot2()
    throws Throwable
  {
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L2 );
    assertEquals( L3.getRoot(), L3 );

    L3.setRoot( L2 );
    L2.setRoot( L1 );
    
    assertEquals( L3.getRoot(), L1 );
    assertEquals( L2.getRoot(), L1 );
    assertEquals( L1.getRoot(), L1 );
  }

  public void testSetRoot3() 
    throws Throwable
  {
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L2 );
    assertEquals( L3.getRoot(), L3 );

    L2.setRoot( L1 );
    L3.setRoot( L2 );

    assertEquals( L3.getRoot(), L1 );
    assertEquals( L2.getRoot(), L1 );
    assertEquals( L1.getRoot(), L1 );
  }

  public void testSetRoot4()
    throws Throwable
  { 
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L2 );
    assertEquals( L3.getRoot(), L3 );
    assertEquals( L4.getRoot(), L4 );

    L4.setRoot( L3 );
    L2.setRoot( L1 );
    L3.setRoot( L1 );

    assertEquals( L4.getRoot(), L1 );
    assertEquals( L3.getRoot(), L1 );
    assertEquals( L2.getRoot(), L1 );
    assertEquals( L1.getRoot(), L1 );
  }

  public void testSetRootCycle() 
    throws Throwable
  {
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L2 );

    L1.setRoot( L2 );
    L2.setRoot( L1 );

    assertEquals( L2.getRoot(), L2 );
    assertEquals( L1.getRoot(), L2 );
  }

  public void setDiamond1() throws Throwable {
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L2 );
    assertEquals( L3.getRoot(), L3 );
    assertEquals( L4.getRoot(), L4 );

    L2.setRoot( L1 );
    L3.setRoot( L1 );
    L4.setRoot( L3 );

    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L1 );
    assertEquals( L3.getRoot(), L1 );
    assertEquals( L4.getRoot(), L1 );
  }

  public void setDiamond2() throws Throwable {
    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L2 );
    assertEquals( L3.getRoot(), L3 );
    assertEquals( L4.getRoot(), L4 );

    L4.setRoot( L3 );
    L2.setRoot( L1 );
    L3.setRoot( L1 );

    assertEquals( L1.getRoot(), L1 );
    assertEquals( L2.getRoot(), L1 );
    assertEquals( L3.getRoot(), L1 );
    assertEquals( L4.getRoot(), L1 );
  }
}



