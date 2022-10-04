package zserio.ast;

import zserio.tools.WarningsConfig;

/**
 * Implementation of ZserioAstVisitor which resolves symbols for see documentation tags.
 */
public class ZserioAstSymbolResolver extends ZserioAstWalker
{
    /**
     * Constructor.
     *
     * @param warningsConfig Warnings subsystem configuration.
     */
    public ZserioAstSymbolResolver(WarningsConfig warningsConfig)
    {
        this.warningsConfig = warningsConfig;
    }

    @Override
    public void visitPackage(Package pkg)
    {
        currentPackage = pkg;

        pkg.visitChildren(this);

        currentPackage = null;
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
    public void visitPubsubType(PubsubType pubsubType)
    {
        visitType(pubsubType);
    }

    @Override
    public void visitDocTagSee(DocTagSee docTagSee)
    {
        docTagSee.resolve(currentPackage, currentScopedType, warningsConfig);
        docTagSee.visitChildren(this);
    }

    private void visitType(CompoundType compoundType)
    {
        if (compoundType.getTemplateParameters().isEmpty())
        {
            currentScopedType = compoundType;
            compoundType.visitChildren(this);
            currentScopedType = null;
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
                throw new InstantiationException(e, instantiation.getInstantiationReferenceStack());
            }
        }
    }

    private final WarningsConfig warningsConfig;
    private Package currentPackage = null;
    private ZserioScopedType currentScopedType = null;
};
