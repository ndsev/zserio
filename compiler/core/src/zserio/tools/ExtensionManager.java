package zserio.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import zserio.ast.Root;
import zserio.extension.common.ZserioExtensionException;

/**
 * The manager to handle all Zserio extensions.
 */
final class ExtensionManager
{
    /**
     * Constructor from command line arguments.
     *
     * @param commandLineArguments Command line arguments to construct from.
     */
    public ExtensionManager(CommandLineArguments commandLineArguments)
    {
        extensions = new ArrayList<Extension>();
        this.commandLineArguments = commandLineArguments;
        final ServiceLoader<Extension> loader = ServiceLoader.load(Extension.class, getClassLoader());
        Iterator<Extension> it = loader.iterator();
        while (it.hasNext())
        {
            Extension extension = it.next();
            if (ExtensionVersionMatcher.match(ZserioVersion.VERSION_STRING, extension.getZserioVersion()))
            {
                extensions.add(extension);
                extension.registerOptions(commandLineArguments.getOptions());
            }
            else
            {
                ZserioToolPrinter.printMessage("Ignoring '" + extension.getName() + "' extension "
                        + "because it expects Zserio core version '" + extension.getZserioVersion() + "' "
                        + "which is not compatible with current Zserio core version '" +
                        ZserioVersion.VERSION_STRING + "'!");
            }
        }
    }

    /**
     * Gets list of available extensions.
     *
     * @return List of extensions.
     */
    public List<Extension> getExtensions()
    {
        return Collections.unmodifiableList(extensions);
    }

    /**
     * Calls all available Zserio extensions.
     *
     * @param rootNode   The root node of Zserio tree to process.
     * @param parameters Parameters to pass to extensions.
     *
     * @throws ZserioExtensionException Throws in case of any error in any extension.
     */
    public void callExtensions(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        if (extensions.isEmpty())
        {
            ZserioToolPrinter.printMessage("No extensions found!");
        }
        else
        {
            if (commandLineArguments.getWithCrossExtensionCheck())
            {
                for (Extension extension : extensions)
                    check(rootNode, extension, parameters);
            }

            for (Extension extension : extensions)
            {
                if (extension.isEnabled(parameters))
                {
                    if (!commandLineArguments.getWithCrossExtensionCheck())
                        check(rootNode, extension, parameters);
                    process(rootNode, extension, parameters);
                }
                else
                {
                    ZserioToolPrinter.printMessage("Extension " + extension.getName() + " is disabled");
                }
            }
        }
    }

    private ClassLoader getClassLoader()
    {
        final ClassLoader currentClassLoader = getClass().getClassLoader();
        final File workingDirectory = getWorkingDirectory();
        if (workingDirectory == null)
            return currentClassLoader;

        final File[] fileList = workingDirectory.listFiles();
        if (fileList == null)
            return currentClassLoader;

        final ArrayList<URL> urlArray = new ArrayList<URL>();
        try
        {
            for (File file : fileList)
            {
                if (isFileZserioExtension(file))
                {
                    urlArray.add(file.toURI().toURL());
                    urlArray.addAll(getDependentJarsFromManifest(file));
                }
            }
        }
        catch (MalformedURLException excpt)
        {
            return currentClassLoader;
        }

        final URLClassLoader urlClassLoader =
                new URLClassLoader(urlArray.toArray(new URL[urlArray.size()]), currentClassLoader);

        return urlClassLoader;
    }

    private File getWorkingDirectory()
    {
        try
        {
            final URI execUri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            final Path execPath = Paths.get(execUri);
            final Path execParentPath = execPath.getParent();

            return (execParentPath == null) ? null : execParentPath.toFile();
        }
        catch (SecurityException | URISyntaxException excpt)
        {
            return null;
        }
    }

    private boolean isFileZserioExtension(File file)
    {
        if (!file.isFile())
            return false;

        final String fileName = file.getName();
        if (!fileName.endsWith(".jar"))
            return false;

        if (!fileName.startsWith("zserio_"))
            return false;

        if (fileName.equals("zserio_core.jar"))
            return false;

        if (fileName.endsWith("_javadocs.jar"))
            return false;

        if (fileName.endsWith("_sources.jar"))
            return false;

        return true;
    }

    private List<URL> getDependentJarsFromManifest(File file)
    {
        final ArrayList<URL> dependentJars = new ArrayList<URL>();
        try (JarFile jarFile = new JarFile(file))
        {
            final Manifest manifest = jarFile.getManifest();
            if (manifest != null)
            {
                final String classPaths = manifest.getMainAttributes().getValue("class-path");
                if (classPaths != null)
                {
                    final File parentFile = file.getParentFile();
                    for (String classPath : classPaths.split("\\s+"))
                    {
                        final File dependentJarFile = new File(parentFile, classPath);
                        dependentJars.add(dependentJarFile.toURI().toURL());
                    }
                }
            }
        }
        catch (IOException excpt)
        {
            // silently ignored
        }

        return dependentJars;
    }

    private static void check(Root rootNode, Extension extension, ExtensionParameters parameters)
            throws ZserioExtensionException
    {
        try
        {
            ZserioToolPrinter.printMessage("Calling " + extension.getName() + " extension check");
            extension.check(rootNode, parameters);
        }
        catch (ZserioExtensionException exception)
        {
            throw new ZserioExtensionException(extension.getName() + ": " + exception.getMessage());
        }
        catch (Throwable exception)
        {
            throw new ZserioExtensionException(
                    extension.getName() + ": " + getThrowableExceptionMessage(exception));
        }
    }

    private static void process(Root rootNode, Extension extension, ExtensionParameters parameters)
            throws ZserioExtensionException
    {
        try
        {
            ZserioToolPrinter.printMessage("Calling " + extension.getName() + " extension");
            extension.process(rootNode, parameters);
        }
        catch (ZserioExtensionException exception)
        {
            throw new ZserioExtensionException(extension.getName() + ": " + exception.getMessage());
        }
        catch (Throwable exception)
        {
            throw new ZserioExtensionException(
                    extension.getName() + ": " + getThrowableExceptionMessage(exception));
        }
    }

    private static String getThrowableExceptionMessage(Throwable throwableException)
    {
        String message = null;

        try (final StringWriter stringWriter = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(stringWriter);)
        {
            throwableException.printStackTrace(printWriter);
            message = stringWriter.toString();
        }
        catch (IOException e)
        {
            // ignore error in StringWriter.close
            if (message == null)
                message = e.getMessage();
        }

        return message;
    }

    private final List<Extension> extensions;
    private final CommandLineArguments commandLineArguments;
}
