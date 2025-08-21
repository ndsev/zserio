package test_utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.*;
import java.util.ArrayList;

/**
 * The class provides help methods for dumping object fields.
 */
public class Dumper
{
    public static String dump(Object obj)
    {
        final Dumper dumper = new Dumper();
        return dumper.toString(obj, 0);
    }

    public static void dumpToFile(Object obj, String fileName) throws IOException
    {
        final Dumper dumper = new Dumper();
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))
        {
            fw.write(dumper.toString(obj, 0));
        }
    }

    private String toString(Object obj, int level)
    {
        if (obj == null)
            return "null";
        if (visited.contains(obj))
            return "...";
        visited.add(obj);
        Class<?> cl = obj.getClass();
        if (cl == String.class)
            return (String)obj;
        else if (cl == Boolean.class || cl == Integer.class || cl == Byte.class || cl == Short.class ||
                cl == Long.class || cl == Float.class || cl == Double.class || cl == Character.class)
            return cl.getName() + "(" + obj.toString() + ")";

        if (cl.isArray())
        {
            StringBuilder r = new StringBuilder(cl.getComponentType().toString());
            r.append("[]{ ");
            for (int i = 0; i < Array.getLength(obj); i++)
            {
                if (i > 0)
                    r.append(", ");
                Object val = Array.get(obj, i);
                if (cl.getComponentType().isPrimitive())
                    r.append(val);
                else
                    r.append(toString(val, level));
            }
            r.append(" }");
            return r.toString();
        }

        StringBuilder r = new StringBuilder(cl.getName());
        r.append(" {");
        ++level;
        String indent = new String(new char[2 * level]).replace('\0', ' ');
        // inspect the fields of this class and all superclasses
        do
        {
            Field[] fields = cl.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            if (fields.length > 0)
                r.append("\n");
            // get the names and values of all fields
            boolean first = true;
            for (Field f : fields)
            {
                if (!Modifier.isStatic(f.getModifiers()))
                {
                    if (!first)
                        r.append("\n");
                    first = false;
                    r.append(indent + f.getName() + "=");
                    try
                    {
                        Class<?> t = f.getType();
                        Object val = f.get(obj);
                        if (t.isPrimitive())
                            r.append(val);
                        else
                            r.append(toString(val, level));
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            if (fields.length > 0)
                r.append("\n");
            cl = cl.getSuperclass();
        } while (cl != null && cl != Object.class);
        --level;
        indent = new String(new char[2 * level]).replace('\0', ' ');
        r.append(indent + "}");

        return r.toString();
    }

    private ArrayList<Object> visited = new ArrayList<Object>();
}
