package zserio.emit.xml;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.ast.Root;
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
        final Option option = new Option(OptionXml, true, "generate XML Syntax Tree");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    @Override
    public void generate(Parameters parameters, Root rootNode)
    {
        if (!parameters.argumentExists(OptionXml))
        {
            System.out.println("Emitting XML Syntax Tree is disabled");
            return;
        }

        System.out.println("Emitting XML Syntax Tree");
        final String outputDir = parameters.getCommandLineArg(OptionXml);
        final SyntaxTreeEmitter syntaxTreeEmitter = new SyntaxTreeEmitter(outputDir);
        syntaxTreeEmitter.emit(rootNode);
    }

    private final static String OptionXml = "xml";
}
