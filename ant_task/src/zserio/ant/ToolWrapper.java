package zserio.ant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

/**
 * The ToolWrapper decouples the execution of a main class in terms
 * of class dependencies from the ant task itself.
 *
 * Hence it is possible to compile the main class in the same ant file
 * in which the task is executed.
 */
public class ToolWrapper
{
    public ToolWrapper(String className, Iterable<Path> classPath, boolean ignoreError)
    {
        this.className = className;
        this.classPath = classPath;
        this.ignoreError = ignoreError;
    }

    /**
     * Puts all classpath items into a list of URLs
     *
     * @return
     */
    private URL [] getUrls() throws BuildException
    {
        ArrayList<URL> urls = new ArrayList<URL>();

        for (Path p : classPath)
        {
            String [] files = p.list();
            for (String f : files)
            {
                String u = null;
                try
                {
                    if (f.endsWith(".jar"))
                    {
                        u = "jar:file:"+f+"!/";
                    }
                    else
                    {
                        u = "file:"+f+"/";
                    }

                    System.out.println("Adding " + u + " to classpath");
                    urls.add(new URL(u));

                }
                catch (MalformedURLException e)
                {
                    throw new BuildException("Malformed URL: " + u);
                }
            }
        }

        URL [] res = new URL[urls.size()];
        urls.toArray(res);

        return res;
    }

    public void callMain(String [] args) throws BuildException
    {
        try
        {
            final URL [] urls = getUrls();

            // We have to use the PreferLocalClassLoader because running
            // ant in eclipse implies certain side effects which harm a
            // correct execution. Hence we need to force java to look for
            // classes in our URLClassLoader before any other classLoader
            // is used.
            final ClassLoader classLoader = new PreferLocalClassLoader(
                    new URLClassLoader(urls, null), this.getClass().getClassLoader());

            Class<?> clazz = Class.forName(className, true, classLoader);

            // and now this is java magic
            Class<?> [] pTypes = new Class<?>[1];
            pTypes[0] = String[].class;
            final Method main = clazz.getDeclaredMethod("main", pTypes);

            final Object [] allArgs = new Object[1];
            allArgs[0] = args;

            final boolean result = (Boolean)main.invoke(null, allArgs);
            if (!ignoreError && !result)
                throw new BuildException("Zserio Tool failed.");
        }
        catch (IllegalAccessException e)
        {
            throw new BuildException(
                    "Failed to exec main on "+ className+": " + e.getMessage(), e);
        }
        catch (InvocationTargetException e)
        {
            throw new BuildException(
                    "Failed to exec main on "+ className+": " + e.getMessage(), e);
        }
        catch (ClassNotFoundException e)
        {
            throw new BuildException("Loading class " + className + " failed.", e);
        }
        catch (NoSuchMethodException e)
        {
            throw new BuildException(
                    "Class " + className + " has no main method.", e);
        }
        catch (IllegalArgumentException e)
        {
            throw new BuildException(
                    "Failed to exec main on " + className + ": " + e.getMessage(), e);
        }
    }

    private final String            className;
    private final Iterable<Path>    classPath;
    private final boolean           ignoreError;
}
