package org.codehaus.plexus.mainclass;

/*
 * Copyright 2007 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.EmptyVisitor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link MainClassFinder) using
 * <a href="http://asm.objectweb.org/">ASM</a> to inspect the class files.
 */
public class AsmMainClassFinder
    extends AbstractMainClassFinder
{
    private static final String ARGS_SIGNATURE = "([Ljava/lang/String;)V";

    protected boolean containsMainClass( InputStream inputStream )
    {
        try
        {
            ClassReader reader = new ClassReader( inputStream );
            HasMainMethodClassVisitor v = new HasMainMethodClassVisitor();
            reader.accept( v, 0 );
            return v.isMainMethod();
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    class HasMainMethodClassVisitor
        extends EmptyVisitor
    {

        private boolean mainMethod;

        public MethodVisitor visitMethod( int access, String name, String desc, String signature, String[] exceptions )
        {
            if ( access == Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC && "main".equals( name ) &&
                ARGS_SIGNATURE.equals( desc ) )
            {
                this.mainMethod = true;
            }
            return null;
        }

        public boolean isMainMethod()
        {
            return mainMethod;
        }

        public void setMainMethod( boolean mainMethod )
        {
            this.mainMethod = mainMethod;
        }
    }
}
