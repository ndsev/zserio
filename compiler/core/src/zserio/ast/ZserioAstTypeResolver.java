package zserio.ast;

import java.util.Map;

/**
 * Implementation of ZserioAstVisitor which manages type resolving phase.
 */
public class ZserioAstTypeResolver extends ZserioAstWalker
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
        pkg.importTypes(packageNameMap);
        pkg.visitChildren(this);
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        subtype.resolve();
        subtype.visitChildren(this);
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        visitType(structureType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        visitType(choiceType);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        visitType(unionType);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        visitType(sqlTableType);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        typeReference.resolve();
        typeReference.visitChildren(this);
    }

    private void visitType(TemplatableType templatableType)
    {
        // skip template declarations
        if (templatableType.getTemplateParameters().isEmpty())
            templatableType.visitChildren(this);
    }

    private Map<PackageName, Package> packageNameMap = null;
}
