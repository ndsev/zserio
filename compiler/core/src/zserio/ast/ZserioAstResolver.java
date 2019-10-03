package zserio.ast;

import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * Implementation of ZserioAstVisitor which manages resolving phase.
 */
public class ZserioAstResolver extends ZserioAstWalker
{
    @Override
    public void visitPackage(Package pkg)
    {
        currentPackage = pkg;

        pkg.visitChildren(this);

        currentPackage = null;
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        subtype.resolve();
        subtype.visitChildren(this);
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        visitType(enumType);
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        visitType(choiceType);
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        visitType(sqlDatabaseType);
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        visitType(sqlTableType);
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        visitType(structureType);
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        visitType(unionType);
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        visitType(serviceType);
    }

    @Override
    public void visitSqlConstraint(SqlConstraint sqlConstraint)
    {
        sqlConstraint.resolve(currentCompoundType);
        sqlConstraint.visitChildren(this);
    }

    @Override
    public void visitTypeReference(TypeReference typeReference)
    {
        typeReference.resolve();
        typeReference.visitChildren(this);
    }

    @Override
    public void visitDocTagSee(DocTagSee docTagSee)
    {
        docTagSee.resolve(currentPackage, currentScopedType);
        docTagSee.visitChildren(this);
    }

    private void visitType(CompoundType compoundType)
    {
        if (compoundType.getTemplateParameters().isEmpty())
        {
            currentScopedType = compoundType;
            currentCompoundType = compoundType;

            compoundType.visitChildren(this);

            currentScopedType = null;
            currentCompoundType = null;
        }
        else
        {
            visitInstantiations(compoundType);
        }
    }

    private void visitType(ZserioScopedType scopedType)
    {
        currentScopedType = scopedType;

        scopedType.visitChildren(this);

        currentScopedType = null;
    }

    private void visitInstantiations(ZserioTemplatableType template)
    {
        for (ZserioTemplatableType instantiation : template.getInstantiations())
        {
            try
            {
                instantiation.accept(this);
            }
            catch (ParserException e)
            {
                ZserioToolPrinter.printError(instantiation.getInstantiationLocation(),
                        "In instantiation of '" + template.getName() + "' required from here");
                throw e;
            }
        }
    }

    private Package currentPackage = null;
    private CompoundType currentCompoundType = null;
    private ZserioScopedType currentScopedType = null;
};
