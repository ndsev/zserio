package zserio.ast4;

import zserio.emit.common.Emitter4;
import zserio.emit.common.ZserioEmitException;

public class ZserioAstEmitter extends ZserioAstVisitor.Base
{
    public ZserioAstEmitter(Emitter4 emitter)
    {
        this.emitter = emitter;
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
    public void visitSqlTableType(SqlTableType sqlTableType)
    {
        try
        {
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

    protected static class UncheckedZserioEmitException extends RuntimeException
    {
        public UncheckedZserioEmitException(ZserioEmitException cause)
        {
            this.cause = cause;
        }

        public ZserioEmitException getCause()
        {
            return cause;
        }

        private static final long serialVersionUID = -7096395966484125254L;
        private ZserioEmitException cause;
    }

    private final Emitter4 emitter;
}
