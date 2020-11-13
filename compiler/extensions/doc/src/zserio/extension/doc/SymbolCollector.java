package zserio.extension.doc;

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
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;

class SymbolCollector extends DefaultTreeWalker
{
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
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        currentNodes = new ArrayList<AstNode>();
    }

    @Override
    public void endPackage(Package pkg) throws ZserioExtensionException
    {
        nodesMap.put(pkg, currentNodes);
        currentNodes = null;
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        currentNodes.add(constant);
    }

    @Override
    public void beginSubtype(Subtype subType) throws ZserioExtensionException
    {
        currentNodes.add(subType);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        currentNodes.add(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        currentNodes.add(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        currentNodes.add(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        currentNodes.add(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        currentNodes.add(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        currentNodes.add(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        currentNodes.add(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioExtensionException
    {
        currentNodes.add(serviceType);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioExtensionException
    {
        currentNodes.add(pubsubType);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {
        currentNodes.add(instantiateType);
    }

    private final Map<Package, List<AstNode>> nodesMap = new HashMap<Package, List<AstNode>>();

    private List<AstNode> currentNodes;
}
