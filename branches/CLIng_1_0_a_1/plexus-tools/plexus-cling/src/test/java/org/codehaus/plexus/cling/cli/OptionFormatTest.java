/* Created on Sep 15, 2004 */
package org.codehaus.plexus.cling.cli;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

/**
 * @author jdcasey
 */
public class OptionFormatTest
    extends TestCase
{

    public void testShouldReturnObjectBooleanWithValidBooleanStringInput() {
        Object result = OptionFormat.BOOLEAN_FORMAT.getValue("true");
        
        assertTrue(Boolean.TRUE == result);
    }
    
    public void testShouldReturnDateWithValidDateStringInput_MMddyyyy() {
        Object result = OptionFormat.DATE_FORMAT_MM_dd_yyyy.getValue("01/02/3333");
        
        assertTrue(result instanceof Date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date)result);
        
        assertEquals("bad month", Calendar.JANUARY, cal.get(Calendar.MONTH));
        assertEquals("bad day", 2, cal.get(Calendar.DATE));
        assertEquals("bad year", 3333, cal.get(Calendar.YEAR));
    }
    
    public void testShouldReturnDateWithValidDateStringInput_yyyyddMMhhmmss() {
        Object result = OptionFormat.DATE_FORMAT_yyyy_MM_dd_kk_mm_ss.getValue("2004-09-15 15:45:30");
        
        assertTrue(result instanceof Date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date)result);
        
        assertEquals("bad month", Calendar.SEPTEMBER, cal.get(Calendar.MONTH));
        assertEquals("bad day", 15, cal.get(Calendar.DATE));
        assertEquals("bad year", 2004, cal.get(Calendar.YEAR));
        assertEquals("bad hour", 15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals("bad minute", 45, cal.get(Calendar.MINUTE));
        assertEquals("bad second", 30, cal.get(Calendar.SECOND));
    }
    
    public void testShouldReturnStringWithStringInput() {
        Object result = OptionFormat.STRING_FORMAT.getValue("This is a test");
        
        assertTrue(result instanceof String);
        
        assertEquals("This is a test", result);
    }
    
    public void testShouldReturnObjectLongWithValidNumberStringInput() {
        Object result = OptionFormat.NUMBER_FORMAT.getValue("123456789123456789");
        
        assertTrue(result instanceof Long);
        
        assertEquals(new Long(123456789123456789L), result);
    }
    
    public void testShouldReturnObjectDoubleWithValidFractionalNumberStringInput() {
        Object result = OptionFormat.FRACTIONAL_NUMBER_FORMAT.getValue("123456789123456789.123456789123456789");
        
        assertTrue(result instanceof Double);
        
        assertEquals(new Double(123456789123456789.123456789123456789D), result);
    }
    
    public void testShouldReturnFileWithValidFilePathStringInput() {
        Object result = OptionFormat.FILE_FORMAT.getValue("/tmp/somefile.txt");
        
        assertTrue(result instanceof File);
        
        assertEquals(new File("/tmp/somefile.txt"), result);
    }
    
    public void testShouldReturnFileWithValidDirPathStringInput() {
        Object result = OptionFormat.DIR_FORMAT.getValue("/tmp");
        
        assertTrue(result instanceof File);
        
        assertEquals(new File("/tmp"), result);
    }
    
    public void testShouldReturnStringWithValidJavaPackageStringInput() {
        Object result = OptionFormat.JAVA_PACKAGE_FORMAT.getValue("org.codehaus.plexus.cling");
        
        assertTrue(result instanceof String);
        
        assertEquals("org.codehaus.plexus.cling", result);
    }
    
    public void testShouldReturnClassWithValidJavaClassStringInput() {
        Object result = OptionFormat.JAVA_CLASS_FORMAT.getValue(OptionFormat.class.getName());
        
        assertTrue(result instanceof Class);
        
        assertEquals(OptionFormat.class, result);
    }
    
}
