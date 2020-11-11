package zserio.emit.doc;

import java.io.File;
import java.util.Set;

import zserio.ast.AstNode;
import zserio.ast.Package;
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
            boolean withSvgDiagrams, String dotExecutable, UsedByCollector usedByCollector,
            Package rootPackage)
    {
        super(withSvgDiagrams, dotExecutable);

        this.outputPathName = outputPathName;

        final String htmlRootDirectory = ".." + File.separator + ".." + File.separator + "..";
        context = new TemplateDataContext(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector,
                rootPackage, htmlRootDirectory);
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
        final Package pkg = AstNodePackageMapper.getPackage(node);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final String name = AstNodeNameMapper.getName(node);
        final String packageFileName = PackageFileNameMapper.getFileName(pkg);

        return StringJoinUtil.joinStrings(symbolCollaborationDirectory, packageFileName,
                typeName + "_" + name, File.separator);
    }

    private void emitDotDiagrams() throws ZserioEmitException
    {
        final Set<AstNode> collaboratingNodes = context.getUsedByCollector().getCollaboratingNodes();
        for (AstNode node : collaboratingNodes)
            emitDotDiagram(node);
    }

    private void emitDotDiagram(AstNode node) throws ZserioEmitException
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
        processDotTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile, outputSvgFile);
    }

    private static final String TEMPLATE_SOURCE_NAME = "symbol_collaboration.dot.ftl";

    private final String outputPathName;
    private final TemplateDataContext context;
}
