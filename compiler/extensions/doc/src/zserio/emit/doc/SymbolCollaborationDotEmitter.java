package zserio.emit.doc;

import java.io.File;
import java.util.Set;

import zserio.ast.AstNode;
import zserio.ast.Package;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

/**
 * Emits symbol collaboration diagrams in DOT format per each Zserio symbol.
 */
class SymbolCollaborationDotEmitter
{
    public static void emit(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            String dotExecutable, UsedByCollector usedByCollector, PackageCollector packageCollector)
                    throws ZserioEmitException
    {
        final String htmlRootDirectory = ".." + File.separator + ".." + File.separator + "..";
        final TemplateDataContext context = new TemplateDataContext(outputPathName, extensionParameters,
                withSvgDiagrams, usedByCollector, packageCollector, htmlRootDirectory);
        final Set<AstNode> collaboratingNodes = usedByCollector.getCollaboratingNodes();
        for (AstNode node : collaboratingNodes)
            emitDotDiagram(outputPathName, withSvgDiagrams, dotExecutable, context, node);
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

    private static void emitDotDiagram(String outputPathName, boolean withSvgDiagrams, String dotExecutable,
            TemplateDataContext context, AstNode node) throws ZserioEmitException
    {
        final UsedByCollector usedByCollector = context.getUsedByCollector();
        final SymbolCollaborationDotTemplateData templateData = new SymbolCollaborationDotTemplateData(context,
                node, usedByCollector.getUsedTypes(node), usedByCollector.getUsedByTypes(node));
        final String dotHtmlLink = getDotSymbolCollaborationHtmlLink(node,
                DocDirectories.SYMBOL_COLLABORATION_DIRECTORY);
        final File outputDotFile = new File(outputPathName, dotHtmlLink);
        final String svgHtmlLink = getSvgSymbolCollaborationHtmlLink(node,
                DocDirectories.SYMBOL_COLLABORATION_DIRECTORY);
        final File outputSvgFile = new File(outputPathName, svgHtmlLink);
        DocFreeMarkerUtil.processTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile);
        if (withSvgDiagrams)
            DotToSvgConverter.convert(dotExecutable, outputDotFile, outputSvgFile);
    }

    private static final String TEMPLATE_SOURCE_NAME = "symbol_collaboration.dot.ftl";

    private static final String DOT_FILE_EXTENSION = ".dot";
    private static final String SVG_FILE_EXTENSION = ".svg";
}
