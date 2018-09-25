package zserio.emit.doc;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import antlr.RecognitionException;

import zserio.antlr.ZserioEmitter;
import zserio.ast.TokenAST;
import zserio.tools.Extension;
import zserio.tools.Parameters;

public class DocExtension implements Extension
{
    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return "Doc Generator";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion()
    {
        return DocExtensionVersion.VERSION_STRING;
    }

    /* (non-Javadoc)
     * @see zserio.tools.Extension#generate(zserio.antlr.ZserioEmitter, zserio.ast.TokenAST)
     */
    @Override
    public void generate(Parameters parameters, ZserioEmitter emitter, TokenAST rootNode) throws ZserioEmitDocException
    {
        if (parameters == null)
        {
            throw new ZserioEmitDocException("No parameters set for HtmlBackend!");
        }

        if (!parameters.argumentExists(OptionDoc))
        {
            System.out.println("Emitting HTML documentation is disabled");
            return;
        }

        System.out.println("Emitting HTML documentation");

        String dotLinksPrefix = null;
        if (parameters.argumentExists(OptionSetDotLinksPrefix))
            dotLinksPrefix = parameters.getCommandLineArg(OptionSetDotLinksPrefix);

        final boolean withSvgDiagrams = parameters.argumentExists(OptionWithSvgDiagrams);

        String dotExecutable = DefaultDotExecutable;
        if (parameters.argumentExists(OptionSetDotExecutable))
            dotExecutable = parameters.getCommandLineArg(OptionSetDotExecutable);

        if (withSvgDiagrams && !DotFileConvertor.isDotExecAvailable(dotExecutable))
            throw new ZserioEmitDocException("The dot executable '" + dotExecutable + "' not found!");

        final String docPath = parameters.getCommandLineArg(OptionDoc);

        try
        {
            // emit DB overview dot file
            DbOverviewDotEmitter dbOverviewDotEmitter = new DbOverviewDotEmitter(docPath, dotLinksPrefix,
                                                            withSvgDiagrams, dotExecutable);
            emitter.setEmitter(dbOverviewDotEmitter);
            emitter.root(rootNode);

            // emit DB structure dot files
            DbStructureDotEmitter dbStructureDotEmitter = new DbStructureDotEmitter(docPath, dotLinksPrefix,
                                                              withSvgDiagrams, dotExecutable);
            emitter.setEmitter(dbStructureDotEmitter);
            emitter.root(rootNode);

            // emit type collaboration diagram files (must be before HTML documentation)
            TypeCollaborationDotEmitter typeCollaborationDotEmitter = new TypeCollaborationDotEmitter(docPath,
                                                            dotLinksPrefix, withSvgDiagrams, dotExecutable);
            emitter.setEmitter(typeCollaborationDotEmitter);
            emitter.root(rootNode);

            // emit frameset
            ContentEmitter docEmitter = new ContentEmitter(docPath, withSvgDiagrams);
            docEmitter.emitFrameset();

            // emit stylesheets
            docEmitter.emitStylesheet();

            // emit HTML documentation
            emitter.setEmitter(docEmitter);
            emitter.root(rootNode);

            // emit list of packages
            PackageEmitter packageEmitter = new PackageEmitter(docPath);
            emitter.setEmitter(packageEmitter);
            emitter.root(rootNode);

            // emit list of classes
            OverviewEmitter overviewEmitter = new OverviewEmitter(docPath);
            emitter.setEmitter(overviewEmitter);
            emitter.root(rootNode);

            // emit list of deprecated elements
            DeprecatedEmitter deprecatedEmitter = new DeprecatedEmitter(docPath);
            emitter.setEmitter(deprecatedEmitter);
            emitter.root(rootNode);
        }
        catch (RecognitionException exc)
        {
            System.out.println("DocExtension.generate: exception:" + exc);
            throw new ZserioEmitDocException(exc);
        }
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
