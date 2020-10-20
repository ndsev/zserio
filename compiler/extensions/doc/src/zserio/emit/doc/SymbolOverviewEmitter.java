package zserio.emit.doc;

import java.io.File;
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
import zserio.ast.Root;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

class SymbolOverviewEmitter extends HtmlDefaultEmitter
{
    public SymbolOverviewEmitter(String outputPathName, Parameters extensionParameters,
            boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector);

        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(),  getResourceManager(),
                HTML_CONTENT_DIRECTORY, SYMBOL_COLLABORATION_DIRECTORY, DB_STRUCTURE_DIRECTORY);

        nodesMap = new HashMap<Package, List<AstNode>>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final Object templateData = new SymbolOverviewTemplateData(context, nodesMap);
        final File outputFile = new File(getOutputPathName(), SYMBOL_OVERVIEW_FILE_NAME);
        processHtmlTemplate(TEMPLATE_SOURCE_NAME, templateData, outputFile);
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

    private static final String SYMBOL_OVERVIEW_FILE_NAME = "symbol_overview.html";
    private static final String TEMPLATE_SOURCE_NAME = "symbol_overview.html.ftl";

    private final TemplateDataContext context;
    private final Map<Package, List<AstNode>> nodesMap;

    private List<AstNode> currentNodes;
}
