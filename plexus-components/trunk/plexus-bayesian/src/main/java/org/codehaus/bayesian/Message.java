package org.codehaus.bayesian;

import java.io.*;
import java.util.*;

//Util package

/**
 Objects of this class will be used to hold E-Mail messages in internal storage variables.
 The contents of the variables can be set and retreived.<br>
 <br>

 Class MailMessage <b>Version 1.0</b>.
 14 February 2003

 */
public class Message implements Serializable
{
    private ArrayList body = null;
    private ArrayList header = null;
    private String subject = null;
    private String from = null;
    private String date = null;
    private String content_type = null;
    private String content_transfer_encoding = null;
    private String uidl = null;
    private double spammy = 0.5;
    private int size = 0;
    private String boundary = null;


    /**
     Default Constructor
     */


////////////////////////// SET /////////////////////////////

    //// setBody ////
    /**
     Sets the Body of the E-mail.
     When collecting E-Mail from the server we will use the retr command.
     An option step will be to remove the header information as this will already have
     been gathered from the TOP command.

     @param b - the body of the E-mail stored in an ArrayList object.
     @return boolean - true if the command succeeds.

     */
    public boolean setBody( ArrayList b )
    {
        body = b;
        return true;
    }

    //// setHeader ////
    /**
     Stores the Header information in the MailMessage Object.
     After collection the header information from the pop3 service and storing it in
     an ArrayList object it is stored within a MailMessage object using this command.
     <code>TOP</code> is used to collect header information from the server.

     @param h - the header information of the E-Mail stored in an Arraylist
     @return boolean - true if the command was successful.
     */
    public boolean setHeader( ArrayList h )
    {
        header = h;
        return true;
    }

    //// setSubject ////
    /**
     Stores the Subject in the MailMessage Object.
     Although the subject is stored in the header information its handy to have this
     method to return it. The Subject is extracted in the pop3client::getMail().

     @param s - the subject of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setSubject( String s )
    {
        subject = s;
        return true;
    }

    //// setFrom ////
    /**
     Stores the From address in the MailMessage Object.
     Although the From address is stored in the header information its handy to have
     this method to return it. The From is extracted in the pop3client::getMail().

     @param f - the subject of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setFrom( String f )
    {
        from = f;
        return true;
    }

    //// setDate ////
    /**
     Stores the Date in the MailMessage Object.
     Although the Date is stored in the header information its handy to have this
     method to return it. The Date is extracted in the pop3client::getMail().
     Note this is a string representing a date Not a Date object.

     @param d - the subject of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setDate( String d )
    {
        date = d;
        return true;
    }

    //// setContentType ////
    /**
     Stores the Content-Type in the MailMessage Object.
     Although the Content-Type is stored in the header information its handy to have this
     method to return it. The Content-Type is extracted in the pop3client::getMail().

     @param c - the Content-Type of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setContentType( String c )
    {
        content_type = c;
        return true;
    }


    //// setContentTransferEncoding ////
    /**
     Stores the Content-Transfer-Encoding in the MailMessage Object.
     The From is extracted in the pop3client::getMail(). Its only really of use
     when we have text/plain Content-Type.

     @param c - the Content-Transfer-Encoding of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setContentTransferEncoding( String c )
    {
        content_transfer_encoding = c;
        return true;
    }

    //// setUidl ////
    /**
     Stores the Unique ID in the MailMessage Object.
     The From is extracted in the pop3client::getMail().
     This may be needed for checking that we have not already downloaded a message.

     @param u - the unique id of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setUidl( String u )
    {
        uidl = u;
        return true;
    }


    //// setSpammy ////
    /**
     Stores the calculated probability of this Message being Spam.
     This value is a double always within the range of 0.0 and 1.0

     1.0 == certain spam
     0.0 == certain ham


     @param s - the calculated spam probability.
     @return boolean - true if the command was successful.
     */
    public boolean setSpammy( double s )
    {
        spammy = s;
        return true;
    }


    //// setSize ////
    /**
     Stores the size of the MailMessage.
     The From is extracted in the pop3client::getMail().
     Its obtained for use in the Pop3 Server.

     @param i - the size of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setSize( int i )
    {
        size = i;
        return true;
    }

    //// setBoundary ////
    /**
     Stores the MIME boundary of the E-Mail.
     This is only relevent if the MIME type is multipart/* or message/*
     This is set in the pop3client::getMail().

     @param b - the boundary of the E-Mail.
     @return boolean - true if the command was successful.
     */
    public boolean setBoundary( String b )
    {
        boundary = b;
        return true;
    }


//////////////////////////////////// GET //////////////////////////////////////

    //// getBody ////
    /**
     Retreives the Body from the MailMessage.
     Note this does not copy the contents rather it passes back the reference.
     (Perhaps get this to do a clone and then pass clone back)

     @return ArrayList containing the body of the E-Mail.
     */
    public ArrayList getBody()
    {
        return body;
    }

    //// getHeader ////
    /**
     Retreives the header from the MailMessage.
     Note this does not copy the contents rather it passes back the reference.
     (Perhaps get this to do a clone and then pass clone back)

     @return ArrayList containing the header of the E-Mail.
     */
    public ArrayList getHeader()
    {
        return header;
    }

    //// getSubject ////
    /**
     Retreives Subject of the Message.
     This is the subject of the stored message.

     @return String containing the Subject.
     */
    public String getSubject()
    {
        return subject;
    }

    //// getFrom ////
    /**
     Retreives the From: header of the Message.

     @return String containing the header From:.
     */
    public String getFrom()
    {
        return from;
    }

    //// getDate ////
    /**
     Retreives the date of the Message.

     @return String containing the header Date:.
     */
    public String getDate()
    {
        return date;
    }

    //// getContentType ////
    /**
     Retreives Content-Type of the Message.
     This is used to determine what parsing is needed to be done in the filtering stage.

     @return String containing the header Content-Type.
     */
    public String getContentType()
    {
        return content_type;
    }

    //// getContentTransferEncoding ////
    /**
     Retreives Content-Transfer-Encoding of the Message.
     This is used to determine what parsing is needed to be done in the filtering stage.
     If in QP or Base64 then there will need to be decoding

     @return String containing the header Content-Transfer-Encoding.
     */
    public String getContentTransferEncoding()
    {
        return content_transfer_encoding;
    }

    //// getUidl ////
    /**
     Retreives Unique Identity of the Message.
     This is used to keep track of messages in the maildrop.

     @return String containing the header UIDL.
     */
    public String getUidl()
    {
        return uidl;
    }


    //// getSpammy ////
    /**
     Retreives spam probability of this Message.
     This is used to determine whether or not the stored E-mail is in fact a spam or ham.
     The value is calculated in the bayesian and assigned.

     Values are always between 0.0 and 1.0
     0.0 == certain ham
     1.0 == certain spam

     @return double value of the messages spam probability.
     */
    public double getSpammy()
    {
        return spammy;
    }


    //// getSize ////
    /**
     Return the size of the message in octets.
     This value is obtained from the list method!
     @return the size of the message in octets.
     */
    public int getSize()
    {
        return size;
    }

    //// getBoundary ////
    /**
     Return the boundary of the message.
     This is the boundary marker for use in MIME messages.
     It will only be relevent in MIME messeges of type multipart/
     or message/

     @return the boundary of the MIME message.
     */
    public String getBoundary()
    {
        return boundary;
    }

}
