package zserio.emit.doc;

import java.io.File;
import java.util.Map;
import java.util.Set;

import zserio.ast.AstNode;
import zserio.ast.PackageName;
import zserio.ast.Root;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

/**
 * Emits symbol collaboration diagrams in DOT format per each Zserio symbol.
 */
class SymbolCollaborationDotEmitter extends DotDefaultEmitter
{
    public SymbolCollaborationDotEmitter(String outputPathName, Parameters extensionParameters,
            String dotLinksPrefix, boolean withSvgDiagrams, String dotExecutable,
            UsedByCollector usedByCollector)
    {
        // TODO[mikir] to re-think dotLinksPrefix, it won't work
        super(outputPathName, extensionParameters, (dotLinksPrefix == null) ? "../.." : dotLinksPrefix,
                withSvgDiagrams, dotExecutable, usedByCollector);

        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(), getPackageMapper(),
                getResourceManager(), getDotLinksPrefix() + File.separator + HTML_CONTENT_DIRECTORY, ".");
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        emitDotDiagrams();
    }

    public static boolean svgCollaborationFileExists(AstNode node, UsedByCollector usedByCollector,
            boolean withSvgDiagrams)
    {
        return withSvgDiagrams && usedByCollector.getUsedByTypeMap().containsKey(node);
    }

    public static String getSvgCollaborationHtmlLink(AstNode node, PackageMapper packageMapper,
            String symbolCollaborationDirectory)
    {
        return getCollaborationHtmlLinkBase(node, packageMapper, symbolCollaborationDirectory) +
                SVG_FILE_EXTENSION;
    }

    private static String getDotCollaborationHtmlLink(AstNode node, PackageMapper packageMapper,
            String symbolCollaborationDirectory)
    {
        return getCollaborationHtmlLinkBase(node, packageMapper, symbolCollaborationDirectory) +
                DOT_FILE_EXTENSION;
    }

    private static String getCollaborationHtmlLinkBase(AstNode node, PackageMapper packageMapper,
            String symbolCollaborationDirectory)
    {
        final PackageName packageName = AstNodePackageNameMapper.getPackageName(node, packageMapper);
        final String typeName = AstNodeTypeNameMapper.getTypeName(node);
        final String name = AstNodeNameMapper.getName(node);

        final String packageNameString = (packageName == null) ? "" :
            ((packageName.isEmpty()) ? DEFAULT_PACKAGE_FILE_NAME : packageName.toString());

        return StringJoinUtil.joinStrings(symbolCollaborationDirectory, packageNameString,
                typeName + "_" + name, File.separator);
    }

    private void emitDotDiagrams() throws ZserioEmitException
    {
        final UsedByCollector usedByCollector = getUsedByCollector();
        for (Map.Entry<AstNode, Set<AstNode>> entry : usedByCollector.getUsedByTypeMap().entrySet())
        {
            final AstNode node = entry.getKey();

            final SymbolCollaborationDotTemplateData templateData = new SymbolCollaborationDotTemplateData(
                    context, node, usedByCollector.getUsedTypes(node), entry.getValue());
            final String dotCollaborationHtmlLink = getDotCollaborationHtmlLink(node, getPackageMapper(),
                    SYMBOL_COLLABORATION_DIRECTORY);
            final File outputDotFile = new File(getOutputPathName(), dotCollaborationHtmlLink);
            final String svgCollaborationHtmlLink = getSvgCollaborationHtmlLink(node, getPackageMapper(),
                    SYMBOL_COLLABORATION_DIRECTORY);
            final File outputSvgFile = new File(getOutputPathName(), svgCollaborationHtmlLink);
            processDotTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile, outputSvgFile);
        }
    }

    private static final String TEMPLATE_SOURCE_NAME = "symbol_collaboration.dot.ftl";

    private final TemplateDataContext context;
}
