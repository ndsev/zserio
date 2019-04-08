package zserio.ast4;

import org.antlr.v4.runtime.Token;

import zserio.ast.PackageName;

public class Import extends AstNodeBase
{
    public Import(Token token, PackageName importedPackageName, String importedTypeName)
    {
        super(token);
        this.importedPackageName = importedPackageName;
        this.importedTypeName = importedTypeName;
    }

    @Override
    public void walk(ZserioListener listener)
    {
        listener.enterImport(this);
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
