package zserio.emit.common;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Import;
import zserio.ast.InstantiateType;
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
 * Implements the TreeWalker interface and does nothing.
 *
 * It saves some typing for derived classes that only need to implement a few of the TreeWalker actions.
 */
public abstract class DefaultTreeWalker implements TreeWalker
{
    @Override
    public void beginRoot(Root root) throws ZserioEmitException
    {}

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {}

    @Override
    public void beginPackage(Package packageToken) throws ZserioEmitException
    {}

    @Override
    public void endPackage(Package packageToken) throws ZserioEmitException
    {}

    @Override
    public void beginImport(Import importNode) throws ZserioEmitException
    {}

    @Override
    public void beginConst(Constant constType) throws ZserioEmitException
    {}

    @Override
    public void beginSubtype(Subtype subType) throws ZserioEmitException
    {}

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {}

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {}

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {}

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {}

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {}

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {}

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {}

    @Override
    public void beginService(ServiceType service) throws ZserioEmitException
    {}

    @Override
    public void beginPubsub(PubsubType pubsub) throws ZserioEmitException
    {}

    @Override
    public void beginInstantiateType(InstantiateType pubsub) throws ZserioEmitException
    {}
}
