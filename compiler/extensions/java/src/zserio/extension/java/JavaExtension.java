package zserio.extension.java;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zserio.ast.Root;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;
import zserio.tools.ZserioToolPrinter;

/**
 * The extension which generates Java API sources.
 */
public class JavaExtension implements Extension
{
    @Override
    public String getName()
    {
        return "Java Generator";
    }

    @Override
    public String getVersion()
    {
        return JavaExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(org.apache.commons.cli.Options options)
    {
        JavaExtensionParameters.registerOptions(options);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return JavaExtensionParameters.hasOptionJava(parameters);
    }

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final JavaExtensionParameters javaParameters = new JavaExtensionParameters(parameters);

        final List<JavaDefaultEmitter> emitters = new ArrayList<JavaDefaultEmitter>();
        emitters.add(new BitmaskEmitter(javaParameters));
        emitters.add(new EnumerationEmitter(javaParameters));
        emitters.add(new StructureEmitter(javaParameters));
        emitters.add(new ChoiceEmitter(javaParameters));
        emitters.add(new UnionEmitter(javaParameters));
        emitters.add(new SqlDatabaseEmitter(javaParameters));
        emitters.add(new SqlTableEmitter(javaParameters));
        emitters.add(new ConstEmitter(javaParameters));
        emitters.add(new ServiceEmitter(javaParameters));
        emitters.add(new PubsubEmitter(javaParameters));

        // emit Java code
        for (JavaDefaultEmitter emitter: emitters)
            rootNode.walk(emitter);

        printReport(emitters);
    }

    private void printReport(List<JavaDefaultEmitter> emitters)
    {
        int generated = 0;
        int skipped = 0;

        for (JavaDefaultEmitter javaEmitter : emitters)
        {
            for (Map.Entry<File, Boolean> entry : javaEmitter.getOutputFiles().entrySet())
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
