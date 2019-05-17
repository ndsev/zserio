package zserio.ast;

import org.antlr.v4.runtime.Token;

/**
 * AST node for import defined in the package.
 */
public class Import extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param token               ANTLR4 token to localize AST node in the sources.
     * @param importedPackageName Imported package name.
     * @param importedTypeName    Imported package type name or null for package type import.
     */
    public Import(Token token, PackageName importedPackageName, String importedTypeName)
    {
        super(token);

        this.importedPackageName = importedPackageName;
        this.importedTypeName = importedTypeName;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitImport(this);
    }

    /**
     * Gets the imported package name.
     *
     * @return Package name specified by the import.
     */
    public PackageName getImportedPackageName()
    {
        return importedPackageName;
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

    private final PackageName importedPackageName;
    private final String importedTypeName;
}
