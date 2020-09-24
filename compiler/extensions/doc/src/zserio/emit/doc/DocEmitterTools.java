package zserio.emit.doc;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BuiltInType;
import zserio.ast.Constant;
import zserio.ast.PackageName;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.StringJoinUtil;

/**
 * Common public static methods used for documentation emitter.
 */
class DocEmitterTools
{
    /**
     * Returns the directory name where to store the HTML file.
     *
     * @param type The ZserioType from which to generate the directory name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getDirectoryNameFromType(AstNode type) throws ZserioEmitException
    {
        return getZserioPackageName(type).toString();
    }

    /**
     * Returns the HTML file name.
     *
     * @param type The ZserioType from which to generate the HTML file name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getHtmlFileNameFromType(AstNode type) throws ZserioEmitException
    {
        return getFileNameFromType(type, ".html");
    }

    /**
     * Returns the URL name of HTML file.
     *
     * @param type The ZserioType from which to generate the URL name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getUrlNameFromType(AstNode type) throws ZserioEmitException
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
    public static String getUrlNameFromTypeAndFieldName(AstNode type, String fieldName)
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
    public static String getDocUrlFromType(String docRootPath, AstNode type) throws ZserioEmitException
    {
        if (docRootPath == null)
            return null;

        return StringJoinUtil.joinStrings(docRootPath, docDirectory,
                DocEmitterTools.getZserioPackageName(type) + ".html#" +
                        (new LinkedType(type).getHyperlinkName()),
                URLDirSeparator);
    }

    // TODO[mikir] Should be removed and replaced by getSvgUrl call.
    public static String getTypeCollaborationSvgUrl(String docRootPath, AstNode type)
            throws ZserioEmitException
    {
        return TypeCollaborationDotEmitter.getSvgUrl(docRootPath, type);
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

    /**
     * Common package name getter for Zserio types and symbols.
     *
     * @param node AST node which represents either Zserio type or global symbol.
     *
     * @return Zserio package name.
     */
    public static PackageName getZserioPackageName(AstNode node) throws ZserioEmitException
    {
        if (node instanceof TypeInstantiation)
        {
            if (node instanceof ArrayInstantiation)
                node = ((ArrayInstantiation)node).getElementTypeInstantiation();
            node = ((TypeInstantiation)node).getType();
        }

        if (node instanceof ZserioType)
        {
            if (((ZserioType)node) instanceof BuiltInType)
            {
                return (new PackageName.Builder()).get();
            }
            return ((ZserioType)node).getPackage().getPackageName();
        }
        if (node instanceof Constant)
        {
            return ((Constant)node).getPackage().getPackageName();
        }
        throw new ZserioEmitException("Unhanled Zserio type or symbol '" + node.getClass().getName() + "'!");
    }

    /**
     * Common name getter for Zserio types and symbols.
     *
     * @param node AST node which represents either Zserio type or global symbol.
     *
     * @return Zserio name.
     */
    public static String getZserioName(AstNode node) throws ZserioEmitException
    {
        if (node instanceof ZserioType)
        {
            return ((ZserioType)node).getName();
        }
        if (node instanceof Constant)
        {
            return ((Constant)node).getName();
        }
        throw new ZserioEmitException("Unhanled Zserio type or symbol '" + node.getClass().getName() + "'!");
    }

    public static String getFileNameFromType(AstNode type, String extensionName) throws ZserioEmitException
    {
        HtmlModuleNameSuffixVisitor suffixVisitor = new HtmlModuleNameSuffixVisitor();
        type.accept(suffixVisitor);

        return getZserioName(type) + "_" + suffixVisitor.getSuffix() + extensionName;
    }

    private static final String docDirectory = "content";
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
