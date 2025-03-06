package org.crosswire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.zip.ZipFile;

public final class ClassUtil {
    
    private static final Logger lgr = LoggerFactory.getLogger(ClassUtil.class);
    private static final String THISMODULE = "org.crosswire.common.ClassUtil";
    private static final char PACKAGE_SEPARATOR_CHAR = '.';
    private static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    private static final String EXTENSION_CLASS = ".class";
    private static final String EXTENSION_JAR = ".jar";
    private static final String EXTENSION_ZIP = ".zip";

    public static Class<?> forName(String className) throws ClassNotFoundException
    {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public static String findClasspathEntry(String className, String classPath)
    {
        int i;
        String full = null;
        String[] paths = StringUtil.split(classPath, File.pathSeparator);
        for ( i = 0; i < paths.length; i++ )
        {
            if ( paths[i].endsWith(".zip") || paths[i].endsWith(".jar") )
            {
                ZipFile zip = null;
            }
            else
            {
                StringBuilder path = new StringBuilder(256);
                String extra = className.replace('.', File.separatorChar);
                path.append(paths[i]);
                if (paths[i].charAt(paths[i].length() - 1) != File.separatorChar)
                {
                    path.append(File.separatorChar);
                }
                path.append(extra);
                path.append(".class");
                String fileName = path.toString();
                File fil = new File(fileName);
                try
                {
                    if ( fil.isFile() )
                    {
                        if (full != null && !full.equals(fileName))
                        {
                            String msg = "Warning duplicate " + className
                                       + "found: " + full
                                       + "and " + paths[i];
                            lgr.warn(msg,THISMODULE);                        
                        }
                        else
                        {
                            full = paths[i];
                        }
                    }
                }
                catch ( Exception e )
                {
                    String msg = "File not found error - " + e.getMessage();
                    lgr.error(msg,THISMODULE);
                }
            }
        }
        return full;
    }

    public static String findClasspathEntry(String className)
    {
        String classpath = System.getProperty("java.class.path", "");
        return findClasspathEntry(className, classpath);
    }

    public static String getShortClassName(Object object, String valueIfNull)
    {
        if (object == null)  { return valueIfNull; }
        return getShortClassName(object.getClass().getName());
    }

    public static String getShortClassName(Class<?> cls)
    {
        if (cls == null) { throw new IllegalArgumentException("The class must not be null"); }
        return getShortClassName(cls.getName());
    }

    public static String getShortClassName(String className)
    {
        if ( (className == null) || (className.length() == 0) )
        {
            throw new IllegalArgumentException("The class name must not be empty");
        }
        int lastDot = 0;
        int i = 0;

        char[] chars = className.toCharArray();

        for ( i = 0; i < chars.length; i++ )
        {
            if (chars[i] == '.')
            {
                lastDot = i + 1;
            }
            else
            {
                if (chars[i] == '$')
                {
                    chars[i] = '.';
                }
            }
        }
        return new String(chars, lastDot, chars.length - lastDot);
    }
}