package zserio.ast;

import java.util.Map;

import zserio.tools.WarningsConfig;

/**
 * Implementation of ZserioAstVisitor which resolves symbols for see documentation tags.
 */
public final class ZserioAstSymbolResolver extends ZserioAstWalker
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
    public void visitRoot(Root root)
    {
        this.packageNameMap = root.getPackageNameMap();

        root.visitChildren(this);

        this.packageNameMap = null;
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
    public void visitDocCommentMarkdown(DocCommentMarkdown docComment)
    {
        docComment.visitChildren(this);
        docComment.resolve(currentPackage, this);
    }

    @Override
    public void visitDocTagSee(DocTagSee docTagSee)
    {
        docTagSee.resolve(packageNameMap, currentPackage, currentScopedType, warningsConfig);
        docTagSee.visitChildren(this);
    }

    private void visitType(CompoundType compoundType)
    {
        currentScopedType = compoundType;
        compoundType.visitChildren(this);
        currentScopedType = null;

        if (!compoundType.getTemplateParameters().isEmpty())
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
    private Map<PackageName, Package> packageNameMap = null;
    private Package currentPackage = null;
    private ZserioScopedType currentScopedType = null;
};
