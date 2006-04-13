package com.hksys.aspectj;

import org.aspectj.lang.JoinPoint;

/**
 * Used to see what's going on inside of the AspectJ BCEL weaver.
 *
 * @author Stephen Haberman
 */
public aspect TraceBcelWeaver
{

    pointcut constructors(): execution(org.aspectj.weaver.bcel.*.new(..));
    pointcut methods(): execution(* org.aspectj.weaver.bcel.*.*(..));

    before(): constructors()
    {
        debugStart(thisJoinPoint);
    }

    after(): constructors()
    {
        debugEnd(thisJoinPoint);
    }

    before(): methods()
    {
        debugStart(thisJoinPoint);
    }

    after(): methods()
    {
        debugEnd(thisJoinPoint);
    }

    after() returning (boolean b): methods()
    {
        System.out.println("boolean " + b);
    }

    private void debugStart(JoinPoint jp)
    {
        System.out.println("Start: " + jp.getStaticPart());

        String arg = "";
        for (int i = 0; i < jp.getArgs().length; i++)
        {
            if (jp.getArgs()[i] != null)
            {
                arg += jp.getArgs()[i].toString() + ", ";
            }
            else
            {
                arg += "null, ";
            }
        }
        System.out.println("Args : " + arg);
    }

    private void debugEnd(JoinPoint jp)
    {
        System.out.println("End  : " + jp.getSignature());

        String arg = "";
        for (int i = 0; i < jp.getArgs().length; i++)
        {
            if (jp.getArgs()[i] != null)
            {
                arg += jp.getArgs()[i].toString() + ", ";
            }
            else
            {
                arg += "null, ";
            }
        }
        if (jp.getArgs().length == 0)
        {
            arg = "null";
        }
        System.out.println("Args : " + arg);
    }

}

