package zserio.extension.python;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.Options;

import zserio.ast.Root;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;
import zserio.tools.ZserioToolPrinter;

/**
 * The extension which generates Python API sources.
 */
public class PythonExtension implements Extension
{
    @Override
    public String getName()
    {
        return "Python Generator";
    }

    @Override
    public String getVersion()
    {
        return PythonExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(Options options)
    {
        PythonExtensionParameters.registerOptions(options);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return PythonExtensionParameters.hasOptionPython(parameters);
    }

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final PythonExtensionParameters pythonParameters = new PythonExtensionParameters(parameters);

        final List<PythonDefaultEmitter> emitters = new ArrayList<PythonDefaultEmitter>();
        emitters.add(new ConstEmitter(pythonParameters));
        emitters.add(new EnumerationEmitter(pythonParameters));
        emitters.add(new BitmaskEmitter(pythonParameters));
        emitters.add(new SubtypeEmitter(pythonParameters));
        emitters.add(new InitPyEmitter(pythonParameters));
        emitters.add(new ApiEmitter(pythonParameters));
        emitters.add(new StructureEmitter(pythonParameters));
        emitters.add(new ChoiceEmitter(pythonParameters));
        emitters.add(new UnionEmitter(pythonParameters));
        emitters.add(new SqlTableEmitter(pythonParameters));
        emitters.add(new SqlDatabaseEmitter(pythonParameters));
        emitters.add(new ServiceEmitter(pythonParameters));
        emitters.add(new PubsubEmitter(pythonParameters));

        // emit Python code
        for (PythonDefaultEmitter pythonEmitter: emitters)
        {
            rootNode.walk(pythonEmitter);
        }

        printReport(emitters);
    }

    private void printReport(List<PythonDefaultEmitter> emitters)
    {
        int generated = 0;
        int skipped = 0;

        for (PythonDefaultEmitter pythonEmitter : emitters)
        {
            for (Map.Entry<File, Boolean> entry : pythonEmitter.getOutputFiles().entrySet())
            {
                if (entry.getValue())
                    generated++;
                else
                    skipped++;
            }
        }

        ZserioToolPrinter.printMessage("  Generated " + generated + " files" +
                (skipped > 0 ? ", skipped " + skipped + " files" : ""));
    }
}
