/**
 *
 */
package zserio.ant;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * ClassLoader which accepts two class loaders: The local classloader
 * and a parent classloader. In contrast to default class loader policy
 * - searching the parent class loader before searching the local class
 * loader - this class inverses the search order.
 *
 * First the local class loader is queried for a certain class, then
 * the parent class loader is asked.
 */
public class PreferLocalClassLoader extends ClassLoader
{
    public PreferLocalClassLoader(ClassLoader local, ClassLoader parent)
    {
        this.local = local;
        this.parent = parent;
    }

    @Override
    public synchronized void clearAssertionStatus()
    {
        local.clearAssertionStatus();
        parent.clearAssertionStatus();
    }

    @Override
    public URL getResource(String s)
    {
        URL url = local.getResource(s);
        if (url == null)
        {
            url = parent.getResource(s);
        }

        return url;
    }

    @Override
    public InputStream getResourceAsStream(String s)
    {
        InputStream stream = local.getResourceAsStream(s);

        if (stream == null)
        {
            stream = parent.getResourceAsStream(s);
        }

        return stream;
    }

    @Override
    public Enumeration<URL> getResources(String s) throws IOException
    {
        Enumeration<URL> urls = local.getResources(s);
        if (!urls.hasMoreElements())
        {
            urls = parent.getResources(s);
        }

        return urls;
    }

    @Override
    public Class<?> loadClass(String cn) throws ClassNotFoundException
    {
        Class<?> clazz = null;
        try
        {
            clazz = local.loadClass(cn);
        }
        catch(ClassNotFoundException e)
        {
            // ignore, ask parent instead
        }

        if(clazz == null)
        {
            // this exception might be thrown
            clazz = parent.loadClass(cn);
        }

        return clazz;
    }

    @Override
    public synchronized void setClassAssertionStatus(String arg0, boolean arg1)
    {
        local.setClassAssertionStatus(arg0, arg1);
        parent.setClassAssertionStatus(arg0, arg1);
    }

    @Override
    public synchronized void setDefaultAssertionStatus(boolean arg0)
    {
        local.setDefaultAssertionStatus(arg0);
        parent.setDefaultAssertionStatus(arg0);
    }

    @Override
    public synchronized void setPackageAssertionStatus(String arg0, boolean arg1)
    {
        local.setPackageAssertionStatus(arg0, arg1);
        parent.setPackageAssertionStatus(arg0, arg1);
    }

    private ClassLoader local;
    private ClassLoader parent;
}
