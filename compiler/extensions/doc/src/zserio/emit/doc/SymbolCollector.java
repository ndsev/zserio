package zserio.emit.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PubsubType;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.emit.common.DefaultTreeWalker;
import zserio.emit.common.ZserioEmitException;

class SymbolCollector extends DefaultTreeWalker
{
    public SymbolCollector()
    {
        nodesMap = new HashMap<Package, List<AstNode>>();
    }

    public Map<Package, List<AstNode>> getNodesMap()
    {
        return nodesMap;
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        currentNodes = new ArrayList<AstNode>();
    }

    @Override
    public void endPackage(Package pkg) throws ZserioEmitException
    {
        nodesMap.put(pkg, currentNodes);
        currentNodes = null;
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        currentNodes.add(constant);
    }

    @Override
    public void beginSubtype(Subtype subType) throws ZserioEmitException
    {
        currentNodes.add(subType);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        currentNodes.add(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        currentNodes.add(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        currentNodes.add(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        currentNodes.add(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        currentNodes.add(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        currentNodes.add(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        currentNodes.add(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        currentNodes.add(serviceType);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        currentNodes.add(pubsubType);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioEmitException
    {
        currentNodes.add(instantiateType);
    }

    private final Map<Package, List<AstNode>> nodesMap;

    private List<AstNode> currentNodes;
}
