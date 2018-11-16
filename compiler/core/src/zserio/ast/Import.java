package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * AST node for import declaration.
 */
public class Import extends TokenAST
{
    /**
     * Gets the imported package name.
     *
     * @return Package name specified by the import.
     */
    public PackageName getImportedPackageName()
    {
        return importedPackageNameBuilder.get();
    }

    /**
     * Gets the type name specified by the import.
     *
     * @return Type name for single type import or null for package type import.
     */
    public String getImportedTypeName()
    {
        return importedTypeName;
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            if (importedTypeName != null)
                importedPackageNameBuilder.addId(importedTypeName);
            importedTypeName = child.getText();
            break;

        case ZserioParserTokenTypes.MULTIPLY:
            if (importedTypeName != null)
                importedPackageNameBuilder.addId(importedTypeName);
            importedTypeName = null;
            break;

        default:
            return false;
        }

        return true;
    }

    private static final long serialVersionUID = 1L;

    private final PackageName.Builder importedPackageNameBuilder = new PackageName.Builder();
    private String importedTypeName = null;
}
