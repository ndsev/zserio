package zserio.emit.common;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Import;
import zserio.ast.Package;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;

/**
 * Implements the Emitter interface and does nothing.
 *
 * It saves some typing for derived classes that only need to implement a few of the emitter actions.
 */
public abstract class DefaultEmitter implements Emitter
{
    @Override
    public void beginRoot(Root root)
    {}

    @Override
    public void endRoot(Root root)
    {}

    @Override
    public void beginPackage(Package packageToken)
    {}

    @Override
    public void endPackage(Package packageToken)
    {}

    @Override
    public void beginImport(Import importNode)
    {}

    @Override
    public void beginConst(Constant constType)
    {}

    @Override
    public void beginSubtype(Subtype subType)
    {}

    @Override
    public void beginStructure(StructureType structureType)
    {}

    @Override
    public void beginChoice(ChoiceType choiceType)
    {}

    @Override
    public void beginUnion(UnionType unionType)
    {}

    @Override
    public void beginEnumeration(EnumType enumType)
    {}

    @Override
    public void beginBitmask(BitmaskType bitmaskType)
    {}

    @Override
    public void beginSqlTable(SqlTableType sqlTableType)
    {}

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType)
    {}

    @Override
    public void beginService(ServiceType service)
    {}

    @Override
    public void beginPubsub(PubsubType pubsub)
    {}
}
