package zserio.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import zserio.ast.Root;
import zserio.extension.common.ZserioExtensionException;

/**
 * The manager to handle all Zserio extensions.
 */
class ExtensionManager
{
    /**
     * Constructor from command line arguments.
     *
     * @param commandLineArguments Command line arguments to construct from.
     */
    public ExtensionManager(CommandLineArguments commandLineArguments)
    {
        extensions = new ArrayList<Extension>();
        ServiceLoader<Extension> loader = ServiceLoader.load(Extension.class, getClass().getClassLoader());
        Iterator<Extension> it = loader.iterator();
        while (it.hasNext())
        {
            Extension extension = it.next();
            if (ExtensionVersionMatcher.matchExtensionVersion(
                    ZserioVersion.VERSION_STRING, extension.getVersion()))
            {
                extensions.add(extension);
                extension.registerOptions(commandLineArguments.getOptions());
            }
            else
            {
                ZserioToolPrinter.printMessage("Ignoring '" + extension.getName() + "' extension " +
                        "because it expects ZserioTool version '" + extension.getVersion() + "' " +
                        "which is not compatible with current ZserioTool version '" +
                        ZserioVersion.VERSION_STRING + "'!");
            }
        }
    }

    /**
     * Prints list of all available extensions.
     */
    public void printExtensions()
    {
        if (extensions.isEmpty())
        {
            ZserioToolPrinter.printMessage("No extensions found!");
        }
        else
        {
            ZserioToolPrinter.printMessage("Available extensions:");
            for (Extension extension : extensions)
            {
                ZserioToolPrinter.printMessage("  " + extension.getName());
            }
        }
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
            for (Extension extension : extensions)
            {
                if (extension.isEnabled(parameters))
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
                        throw new ZserioExtensionException(extension.getName() + ": " +
                                getThrowableExceptionMessage(exception));
                    }
                }
                else
                {
                    ZserioToolPrinter.printMessage("Extension " + extension.getName() + " is disabled");
                }
            }
        }
    }

    private static String getThrowableExceptionMessage(Throwable throwableException)
    {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);
        throwableException.printStackTrace(printWriter);
        final String message = stringWriter.toString();
        printWriter.close();
        try
        {
            stringWriter.close();
        }
        catch (IOException exception)
        {
            // just ignore it
        }

        return message;
    }

    private final List<Extension> extensions;
}
