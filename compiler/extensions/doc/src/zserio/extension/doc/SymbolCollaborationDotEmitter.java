package zserio.extension.doc;


import java.io.File;
import java.util.Set;

import zserio.ast.AstNode;
import zserio.ast.Package;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.StringJoinUtil;

/**
 * Symbol collaboration dot emitter.
 *
 * Symbol collaboration dot emitter creates symbol collaboration diagrams in DOT format for each Zserio symbol.
 */
class SymbolCollaborationDotEmitter
{
    public static void emit(OutputFileManager outputFileManager, DocExtensionParameters docParameters,
            UsedByCollector usedByCollector) throws ZserioExtensionException
    {
        final String htmlRootDirectory = ".." + File.separator + ".." + File.separator + "..";
        final TemplateDataContext context = new TemplateDataContext(docParameters, htmlRootDirectory);
        final Set<AstNode> collaboratingNodes = usedByCollector.getCollaboratingNodes();
        for (AstNode node : collaboratingNodes)
            emitDotDiagram(outputFileManager, docParameters, usedByCollector, context, node);
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
        final Package pkg = AstNodePackageMapper.getPackage(node);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final String name = AstNodeNameMapper.getName(node);
        final String packageFileName = PackageFileNameMapper.getFileName(pkg);

        return StringJoinUtil.joinStrings(symbolCollaborationDirectory, packageFileName,
                typeName + "_" + name, File.separator);
    }

    private static void emitDotDiagram(OutputFileManager outputFileManager,
            DocExtensionParameters docParameters, UsedByCollector usedByCollector, TemplateDataContext context,
            AstNode node) throws ZserioExtensionException
    {
        final SymbolCollaborationDotTemplateData templateData = new SymbolCollaborationDotTemplateData(context,
                node, usedByCollector.getUsedTypes(node), usedByCollector.getUsedByTypes(node));
        final String dotHtmlLink = getDotSymbolCollaborationHtmlLink(node,
                DocDirectories.SYMBOL_COLLABORATION_DIRECTORY);
        final String outputDir = docParameters.getOutputDir();

        final File outputDotFile = new File(outputDir, dotHtmlLink);
        DocFreeMarkerUtil.processTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile);
        outputFileManager.registerOutputFile(outputDotFile);

        if (docParameters.getWithSvgDiagrams())
        {
            final String svgHtmlLink = getSvgSymbolCollaborationHtmlLink(node,
                    DocDirectories.SYMBOL_COLLABORATION_DIRECTORY);
            final File outputSvgFile = new File(outputDir, svgHtmlLink);
            DotToSvgConverter.convert(docParameters.getDotExecutable(), outputDotFile, outputSvgFile);
            outputFileManager.registerOutputFile(outputSvgFile);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "symbol_collaboration.dot.ftl";

    private static final String DOT_FILE_EXTENSION = ".dot";
    private static final String SVG_FILE_EXTENSION = ".svg";
}
