package zserio.extension.json;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import zserio.ast.Root;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.Extension;
import zserio.tools.ExtensionParameters;

/**
 * @Author: kancve
 * @Date: 2022/9/10
 * @Description: JsonExtension
 */
public class JsonExtension implements Extension {
    @Override
    public String getName() {
        return "JSON Generator";
    }

    @Override
    public String getVersion() {
        return JsonExtensionVersion.VERSION_STRING;
    }

    @Override
    public void registerOptions(Options options) {
        final Option option = new Option(OptionJson, true, "generate JSON Abstract Syntax Tree");
        option.setArgName("outputDir");
        option.setRequired(false);
        options.addOption(option);
    }

    @Override
    public boolean isEnabled(ExtensionParameters parameters) {
        return parameters.argumentExists(OptionJson);
    }

    @Override
    public void check(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException {
    }

    @Override
    public void process(Root rootNode, ExtensionParameters parameters) throws ZserioExtensionException {
        final OutputFileManager outputFileManager = new OutputFileManager(parameters);
        final String outputDir = parameters.getCommandLineArg(OptionJson);

        final JsonAstWriter JsonAstWriter = new JsonAstWriter(outputFileManager, outputDir);
        rootNode.accept(JsonAstWriter);

        JsonAstWriter.save();
        outputFileManager.printReport();
    }

    private final static String OptionJson = "json";
}
