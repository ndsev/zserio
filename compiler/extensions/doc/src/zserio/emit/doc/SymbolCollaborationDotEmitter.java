package zserio.emit.doc;

import java.io.File;

import zserio.ast.AstNode;
import zserio.ast.PackageName;
import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

/**
 * Emits symbol collaboration diagrams in DOT format per each Zserio symbol.
 */
class SymbolCollaborationDotEmitter extends DotDefaultEmitter
{
    public SymbolCollaborationDotEmitter(String outputPathName, Parameters extensionParameters,
            boolean withSvgDiagrams, String dotExecutable, UsedByCollector usedByCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, dotExecutable, usedByCollector);

        final String directoryPrefix = ".." + File.separator + ".." + File.separator;
        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(), getResourceManager(),
                directoryPrefix + HTML_CONTENT_DIRECTORY, ".");
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        emitDotDiagrams();
    }

    public static boolean svgSymbolCollaborationDiagramExists(AstNode node, UsedByCollector usedByCollector,
            boolean withSvgDiagrams)
    {
        return withSvgDiagrams && usedByCollector.getCollaboratingNodes().contains(node);
    }

    public static String getSvgSymbolCollaborationHtmlLink(AstNode node, String symbolCollaborationDirectory)
    {
        return getSymbolCollaborationHtmlLinkBase(node, symbolCollaborationDirectory) + SVG_FILE_EXTENSION;
    }

    private static String getDotSymbolCollaborationHtmlLink(AstNode node, String symbolCollaborationDirectory)
    {
        return getSymbolCollaborationHtmlLinkBase(node, symbolCollaborationDirectory) + DOT_FILE_EXTENSION;
    }

    private static String getSymbolCollaborationHtmlLinkBase(AstNode node, String symbolCollaborationDirectory)
    {
        final PackageName packageName = AstNodePackageNameMapper.getPackageName(node);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final String name = AstNodeNameMapper.getName(node);

        final String packageNameString = (packageName == null) ? "" : packageName.toString();

        return StringJoinUtil.joinStrings(symbolCollaborationDirectory, packageNameString,
                typeName + "_" + name, File.separator);
    }

    private void emitDotDiagrams() throws ZserioEmitException
    {
        for (AstNode node : getUsedByCollector().getCollaboratingNodes())
            emitDotDiagram(node);
    }

    private void emitDotDiagram(AstNode node) throws ZserioEmitException
    {
        final SymbolCollaborationDotTemplateData templateData = new SymbolCollaborationDotTemplateData(context,
                node, getUsedByCollector().getUsedTypes(node), getUsedByCollector().getUsedByTypes(node));
        final String dotHtmlLink = getDotSymbolCollaborationHtmlLink(node, SYMBOL_COLLABORATION_DIRECTORY);
        final File outputDotFile = new File(getOutputPathName(), dotHtmlLink);
        final String svgHtmlLink = getSvgSymbolCollaborationHtmlLink(node, SYMBOL_COLLABORATION_DIRECTORY);
        final File outputSvgFile = new File(getOutputPathName(), svgHtmlLink);
        processDotTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile, outputSvgFile);
    }

    private static final String TEMPLATE_SOURCE_NAME = "symbol_collaboration.dot.ftl";

    private final TemplateDataContext context;
}
