package zserio.ast;

import zserio.emit.common.Emitter;
import zserio.emit.common.ZserioEmitException;

/**
 * Implementation of ZserioAstVisitor which adapts the visitor interface to emitter interface for extensions.
 */
public class ZserioAstEmitter extends ZserioAstWalker
{
    /**
     * Constructor.
     *
     * @param emitter Emitter to call during AST walking.
     * @param resolveTempaltes Whether to resolve templates and emit only its instances.
     */
    public ZserioAstEmitter(Emitter emitter, boolean resolveTemplates)
    {
        this.emitter = emitter;
        this.resolveTemplates = resolveTemplates;
    }

    @Override
    public void visitRoot(Root root)
    {
        try
        {
            emitter.beginRoot(root);
            root.visitChildren(this);
            emitter.endRoot(root);
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
            emitter.beginPackage(pkg);
            pkg.visitChildren(this);
            emitter.endPackage(pkg);
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
            emitter.beginImport(importNode);
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
            emitter.beginConst(constant);
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
            emitter.beginSubtype(subtype);
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
                emitter.beginStructure(structureType);
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
                emitter.beginChoice(choiceType);
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
                emitter.beginUnion(unionType);
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
            emitter.beginEnumeration(enumType);
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
            emitter.beginBitmask(bitmaskType);
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
                emitter.beginSqlTable(sqlTableType);
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
            emitter.beginSqlDatabase(sqlDatabaseType);
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
            emitter.beginService(serviceType);
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
            emitter.beginPubsub(pubsubType);
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
            emitter.beginInstantiateType(instantiateType);
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
        return resolveTemplates && !template.getTemplateParameters().isEmpty();
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

    private final Emitter emitter;
    private final boolean resolveTemplates;
}
