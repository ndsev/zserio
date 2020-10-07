package zserio.emit.doc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import zserio.ast.Root;
import zserio.emit.common.Emitter;
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

        option = new Option(OptionSetDotLinksPrefix, true, "set doc links prefix in generated dot file");
        option.setArgName("prefix");
        option.setRequired(false);
        options.addOption(option);

        OptionGroup svgDiagramsGroup = new OptionGroup();
        option = new Option(OptionWithSvgDiagrams, false,
                "enable generation of svg diagrams from dot files");
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

        String dotLinksPrefix = null;
        if (parameters.argumentExists(OptionSetDotLinksPrefix))
            dotLinksPrefix = parameters.getCommandLineArg(OptionSetDotLinksPrefix);

        final boolean withSvgDiagrams = parameters.argumentExists(OptionWithSvgDiagrams);

        String dotExecutable = DefaultDotExecutable;
        if (parameters.argumentExists(OptionSetDotExecutable))
            dotExecutable = parameters.getCommandLineArg(OptionSetDotExecutable);

        if (withSvgDiagrams && !DotToSvgConverter.isDotExecAvailable(dotExecutable))
            throw new ZserioEmitException("The dot executable '" + dotExecutable + "' not found!");

        // collect used by information
        final UsedByCollector usedByCollector = new UsedByCollector();
        rootNode.emit(usedByCollector);

        // emit HTML index file
        HtmlIndexEmitter.emit(outputDir);

        // emit CSS styles file
        WebStylesEmitter.emit(outputDir);

        // emit DOT and HTML files (DOT files must be before HTML files)
        final List<Emitter> emitters = new ArrayList<Emitter>();
        emitters.add(new DbOverviewDotEmitter(outputDir, parameters, dotLinksPrefix, withSvgDiagrams,
                dotExecutable, usedByCollector));
        emitters.add(new DbStructureDotEmitter(outputDir, parameters, dotLinksPrefix, withSvgDiagrams,
                dotExecutable, usedByCollector));
        emitters.add(new SymbolCollaborationDotEmitter(outputDir, parameters, dotLinksPrefix, withSvgDiagrams,
                dotExecutable, usedByCollector));
        emitters.add(new PackageOverviewEmitter(outputDir, parameters, withSvgDiagrams, usedByCollector));
        emitters.add(new SymbolOverviewEmitter(outputDir, parameters, withSvgDiagrams, usedByCollector));
        emitters.add(new PackageEmitter(outputDir, parameters, withSvgDiagrams, usedByCollector));
        for (Emitter emitter : emitters)
            rootNode.emit(emitter);
    }

    private final static String OptionDoc = "doc";
    private final static String OptionSetDotLinksPrefix = "setDotLinksPrefix";
    private final static String OptionWithSvgDiagrams = "withSvgDiagrams";
    private final static String OptionWithoutSvgDiagrams = "withoutSvgDiagrams";
    private final static String OptionSetDotExecutable = "setDotExecutable";

    private final static String DefaultDotExecutable = "dot";
}
