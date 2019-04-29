package zserio.ast4;

import java.util.Map;

public class ZserioAstResolver extends ZserioAstVisitor.Base
{
    @Override
    public void visitRoot(Root root)
    {
        packageNameMap = root.getPackageNameMap();

        root.visitChildren(this);

        packageNameMap = null;
    }

    @Override
    public void visitPackage(Package pkg)
    {
        pkg.resolve(packageNameMap);
        pkg.visitChildren(this);
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        subtype.resolve();
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        sqlConstraint.resolve();
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        typeReference.resolve();
    }

    private Map<PackageName, Package> packageNameMap = null;
};
