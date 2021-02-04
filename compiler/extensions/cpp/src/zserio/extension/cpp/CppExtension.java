package zserio.extension.cpp;

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
 * The extension which generates C++ API sources.
 */
public class CppExtension implements Extension
{
    @Override
    public String getName()
    {
        return "C++11 Generator";
    }

    @Override
    public String getVersion()
    {
        return CppExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(Options options)
    {
        CppExtensionParameters.registerOptions(options);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return CppExtensionParameters.hasOptionCpp(parameters);
    }

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final CppExtensionParameters cppParameters = new CppExtensionParameters(parameters);

        final List<CppDefaultEmitter> emitters = new ArrayList<CppDefaultEmitter>();
        emitters.add(new ConstEmitter(cppParameters));
        emitters.add(new SubtypeEmitter(cppParameters));
        emitters.add(new EnumerationEmitter(cppParameters));
        emitters.add(new BitmaskEmitter(cppParameters));
        emitters.add(new StructureEmitter(cppParameters));
        emitters.add(new ChoiceEmitter(cppParameters));
        emitters.add(new UnionEmitter(cppParameters));
        emitters.add(new SqlDatabaseEmitter(cppParameters));
        emitters.add(new SqlTableEmitter(cppParameters));
        emitters.add(new ServiceEmitter(cppParameters));
        emitters.add(new PubsubEmitter(cppParameters));

        // emit C++ code
        for (CppDefaultEmitter emitter: emitters)
            rootNode.walk(emitter);

        printReport(emitters);
    }

    private void printReport(List<CppDefaultEmitter> emitters)
    {
        int generated = 0;
        int skipped = 0;

        for (CppDefaultEmitter cppEmitter : emitters)
        {
            for (Map.Entry<File, Boolean> entry : cppEmitter.getOutputFiles().entrySet())
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
