package zserio.extension.doc;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class DocExtensionParameters
{
    public DocExtensionParameters(ExtensionParameters parameters) throws ZserioExtensionException
    {
        pathName = parameters.getPathName();
        outputDir = parameters.getCommandLineArg(OptionDoc);
        withSvgDiagrams = parameters.argumentExists(OptionWithSvgDiagrams);
        dotExecutable = (parameters.argumentExists(OptionSetDotExecutable)) ?
                parameters.getCommandLineArg(OptionSetDotExecutable) : DefaultDotExecutable;

        if (withSvgDiagrams && !DotToSvgConverter.isDotExecAvailable(dotExecutable))
            throw new ZserioExtensionException("The dot executable '" + dotExecutable + "' not found!");
    }

    public String getPathName()
    {
        return pathName;
    }

    public String getOutputDir()
    {
        return outputDir;
    }

    public boolean getWithSvgDiagrams()
    {
        return withSvgDiagrams;
    }

    public String getDotExecutable()
    {
        return dotExecutable;
    }

    static void registerOptions(org.apache.commons.cli.Options options)
    {
        Option option = new Option(OptionDoc, true, "generate HTML documentation");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);

        final OptionGroup svgDiagramsGroup = new OptionGroup();
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

    static boolean hasOptionDoc(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionDoc);
    }

    private static final String OptionDoc = "doc";
    private static final String OptionWithSvgDiagrams = "withSvgDiagrams";
    private static final String OptionWithoutSvgDiagrams = "withoutSvgDiagrams";
    private static final String OptionSetDotExecutable = "setDotExecutable";

    private static final String DefaultDotExecutable = "dot";

    private final String pathName;
    private final String outputDir;
    private final boolean withSvgDiagrams;
    private final String dotExecutable;
}
