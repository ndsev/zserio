package zserio.emit.doc;

import java.io.File;

import zserio.ast.ZserioType;
import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.StringJoinUtil;

/**
 * Common public static methods used for documentation emitter.
 */
public class DocEmitterTools
{
    /**
     * Returns the directory name where to store the HTML file.
     *
     * @param type The ZserioType from which to generate the directory name.
     */
    public static String getDirectoryNameFromType(ZserioType type)
    {
        return type.getPackage().getPackageName().toString();
    }

    /**
     * Returns the HTML file name.
     *
     * @param type The ZserioType from which to generate the HTML file name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getHtmlFileNameFromType(ZserioType type) throws ZserioEmitException
    {
        return getFileNameFromType(type, "html");
    }

    /**
     * Returns the DOT file name.
     *
     * @param type The ZserioType for which to generate the DOT file name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getDotFileNameFromType(ZserioType type) throws ZserioEmitException
    {
        return getFileNameFromType(type, "dot");
    }

    /**
     * Returns the SVG file name.
     *
     * @param type The ZserioType for which to generate the SVG file name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getSvgFileNameFromType(ZserioType type) throws ZserioEmitException
    {
        return getFileNameFromType(type, "svg");
    }

    /**
     * Returns the URL name of HTML file.
     *
     * @param type The ZserioType from which to generate the URL name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getUrlNameFromType(ZserioType type) throws ZserioEmitException
    {
        return ".." + URLDirSeparator + getDirectoryNameFromType(type) +
                URLDirSeparator + getHtmlFileNameFromType(type);
    }

    /**
     * Returns the URL name of HTML file.
     *
     * @param type      The ZserioType from which to generate the URL name.
     * @param fieldName The field name from which to generate the URL name or null if no field name is present.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getUrlNameFromTypeAndFieldName(ZserioType type, String fieldName)
            throws ZserioEmitException
    {
        String urlName = getUrlNameFromType(type);
        if (fieldName != null)
            urlName += "#" + fieldName;

        return urlName;
    }

    /**
     * Gets the URL to the documentation of the Zserio type.
     *
     * @param docRootPath The path to the root of the generated HTML documentation or null.
     * @param type        Zserio type for which to get the documentation URL.
     *
     * @return The string which represents the URL to the documentation or null if docRootPath was null.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getDocUrlFromType(String docRootPath, ZserioType type) throws ZserioEmitException
    {
        if (docRootPath == null)
            return null;

        return StringJoinUtil.joinStrings(docRootPath, docDirectory,
                                          DocEmitterTools.getDirectoryNameFromType(type),
                                          DocEmitterTools.getHtmlFileNameFromType(type), URLDirSeparator);
    }

    /**
     * Gets the name for database overview dot file.
     *
     * @param docRootPath The path to the root of the generated HTML documentation.
     *
     * @return The file which represents the dot file name for database overview.
     */
    public static File getDbOverviewDotFile(String docRootPath)
    {
        return new File(StringJoinUtil.joinStrings(docRootPath, dbOverviewDirectory,
                "overview.dot", File.separator));
    }

    /**
     * Gets the name for database overview svg file.
     *
     * @param docRootPath The path to the root of the generated HTML documentation.
     *
     * @return The file which represents the svg file name for database overview.
     */
    public static File getDbOverviewSvgFile(String docRootPath)
    {
        return new File(StringJoinUtil.joinStrings(docRootPath, dbOverviewDirectory,
                "overview.svg", File.separator));
    }

    /**
     * Gets the name for database structure dot file.
     *
     * @param docRootPath The path to the root of the generated HTML documentation.
     * @param type        Database type for which to get the database structure dot file name.
     *
     * @return The file which represents the dot file name for database structure.
     */
    public static File getDbStructureDotFile(String docRootPath, SqlDatabaseType type)
    {
        return new File(StringJoinUtil.joinStrings(docRootPath, dbStructureDirectory,
                type.getName() + ".dot", File.separator));
    }

