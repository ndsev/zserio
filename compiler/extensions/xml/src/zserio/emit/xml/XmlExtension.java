package zserio.emit.xml;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.ast.Root;
import zserio.emit.common.FileUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Extension;
import zserio.tools.Parameters;

public class XmlExtension implements Extension
{
    @Override
    public String getName()
    {
        return "XML Generator";
    }

    @Override
    public String getVersion()
    {
        return XmlExtensionVersion.VERSION_STRING;
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
    public boolean isEnabled(Parameters parameters)
    {
        return parameters.argumentExists(OptionXml);
    }

    @Override
    public void generate(Parameters parameters, Root rootNode) throws ZserioEmitException
    {
        final String outputDir = parameters.getCommandLineArg(OptionXml);

        final File outputFile = new File(outputDir, "abstract_syntax_tree.xml");
        FileUtil.createOutputDirectory(outputFile);

        final XmlAstWriter xmlAstWriter = new XmlAstWriter();
        rootNode.accept(xmlAstWriter);
        xmlAstWriter.save(outputFile);
    }

    private final static String OptionXml = "xml";
}
