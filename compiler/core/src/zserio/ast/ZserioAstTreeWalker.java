package zserio.ast;

import zserio.emit.common.TreeWalker;
import zserio.emit.common.ZserioEmitException;

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
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
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
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitImport(Import importNode)
    {
        try
        {
            walker.beginImport(importNode);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitConstant(Constant constant)
    {
        try
        {
            walker.beginConst(constant);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitSubtype(Subtype subtype)
    {
        try
        {
            walker.beginSubtype(subtype);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
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
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
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
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
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
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitEnumType(EnumType enumType)
    {
        try
        {
            walker.beginEnumeration(enumType);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitBitmaskType(BitmaskType bitmaskType)
    {
        try
        {
            walker.beginBitmask(bitmaskType);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
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
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
    {
        try
        {
            walker.beginSqlDatabase(sqlDatabaseType);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitServiceType(ServiceType serviceType)
    {
        try
        {
            walker.beginService(serviceType);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitPubsubType(PubsubType pubsubType)
    {
        try
        {
            walker.beginPubsub(pubsubType);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }
    }

    @Override
    public void visitInstantiateType(InstantiateType instantiateType)
    {
        try
        {
            walker.beginInstantiateType(instantiateType);
        }
        catch (ZserioEmitException e)
        {
            throw new UncheckedZserioEmitException(e);
        }

        final TemplatableType instantiation = (TemplatableType)instantiateType.getTypeReference().getType();
        // emit only explicit instantiations moved to a different package
        if (instantiateType.getPackage() != instantiation.getTemplate().getPackage())
            instantiation.accept(this);
    }

    private boolean needsVisitInstantiations(ZserioTemplatableType template)
    {
        return walker.traverseTemplateInstantiations() && !template.getTemplateParameters().isEmpty();
    }

    private void visitInstantiations(ZserioTemplatableType template) throws ZserioEmitException
    {
        for (ZserioTemplatableType instantiation : template.getInstantiations())
        {
            // emit only instantiations in the current package
            if (template.getPackage() == instantiation.getPackage())
                instantiation.accept(this);
        }
    }

    static class UncheckedZserioEmitException extends RuntimeException
    {
        public UncheckedZserioEmitException(ZserioEmitException originalException)
        {
            this.originalException = originalException;
        }

        public ZserioEmitException getOriginalException()
        {
            return originalException;
        }

        private static final long serialVersionUID = -7096395966484125254L;

        private final ZserioEmitException originalException;
    }

    private final TreeWalker walker;
}
