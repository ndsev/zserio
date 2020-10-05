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
     * Returns the URL name of HTML file.
     *
     * @param type The ZserioType from which to generate the URL name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getUrlNameFromType(AstNode type) throws ZserioEmitException
    {
        return DocEmitterTools.getZserioPackageName(type) + ".html";
    }

    /**
     * Get anchor name for the given Zserio type.
     *
     * @param type Zserio type.
     * @return Anchor name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getAnchorName(AstNode type) throws ZserioEmitException
    {
        return new LinkedType(type).getHyperlinkName();
    }

    /**
     * Gets anchor name for the given Zserio type and it's member name.
     *
     * @param type Zserio type.
     * @param name Member name - e.g. fieldName, bitmaskValue, etc.
     *
     * @return Anchor name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getAnchorName(AstNode type, String memberName) throws ZserioEmitException
    {
        return StringJoinUtil.joinStrings(getAnchorName(type), memberName, "_");
    }

    /**
     * Gets anchor name for the given Zserio type and it's member name with custom prefix.
     *
     * @param type Zserio type.
     * @param prefix Prefix to use in anchor name.
     * @param name Member name - e.g. fieldName, bitmaskValue, etc.
     *
     * @return Anchor name.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public static String getAnchorName(AstNode type, String prefix, String memberName)
            throws ZserioEmitException
    {
        return StringJoinUtil.joinStrings(getAnchorName(type), prefix, memberName, "_");
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
