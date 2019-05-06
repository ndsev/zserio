package zserio.emit.common;

import zserio.ast4.ChoiceType;
import zserio.ast4.ConstType;
import zserio.ast4.EnumType;
import zserio.ast4.Import;
import zserio.ast4.Package;
import zserio.ast4.Root;
import zserio.ast4.ServiceType;
import zserio.ast4.SqlDatabaseType;
import zserio.ast4.SqlTableType;
import zserio.ast4.StructureType;
import zserio.ast4.Subtype;
import zserio.ast4.UnionType;

/**
 * Implements the Emitter interface and does nothing. Saves some typing for derived classes that only need to
 * implement a few of the emitter actions.
 */
public abstract class DefaultEmitter implements Emitter4
{
    @Override
    public void beginRoot(Root root) throws ZserioEmitException {}
    @Override
    public void endRoot(Root root) throws ZserioEmitException {}

    @Override
    public void beginPackage(Package packageToken) throws ZserioEmitException {}

    @Override
    public void beginImport(Import importNode) throws ZserioEmitException {}

    @Override
    public void beginConst(ConstType constType) throws ZserioEmitException {}

    @Override
    public void beginSubtype(Subtype subType) throws ZserioEmitException {}

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException {}

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException {}

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException {}

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException {}

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException {}

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException {}

    @Override
    public void beginService(ServiceType service) throws ZserioEmitException {}
}
