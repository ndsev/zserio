package zserio.emit.doc;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
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

public class SymbolOverviewEmitter extends HtmlDefaultEmitter
{
    public SymbolOverviewEmitter(String outputPathName, Parameters extensionParameters,
            boolean withSvgDiagrams, UsedByCollector usedByCollector)
    {
        super(extensionParameters, withSvgDiagrams, usedByCollector);

        this.outputPathName = outputPathName;
        packageNames = new TreeSet<String>();
        linkedTypes = new TreeSet<LinkedType>();
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        final Object templateData = new SymbolOverviewTemplateData(packageNames, linkedTypes);
        final File outputFile = new File(outputPathName, SYMBOL_OVERVIEW_FILE_NAME);
        processHtmlTemplate(TEMPLATE_SOURCE_NAME, templateData, outputFile);
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        super.beginPackage(pkg);

        // TODO consider to use '.' to '_' but....
        packageNames.add(pkg.getPackageName().toString("_")); // TODO[mikir] replace it when top level package is ready
//      packageNames.add(getPackageMapper().getPackageName(pkg).toString("_"));
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(constant));
    }

    @Override
    public void beginSubtype(Subtype subType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(subType));
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(structureType));
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(choiceType));
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(unionType));
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(enumType));
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(bitmaskType));
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(sqlTableType));
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(sqlDatabaseType));
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(serviceType));
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        linkedTypes.add(new LinkedType(pubsubType));
    }

    private static final String SYMBOL_OVERVIEW_FILE_NAME = "symbol_overview.html";
    private static final String TEMPLATE_SOURCE_NAME = "symbol_overview.html.ftl";

    private final String outputPathName;
    private final Set<String> packageNames;
    private final Set<LinkedType> linkedTypes;
}
