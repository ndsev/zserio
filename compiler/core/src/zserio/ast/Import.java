package zserio.ast;

import java.util.List;
import java.util.Map;

/**
 * AST node for import defined in the package.
 */
public class Import extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location            AST node location.
     * @param importedPackageName Imported package name.
     * @param importedName        Imported name or null for full package import.
     * @param docComments         List of documentation comments belonging to this node.
     */
    public Import(AstLocation location, PackageName importedPackageName, String importedName,
            List<DocComment> docComments)
    {
        super(location, docComments);

        this.importedPackageName = importedPackageName;
        this.importedName = importedName;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitImport(this);
    }

    /**
     * Gets the imported package.
     *
     * @return Package specified by the import.
     */
    public Package getImportedPackage()
    {
        return importedPackage;
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
     * Gets the symbol specified by the import.
     *
     * @return Symbol for single import or null for full package import.
     */
    public PackageSymbol getImportedSymbol()
    {
        return importedSymbol;
    }

    /**
     * Resolves this import.
     *
     * @param packageNameMap Map of all available package name to the package object.
     */
    void resolveImport(Map<PackageName, Package> packageNameMap)
    {
        importedPackage = packageNameMap.get(importedPackageName);
        if (importedPackage == null)
        {
            // imported package has not been found => this could happen only for default packages
            throw new ParserException(this, "Default package cannot be imported!");
        }

        if (importedName == null)
        {
            // this is a package import
            importedSymbol = null;
        }
        else
        {
            // this is a single import
            importedSymbol = importedPackage.getLocalSymbol(importedName);
            if (importedSymbol == null)
            {
                throw new ParserException(this, "Unresolved import of '" +
                        ZserioTypeUtil.getFullName(importedPackageName, importedName) + "'!");
            }
        }
    }

    private final PackageName importedPackageName;
    private final String importedName;

    private Package importedPackage;
    private PackageSymbol importedSymbol;
}
