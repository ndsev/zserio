package zserio.extension.xml;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.ast.Root;
import zserio.extension.common.FileUtil;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;

public class XmlExtension implements Extension
{
    @Override
    public String getName()
    {
        return "XML Generator";
    }

    @Override
    public String getExtensionVersion()
    {
        return XmlExtensionVersion.XML_EXTENSION_VERSION_STRING;
    }

    @Override
    public String getZserioVersion()
    {
        return XmlExtensionVersion.EXPECTED_ZSERIO_VERSION_STRING;
    }

    @Override
    public void registerOptions(Options options)
    {
        final Option option = new Option(OptionXml, true, "generate XML Abstract Syntax Tree");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters)
    {
        return parameters.argumentExists(OptionXml);
    }

    @Override
    public void check(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {}

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException
    {
        final OutputFileManager outputFileManager = new OutputFileManager(parameters);
        final String outputDir = parameters.getCommandLineArg(OptionXml);

        final File outputFile = new File(outputDir, "abstract_syntax_tree.xml");
        FileUtil.createOutputDirectory(outputFile);

        final XmlAstWriter xmlAstWriter = new XmlAstWriter();
        rootNode.accept(xmlAstWriter);
        xmlAstWriter.save(outputFile);
        outputFileManager.registerOutputFile(outputFile);

        outputFileManager.printReport();
    }

    private final static String OptionXml = "xml";
}