    /**
     * Gets the name for database structure svg file.
     *
     * @param docRootPath The path to the root of the generated HTML documentation.
     * @param type        Database type for which to get the database structure svg file name.
     *
     * @return The file which represents the svg file name for database structure.
     */
    public static File getDbStructureSvgFile(String docRootPath, SqlDatabaseType type)
    {
        return new File(StringJoinUtil.joinStrings(docRootPath, dbStructureDirectory,
                type.getName() + ".svg", File.separator));
    }

    /**
     * Gets the name for type collaboration dot file.
     *
     * @param docRootPath The path to the root of the generated HTML documentation.
     * @param type        Zserio type for which to get the type collaboration dot file name.
     *
     * @return The file which represents the dot file name for type collaboration.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static File getTypeCollaborationDotFile(String docRootPath, ZserioType type)
            throws ZserioEmitException
    {
        return new File(StringJoinUtil.joinStrings(docRootPath, typeCollaborationDirectory,
                                          DocEmitterTools.getDirectoryNameFromType(type),
                                          DocEmitterTools.getDotFileNameFromType(type), File.separator));
    }

    /**
     * Gets the name for type collaboration svg file.
     *
     * @param docRootPath The path to the root of the generated HTML documentation.
     * @param type        Zserio type for which to get the type collaboration svg file name.
     *
     * @return The file which represents the svg file name for type collaboration.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static File getTypeCollaborationSvgFile(String docRootPath, ZserioType type)
            throws ZserioEmitException
    {
        return new File(StringJoinUtil.joinStrings(docRootPath, typeCollaborationDirectory,
                                          DocEmitterTools.getDirectoryNameFromType(type),
                                          DocEmitterTools.getSvgFileNameFromType(type), File.separator));
    }

    /**
     * Gets the URL for type collaboration svg file if exists.
     *
     * @param docRootPath The path to the root of the generated HTML documentation.
     * @param type        Zserio type for which to get the URL.
     *
     * @return The string which represents the URL for type collaboration svg file or null if not exists.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getTypeCollaborationSvgUrl(String docRootPath, ZserioType type)
            throws ZserioEmitException
    {
        final String svgFileNameBase = StringJoinUtil.joinStrings(typeCollaborationDirectory,
                                          DocEmitterTools.getDirectoryNameFromType(type),
                                          DocEmitterTools.getSvgFileNameFromType(type), URLDirSeparator);
        final String svgFileName = StringJoinUtil.joinStrings(docRootPath, svgFileNameBase, URLDirSeparator);
        final File svgFile = new File(svgFileName);

        return (svgFile.exists()) ? StringJoinUtil.joinStrings("..", "..", svgFileNameBase, URLDirSeparator)
                                  : null;
    }

    /**
     * Gets the database color.
     *
     * This is tricky and hard-coded. Should be done properly by external configuration.
     *
     * @param databaseIndex Index of the database calculated from its occurrence in the zserio.
     *
     * @return The string which represents the database color.
     */
    public static String getDatabaseColor(int databaseIndex)
    {
        return databaseColorList[databaseIndex % databaseColorList.length];
    }

    private static String getFileNameFromType(ZserioType type, String extensionName) throws ZserioEmitException
    {
        HtmlModuleNameSuffixVisitor suffixVisitor = new HtmlModuleNameSuffixVisitor();
        type.accept(suffixVisitor);

        return type.getName() + "_" + suffixVisitor.getSuffix() + "." + extensionName;
    }

    private static final String docDirectory = "content";
    private static final String dbOverviewDirectory = "db_overview";
    private static final String dbStructureDirectory = "db_structure";
    private static final String typeCollaborationDirectory = "type_collaboration";
    private static final String URLDirSeparator = "/";

    private static final String[] databaseColorList = new String[]
    {
        "#E60003",
        "#00679C",
        "#AE080F",
        "#779A14",
        "#450B3F",
        "#537374",
        "#E74294",
        "#E8A900",
        "#0068B4",
        "#905A4A",
        "#1C95D4",
        "#AC7900",
        "#EA4F00",
        "#84715F",
        "#3E3837",
        "#6A1385",
        "#083788"
    };
}
