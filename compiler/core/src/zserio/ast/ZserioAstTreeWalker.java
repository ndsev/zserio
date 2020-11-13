package zserio.ast;

import zserio.extension.common.TreeWalker;
import zserio.extension.common.ZserioExtensionException;

/**
 * Implementation of ZserioAstVisitor which calls appropriate TreeWalker interface for extensions.
 */
public class ZserioAstTreeWalker extends ZserioAstWalker
{
    /**
     * Constructor.
     *
     * @param walker Walker to call during AST walking.
     */
    public ZserioAstTreeWalker(TreeWalker walker)
    {
        this.walker = walker;
    }

    @Override
    public void visitRoot(Root root)
    {
        try
        {
            walker.beginRoot(root);
            root.visitChildren(this);
            walker.endRoot(root);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitPackage(Package pkg)
    {
        try
        {
            walker.beginPackage(pkg);
            pkg.visitChildren(this);
            walker.endPackage(pkg);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitImport(Import importNode)
    {
        try
        {
            walker.beginImport(importNode);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitConstant(Constant constant)
    {
        try
        {
            walker.beginConst(constant);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        try
        {
            walker.beginSubtype(subtype);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitStructureType(StructureType structureType)
    {
        try
        {
            if (needsVisitInstantiations(structureType))
                visitInstantiations(structureType);
            else
                walker.beginStructure(structureType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitChoiceType(ChoiceType choiceType)
    {
        try
        {
            if (needsVisitInstantiations(choiceType))
                visitInstantiations(choiceType);
            else
                walker.beginChoice(choiceType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitUnionType(UnionType unionType)
    {
        try
        {
            if (needsVisitInstantiations(unionType))
                visitInstantiations(unionType);
            else
                walker.beginUnion(unionType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        try
        {
            walker.beginEnumeration(enumType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitBitmaskType(BitmaskType bitmaskType)
    {
        try
        {
            walker.beginBitmask(bitmaskType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        try
        {
            if (needsVisitInstantiations(sqlTableType))
                visitInstantiations(sqlTableType);
            else
                walker.beginSqlTable(sqlTableType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        try
        {
            walker.beginSqlDatabase(sqlDatabaseType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        try
        {
            walker.beginService(serviceType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitPubsubType(PubsubType pubsubType)
    {
        try
        {
            walker.beginPubsub(pubsubType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }
    }

    @Override
    public void visitInstantiateType(InstantiateType instantiateType)
    {
        try
        {
            walker.beginInstantiateType(instantiateType);
        }
        catch (ZserioExtensionException e)
        {
            throw new UncheckedZserioExtensionException(e);
        }

        if (walker.traverseTemplateInstantiations())
        {
            final TemplatableType instantiation = (TemplatableType)instantiateType.getTypeReference().getType();
            // walk only through explicit instantiations moved to a different package
            if (instantiateType.getPackage() != instantiation.getTemplate().getPackage())
                instantiation.accept(this);
        }
    }

    private boolean needsVisitInstantiations(ZserioTemplatableType template)
    {
        return walker.traverseTemplateInstantiations() && !template.getTemplateParameters().isEmpty();
    }

    private void visitInstantiations(ZserioTemplatableType template) throws ZserioExtensionException
    {
        for (ZserioTemplatableType instantiation : template.getInstantiations())
        {
            // walk only through instantiations in the current package
            if (template.getPackage() == instantiation.getPackage())
                instantiation.accept(this);
        }
    }

    static class UncheckedZserioExtensionException extends RuntimeException
    {
        public UncheckedZserioExtensionException(ZserioExtensionException originalException)
        {
            this.originalException = originalException;
        }

        public ZserioExtensionException getOriginalException()
        {
            return originalException;
        }

        private static final long serialVersionUID = -7096395966484125254L;

        private final ZserioExtensionException originalException;
    }

    private final TreeWalker walker;
}
