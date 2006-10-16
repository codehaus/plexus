package org.codehaus.plexus.formica;

/*
 * Copyright (c) 2004, Codehaus.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class TargetObject
{
    private String field0;
    private String field1;
    private String field2;
    private String field3;
    private String field4;

    private String passwordOne;
    private String passwordTwo;
    private String emailOne;
    private String emailTwo;

    public String getField0()
    {
        return field0;
    }

    public void setField0(  String field0 )
    {
        this.field0 = field0;
    }

    public String getField1()
    {
        return field1;
    }

    public void setField1(  String field1 )
    {
        this.field1 = field1;
    }

    public String getField2()
    {
        return field2;
    }

    public void setField2(  String field2 )
    {
        this.field2 = field2;
    }

    public String getField3()
    {
        return field3;
    }

    public void setField3(  String field3 )
    {
        this.field3 = field3;
    }

    public String getField4()
    {
        return field4;
    }

    public void setField4(  String field4 )
    {
        this.field4 = field4;
    }


    public String getPasswordOne()
    {
        return passwordOne;
    }

    public void setPasswordOne(  String passwordOne )
    {
        this.passwordOne = passwordOne;
    }

    public String getPasswordTwo()
    {
        return passwordTwo;
    }

    public void setPasswordTwo(  String passwordTwo )
    {
        this.passwordTwo = passwordTwo;
    }

    public String getEmailOne()
    {
        return emailOne;
    }

    public void setEmailOne(  String emailOne )
    {
        this.emailOne = emailOne;
    }

    public String getEmailTwo()
    {
        return emailTwo;
    }

    public void setEmailTwo(  String emailTwo )
    {
        this.emailTwo = emailTwo;
    }


    public String toString()
    {
        return "org.codehaus.plexus.formica.TargetObject{" +
            "field0='" + field0 + "'" +
            ", field1='" + field1 + "'" +
            ", field2='" + field2 + "'" +
            ", field3='" + field3 + "'" +
            ", field4='" + field4 + "'" +
            ", passwordOne='" + passwordOne + "'" +
            ", passwordTwo='" + passwordTwo + "'" +
            ", emailOne='" + emailOne + "'" +
            ", emailTwo='" + emailTwo + "'" +
            "}";
    }
}
