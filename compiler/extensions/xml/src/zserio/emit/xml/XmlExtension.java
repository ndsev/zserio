package zserio.emit.xml;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.antlr.ZserioEmitter;
import zserio.ast.TokenAST;
import zserio.tools.Extension;
import zserio.tools.Parameters;

public class XmlExtension implements Extension
{
    /** {@inheritDoc} */
    @Override
    public String getName()
    {
        return "XML Generator";
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion()
    {
        return XmlExtensionVersion.VERSION_STRING;
    }

    /** {@inheritDoc} */
    @Override
    public void registerOptions(Options options)
    {
        Option option = new Option(OptionXml, true,
                "generate XML Syntax Tree");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    /** {@inheritDoc} */
    @Override
    public void generate(Parameters parameters, ZserioEmitter emitter, TokenAST rootNode)
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
