package zserio.ast;

/**
 * AST node for import defined in the package.
 */
public class Import extends AstNodeBase
{
    /**
     * Constructor.
     *
     * @param location            AST node location.
     * @param importedPackageName Imported package name.
     * @param importedName        Imported name or null for full package import.
     */
    public Import(AstLocation location, PackageName importedPackageName, String importedName)
    {
        super(location);

        this.importedPackageName = importedPackageName;
        this.importedName = importedName;
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
     * Gets the name specified by the import.
     *
     * @return Name for single import or null for full package import.
     */
    public String getImportedName()
    {
        return importedName;
    }

    private final PackageName importedPackageName;
    private final String importedName;
}
