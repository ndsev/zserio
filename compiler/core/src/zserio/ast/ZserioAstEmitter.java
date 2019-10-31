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
     */
    public ZserioAstEmitter(Emitter emitter)
    {
        this.emitter = emitter;
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
    public void visitConstType(ConstType constType)
    {
        try
        {
            emitter.beginConst(constType);
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
            if (structureType.getTemplateParameters().isEmpty())
                emitter.beginStructure(structureType);
            else
                visitInstantiations(structureType);
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
            if (choiceType.getTemplateParameters().isEmpty())
                emitter.beginChoice(choiceType);
            else
                visitInstantiations(choiceType);
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
            if (unionType.getTemplateParameters().isEmpty())
                emitter.beginUnion(unionType);
            else
                visitInstantiations(unionType);
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
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        try
        {
            if (sqlTableType.getTemplateParameters().isEmpty())
                emitter.beginSqlTable(sqlTableType);
            else
                visitInstantiations(sqlTableType);
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
    public void visitInstantiateType(InstantiateType instantiateType)
    {
        final TemplatableType instantiation = (TemplatableType)instantiateType.getTypeReference().getType();
        // emit only explicit instantiations moved to a different package
        if (instantiateType.getPackage() != instantiation.getTemplate().getPackage())
            instantiation.accept(this);
    }

    private void visitInstantiations(ZserioTemplatableType template)
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
}
