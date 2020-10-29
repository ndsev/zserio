package zserio.emit.doc;

import java.io.File;

import zserio.ast.Package;
import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

/**
 * Emits index HTML file.
 */
class IndexEmitter extends HtmlDefaultEmitter
{
    public IndexEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector);
        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(), getResourceManager(),
                "content", SYMBOL_COLLABORATION_DIRECTORY);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final SymbolTemplateData templateData = SymbolTemplateDataCreator.createData(context, rootPackage);
        final File outputFile = new File(getOutputPathName(), INDEX_FILE_NAME);
        processHtmlTemplate(INDEX_TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    @Override
    public void beginPackage(Package pkg)
    {
        if (rootPackage == null)
            rootPackage = pkg;
    }

    private final TemplateDataContext context;
    private Package rootPackage = null;
    private static final String INDEX_FILE_NAME = "index.html";
    private static final String INDEX_TEMPLATE_SOURCE_NAME = "index.html.ftl";
}
