package zserio.ast;

import java.util.List;

import zserio.tools.ZserioVersion;

/**
 * AST node for compatibility version.
 */
public final class CompatibilityVersion extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param versionStringLiteral Version string literal (including quotes!).
     * @param docComments List of documentation comments belonging to this node.
     */
    public CompatibilityVersion(AstLocation location, String versionStringLiteral, List<DocComment> docComments)
    {
        super(location, docComments);

        try
        {
            this.version = ZserioVersion.parseVersion(
                    versionStringLiteral.substring(1, versionStringLiteral.length() - 1)); // strip quotes
        }
        catch (RuntimeException e)
        {
            throw new ParserException(location, e.getMessage());
        }

        if (version.compareTo(CURRENT_ZSERIO_VERSION) > 0)
        {
            throw new ParserException(location,
                    "Package specifies compatibility version '" + version + "' " +
                    "which is higher than current zserio version '" + CURRENT_ZSERIO_VERSION + "'!");
        }

        if (version.compareTo(MINIMUM_SUPPORTED_VERSION) < 0)
        {
            throw new ParserException(location,
                    "Package specifies unsupported compatibility version '" + version +
                    "', minimum supported version is '" + MINIMUM_SUPPORTED_VERSION + "'!");
        }
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitCompatibilityVersion(this);
    }

    /**
     * Gets parsed version.
     *
     * @return Version.
     */
    public ZserioVersion getVersion()
    {
        return version;
    }

    private static final ZserioVersion CURRENT_ZSERIO_VERSION =
            ZserioVersion.parseVersion(ZserioVersion.VERSION_STRING);
    private static final ZserioVersion MINIMUM_SUPPORTED_VERSION = new ZserioVersion(2, 4, 0);

    private final ZserioVersion version;
}
