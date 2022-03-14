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
        this.commandLineArguments = commandLineArguments;
        ServiceLoader<Extension> loader = ServiceLoader.load(Extension.class, getClass().getClassLoader());
        Iterator<Extension> it = loader.iterator();
        while (it.hasNext())
        {
            Extension extension = it.next();
            if (ExtensionVersionMatcher.match(ZserioVersion.VERSION_STRING, extension.getVersion()))
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
            throw new ZserioExtensionException(extension.getName() + ": " +
                    getThrowableExceptionMessage(exception));
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
            throw new ZserioExtensionException(extension.getName() + ": " +
                    getThrowableExceptionMessage(exception));
        }
    }

    private static String getThrowableExceptionMessage(Throwable throwableException)
    {
        String message = null;

        try (
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
        )
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
