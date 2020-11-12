package zserio.emit.doc;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import zserio.ast.Package;
import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Extension;
import zserio.tools.Parameters;

/**
 * The extension which generates HTML documentation.
 */
public class DocExtension implements Extension
{
    @Override
    public String getName()
    {
        return "Doc Generator";
    }

    @Override
    public String getVersion()
    {
        return DocExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(org.apache.commons.cli.Options options)
    {
        Option option = new Option(OptionDoc, true, "generate HTML documentation");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);

        OptionGroup svgDiagramsGroup = new OptionGroup();
        option = new Option(OptionWithSvgDiagrams, false, "enable generation of svg diagrams from dot files");
        svgDiagramsGroup.addOption(option);
        option = new Option(OptionWithoutSvgDiagrams, false,
                "disable generation of svg diagrams from dot files (default)");
        svgDiagramsGroup.addOption(option);
        svgDiagramsGroup.setRequired(false);
        options.addOptionGroup(svgDiagramsGroup);

        option = new Option(OptionSetDotExecutable, true,
                "set dot executable to use for conversions to svg format");
        option.setArgName("dotExec");
        option.setRequired(false);
        options.addOption(option);
    }

    @Override
    public boolean isEnabled(Parameters parameters)
    {
        return parameters.argumentExists(OptionDoc);
    }

    @Override
    public void generate(Parameters parameters, Root rootNode) throws ZserioEmitException
    {
        final String outputDir = parameters.getCommandLineArg(OptionDoc);

        final boolean withSvgDiagrams = parameters.argumentExists(OptionWithSvgDiagrams);

        String dotExecutable = DefaultDotExecutable;
        if (parameters.argumentExists(OptionSetDotExecutable))
            dotExecutable = parameters.getCommandLineArg(OptionSetDotExecutable);

        if (withSvgDiagrams && !DotToSvgConverter.isDotExecAvailable(dotExecutable))
            throw new ZserioEmitException("The dot executable '" + dotExecutable + "' not found!");

        // collect used by information
        final UsedByCollector usedByCollector = new UsedByCollector();
        rootNode.walk(usedByCollector);

        // collect packages
        final PackageCollector packageCollector = new PackageCollector();
        rootNode.accept(packageCollector);

        // collect package symbols
        final SymbolCollector symbolCollector = new SymbolCollector();
        rootNode.walk(symbolCollector);

        // emit external files needed by HTML during runtime
        HtmlRuntimeEmitter.emit(outputDir);

        // emit CSS styles file
        StylesheetEmitter.emit(outputDir);

        // emit DOT files
        SymbolCollaborationDotEmitter.emit(outputDir, parameters, withSvgDiagrams, dotExecutable,
                usedByCollector, packageCollector);

        // emit HTML index file
        final Package rootPackage = rootNode.getRootPackage();
        IndexEmitter.emit(outputDir, parameters, withSvgDiagrams, usedByCollector, packageCollector,
                rootPackage);

        // emit HTML files
        final PackageEmitter packageEmitter = new PackageEmitter(outputDir, parameters, withSvgDiagrams,
                usedByCollector, symbolCollector, packageCollector);
        rootNode.walk(packageEmitter);
    }

    private final static String OptionDoc = "doc";
    private final static String OptionWithSvgDiagrams = "withSvgDiagrams";
    private final static String OptionWithoutSvgDiagrams = "withoutSvgDiagrams";
    private final static String OptionSetDotExecutable = "setDotExecutable";

    private final static String DefaultDotExecutable = "dot";
}
