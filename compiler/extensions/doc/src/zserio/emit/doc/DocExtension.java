package zserio.emit.doc;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Extension;
import zserio.tools.Parameters;

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
    public boolean isEnabled(Parameters parameters)
    {
        return parameters.argumentExists(OptionDoc);
    }

    @Override
    public void generate(Parameters parameters, Root rootNode) throws ZserioEmitException
    {
        String dotLinksPrefix = null;
        if (parameters.argumentExists(OptionSetDotLinksPrefix))
            dotLinksPrefix = parameters.getCommandLineArg(OptionSetDotLinksPrefix);

        final boolean withSvgDiagrams = parameters.argumentExists(OptionWithSvgDiagrams);

        String dotExecutable = DefaultDotExecutable;
        if (parameters.argumentExists(OptionSetDotExecutable))
            dotExecutable = parameters.getCommandLineArg(OptionSetDotExecutable);

        if (withSvgDiagrams && !DotFileConvertor.isDotExecAvailable(dotExecutable))
            throw new ZserioEmitException("The dot executable '" + dotExecutable + "' not found!");

        final String docPath = parameters.getCommandLineArg(OptionDoc);

        // collect used by information
        final UsedByCollector usedByCollector = new UsedByCollector();
        rootNode.emit(usedByCollector);

        // emit DB overview dot file
        DbOverviewDotEmitter dbOverviewDotEmitter = new DbOverviewDotEmitter(docPath, dotLinksPrefix,
                                                        withSvgDiagrams, dotExecutable);
        rootNode.emit(dbOverviewDotEmitter);

        // emit DB structure dot files
        DbStructureDotEmitter dbStructureDotEmitter = new DbStructureDotEmitter(docPath, dotLinksPrefix,
                                                          withSvgDiagrams, dotExecutable);
        rootNode.emit(dbStructureDotEmitter);

        // emit type collaboration diagram files (must be before HTML documentation)
        TypeCollaborationDotEmitter typeCollaborationDotEmitter = new TypeCollaborationDotEmitter(docPath,
                dotLinksPrefix, withSvgDiagrams, dotExecutable, usedByCollector);
        rootNode.emit(typeCollaborationDotEmitter);

        // emit frameset
        ContentEmitter docEmitter = new ContentEmitter(docPath, withSvgDiagrams, usedByCollector);
        docEmitter.emitFrameset();

        // emit stylesheets
        docEmitter.emitStylesheet();

        // emit HTML documentation
        rootNode.emit(docEmitter);

        // emit list of packages
        PackageEmitter packageEmitter = new PackageEmitter(docPath);
        rootNode.emit(packageEmitter);

        // emit list of classes
        OverviewEmitter overviewEmitter = new OverviewEmitter(docPath);
        rootNode.emit(overviewEmitter);

        // emit list of deprecated elements
        DeprecatedEmitter deprecatedEmitter = new DeprecatedEmitter(docPath);
        rootNode.emit(deprecatedEmitter);
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

    private final static String OptionDoc = "doc";
    private final static String OptionSetDotLinksPrefix = "setDotLinksPrefix";
    private final static String OptionWithSvgDiagrams = "withSvgDiagrams";
    private final static String OptionWithoutSvgDiagrams = "withoutSvgDiagrams";
    private final static String OptionSetDotExecutable = "setDotExecutable";

    private final static String DefaultDotExecutable = "dot";
}
