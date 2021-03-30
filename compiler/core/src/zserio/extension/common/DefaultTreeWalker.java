package zserio.extension.common;

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
    public void beginRoot(Root root) throws ZserioExtensionException
    {}

    @Override
    public void endRoot(Root root) throws ZserioExtensionException
    {}

    @Override
    public void beginPackage(Package packageToken) throws ZserioExtensionException
    {}

    @Override
    public void endPackage(Package packageToken) throws ZserioExtensionException
    {}

    @Override
    public void beginImport(Import importNode) throws ZserioExtensionException
    {}

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {}

    @Override
    public void beginSubtype(Subtype subType) throws ZserioExtensionException
    {}

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {}

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {}

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {}

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {}

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {}

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {}

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {}

    @Override
    public void beginService(ServiceType service) throws ZserioExtensionException
    {}

    @Override
    public void beginPubsub(PubsubType pubsub) throws ZserioExtensionException
    {}

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {}
}
