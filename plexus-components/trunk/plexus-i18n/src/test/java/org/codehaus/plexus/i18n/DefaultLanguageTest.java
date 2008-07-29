package org.codehaus.plexus.i18n;

import junit.framework.TestCase;

public class DefaultLanguageTest
    extends TestCase
{
  private static Language lang;

  protected void setUp()
  throws Exception
  {
    lang = new DefaultLanguage(DefaultLanguageTest.class);
  }
  
  public void testGetMessage()
  {
    String msg = lang.getMessage( "msg1" );
    assertEquals( "Hellow Orld!", msg );
  }
  
  public void testGetMessageArgs()
  {
    String msg = lang.getMessage( "msg2", "deer" );
    assertEquals( "Hellow deer Orld!", msg );
  }

}
