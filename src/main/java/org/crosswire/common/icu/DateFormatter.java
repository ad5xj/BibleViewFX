package org.crosswire.common.icu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

public final class DateFormatter 
{
    public static final int FULL = 0;
    public static final int LONG = 1;
    public static final int MEDIUM = 2;
    public static final int SHORT = 3;
    public static final int DEFAULT = 2;

    private static final String THISMODULE = "org.crosswire.common.icu.DateFormatter";
    private static final String DEFAULT_SIMPLE_DATE_FORMAT_CLASS = "java.time.format.DateTimeFormatter";
    private static final String DEFAULT_DATE_FORMAT_CLASS = "java.time.format.DateTimeFormatFormatter";

    private static final Logger lgr = LoggerFactory.getLogger(DateFormatter.class);

    private Object formatter;

    private Class<?> formatterClass;

    private static Class<?> defaultSimpleDateFormat;
    private static Class<?> defaultDateFormat;

    static
    {
        try
        {
            defaultSimpleDateFormat = ClassUtil.forName("com.ibm.icu.text.SimpleDateFormat");
        }
        catch ( ClassNotFoundException ex )
        {
            String msg = "Error loading simple date format class [" 
                       + defaultSimpleDateFormat.toString()
                       + " ]";
            lgr.error(msg,THISMODULE);
        }
        try
        {
            defaultDateFormat = ClassUtil.forName("java.time.format.DateTimeFormatter");
        }
        catch (ClassNotFoundException ex)
        {
            lgr.info("Error loading date format class [{}]", "java.time.foramt.DateTimeFormatter");
        }
    }

    public static DateFormatter getDateInstance(int format)
    {
        DateFormatter fmt = new DateFormatter();
        boolean oops = false;
        try
        {
            fmt.formatterClass = defaultDateFormat;
            Class<?>[] instanceTypes = new Class[]
            {
                int.class
            };
            Object[] instanceParams =
            {
                Integer.valueOf(format)
            };
            fmt.formatter = ReflectionUtil.invoke(fmt.formatterClass, fmt.formatterClass, "getDateInstance", instanceParams, instanceTypes);
        }
        catch (NoSuchMethodException e)
        {
            oops = true;
        }
        catch (IllegalAccessException e)
        {
            oops = true;
        }
        catch (InvocationTargetException e)
        {
            oops = true;
        }
        catch (NullPointerException e)
        {
            oops = true;
        }
        if (oops)
        {
            fmt.formatterClass = DateFormat.class;
            fmt.formatter = DateFormat.getDateInstance(format);
        }
        return fmt;
    }

    public static DateFormatter getDateInstance()
    {
        return getDateInstance(2);
    }

    public static DateFormatter getSimpleDateInstance(String format)
    {
        DateFormatter fmt = new DateFormatter();
        boolean oops = false;
        try
        {
            fmt.formatterClass = defaultSimpleDateFormat;
            fmt.formatter = ReflectionUtil.construct(fmt.formatterClass, new Object[]
            {
                format
            });
        }
        catch (NoSuchMethodException e)
        {
            oops = true;
        }
        catch (IllegalAccessException e)
        {
            oops = true;
        }
        catch (InvocationTargetException e)
        {
            oops = true;
        }
        catch (NullPointerException e)
        {
            oops = true;
        }
        catch (InstantiationException e)
        {
            oops = true;
        }
        if (oops)
        {
            fmt.formatterClass = SimpleDateFormat.class;
            fmt.formatter = new SimpleDateFormat(format);
        }
        return fmt;
    }

    public void setLenient(boolean lenient)
    {
        try
        {
            Class<?>[] lenientTypes = new Class[]
            {
                boolean.class
            };
            Object[] lenientParams =
            {
                Boolean.valueOf(lenient)
            };
            ReflectionUtil.invoke(this.formatterClass, this.formatter, "setLenient", lenientParams, lenientTypes);
        }
        catch (NoSuchMethodException e)
        {
            assert false : e;
        }
        catch (IllegalAccessException e)
        {
            assert false : e;
        }
        catch (InvocationTargetException e)
        {
            assert false : e;
        }
    }

    public String format(Date date)
    {
        try
        {
            return (String) ReflectionUtil.invoke(this.formatterClass, this.formatter, "format", new Object[]
            {
                date
            });
        }
        catch (NoSuchMethodException e)
        {
            assert false : e;
        }
        catch (IllegalAccessException e)
        {
            assert false : e;
        }
        catch (InvocationTargetException e)
        {
            assert false : e;
        }
        return "";
    }

    public Date parse(String text)
    {
        try
        {
            return (Date) ReflectionUtil.invoke(this.formatterClass, this.formatter, "parse", new Object[]
            {
                text
            });
        }
        catch (NoSuchMethodException e)
        {
            assert false : e;
        }
        catch (IllegalAccessException e)
        {
            assert false : e;
        }
        catch (InvocationTargetException e)
        {
            assert false : e;
        }
        return new Date();
    }
}
