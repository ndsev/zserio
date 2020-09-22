package zserio.emit.doc;

import java.io.File;
import java.util.Map;
import java.util.Set;

import zserio.ast.AstNode;
import zserio.ast.Root;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

/**
 * Emits type collaboration diagrams in DOT format per each Zserio type.
 */
public class TypeCollaborationDotEmitter extends DotDefaultEmitter
{
    public TypeCollaborationDotEmitter(String outputPathName, Parameters extensionParameters,
            String dotLinksPrefix, boolean withSvgDiagrams, String dotExecutable,
            UsedByCollector usedByCollector)
    {
        // TODO[mikir] to re-think dotLinksPrefix, it won't work
        super(extensionParameters, (dotLinksPrefix == null) ? "../.." : dotLinksPrefix, withSvgDiagrams,
                dotExecutable, usedByCollector);

        this.outputPathName = outputPathName;
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        emitDotDiagrams();
    }

    public static String getSvgUrl(String docRootPath, AstNode type) throws ZserioEmitException
    {
        final String svgFileNameBase = StringJoinUtil.joinStrings(
                TYPE_COLLABORATION_DOT_DIRECTORY, DocEmitterTools.getDirectoryNameFromType(type),
                DocEmitterTools.getFileNameFromType(type, SVG_FILE_EXTENSION), URL_DIR_SEPARATOR);
        final String svgFileName = StringJoinUtil.joinStrings(docRootPath, svgFileNameBase, URL_DIR_SEPARATOR);
        final File svgFile = new File(svgFileName);

        return (svgFile.exists()) ? StringJoinUtil.joinStrings("..", svgFileNameBase, URL_DIR_SEPARATOR) : null;
    }

    private void emitDotDiagrams() throws ZserioEmitException
    {
        final UsedByCollector usedByCollector = getUsedByCollector();
        for (Map.Entry< AstNode, Set<AstNode> > entry : usedByCollector.getUsedByTypeMap().entrySet())
        {
            final AstNode type = entry.getKey();
            final TypeCollaborationDotTemplateData templateData = new TypeCollaborationDotTemplateData(type,
                    usedByCollector.getUsedTypes(type), entry.getValue(), getDotLinksPrefix());
            final File outputDotFile = new File(
                    StringJoinUtil.joinStrings(outputPathName, TYPE_COLLABORATION_DOT_DIRECTORY,
                    DocEmitterTools.getDirectoryNameFromType(type),
                    DocEmitterTools.getFileNameFromType(type, DOT_FILE_EXTENSION), File.separator));
            final File outputSvgFile = new File(
                    StringJoinUtil.joinStrings(outputPathName, TYPE_COLLABORATION_DOT_DIRECTORY,
                    DocEmitterTools.getDirectoryNameFromType(type),
                    DocEmitterTools.getFileNameFromType(type, SVG_FILE_EXTENSION), File.separator));
            processDotTemplate(TEMPLATE_SOURCE_NAME, templateData, outputDotFile, outputSvgFile);
        }
    }

    private static final String TYPE_COLLABORATION_DOT_DIRECTORY = "type_collaboration";

    private static final String TEMPLATE_SOURCE_NAME = "type_collaboration.dot.ftl";
    private static final String URL_DIR_SEPARATOR = "/";

    private final String outputPathName;
}
