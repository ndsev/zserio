package zserio.emit.doc;

import java.io.File;

import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

/**
 * Emits index HTML file.
 */
class IndexEmitter extends HtmlDefaultEmitter
{
    public IndexEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector, PackageCollector packageCollector)
    {
        super();

        this.outputPathName = outputPathName;

        final String htmlRootDirectory = ".";
        context = new TemplateDataContext(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector,
                packageCollector, htmlRootDirectory);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final SymbolTemplateData templateData = SymbolTemplateDataCreator.createData(context,
                root.getRootPackage());
        final File outputFile = new File(outputPathName, INDEX_FILE_NAME);
        processHtmlTemplate(INDEX_TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    private static final String INDEX_FILE_NAME = "index.html";
    private static final String INDEX_TEMPLATE_SOURCE_NAME = "index.html.ftl";

    private final String outputPathName;
    private final TemplateDataContext context;
}
