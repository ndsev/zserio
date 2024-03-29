package zserio.extension.doc;

import java.io.File;
import java.io.PrintWriter;
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
import zserio.ast.RuleGroup;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.FileUtil;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.StringJoinUtil;

/**
 * Package emitter.
 *
 * Package emitter creates main HTML for each package containing description of all package symbols.
 */
final class PackageEmitter extends DefaultTreeWalker
{
    public PackageEmitter(OutputFileManager outputFileManager, DocExtensionParameters docParameters,
            DocResourceManager docResourceManager, SymbolCollector symbolCollector,
            PackageCollector packageCollector, UsedByCollector usedByCollector,
            UsedByChoiceCollector usedByChoiceCollector, Package rootPackage, boolean hasSchemaRules)
    {
        this.outputFileManager = outputFileManager;

        final String outputDir = docParameters.getOutputDir();
        htmlPackagesDirectory =
                StringJoinUtil.joinStrings(outputDir, DocDirectories.PACKAGES_DIRECTORY, File.separator);
        docResourceManager.setCurrentOutputDir(htmlPackagesDirectory);

        nodesMap = symbolCollector.getNodesMap();

        final String htmlRootDirectory = StringJoinUtil.joinStrings("..", "..", File.separator);
        context = new PackageTemplateDataContext(
                docParameters, htmlRootDirectory, usedByCollector, usedByChoiceCollector, docResourceManager);

        headerNavigation = new HeaderNavigationTemplateData(
                context, rootPackage, hasSchemaRules, HeaderNavigationTemplateData.ActiveItem.PACKAGES_ITEM);
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        final String packageHtmlLink = getPackageHtmlLink(pkg, htmlPackagesDirectory);
        final File outputFile = new File(packageHtmlLink);
        FileUtil.createOutputDirectory(outputFile);
        writer = FileUtil.createWriter(outputFile);

        final BeginPackageTemplateData templateData =
                new BeginPackageTemplateData(context, pkg, nodesMap, headerNavigation);

        DocFreeMarkerUtil.processTemplate("begin_package.html.ftl", templateData, writer);

        outputFileManager.registerOutputFile(outputFile);
    }

    @Override
    public void endPackage(Package pkg) throws ZserioExtensionException
    {
        final EndPackageTemplateData templateData = new EndPackageTemplateData(context, pkg);
        DocFreeMarkerUtil.processTemplate("end_package.html.ftl", templateData, writer);
        writer.close();
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        final ConstantTemplateData templateData = new ConstantTemplateData(context, constant);
        DocFreeMarkerUtil.processTemplate("constant.html.ftl", templateData, writer);
    }

    @Override
    public void beginRuleGroup(RuleGroup ruleGroup) throws ZserioExtensionException
    {
        final RuleGroupTemplateData templateData = new RuleGroupTemplateData(context, ruleGroup);
        DocFreeMarkerUtil.processTemplate("rule_group.html.ftl", templateData, writer);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        final SubtypeTemplateData templateData = new SubtypeTemplateData(context, subtype);
        DocFreeMarkerUtil.processTemplate("subtype.html.ftl", templateData, writer);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        final EnumerationTemplateData templateData = new EnumerationTemplateData(context, enumType);
        DocFreeMarkerUtil.processTemplate("enumeration.html.ftl", templateData, writer);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        final BitmaskTemplateData templateData = new BitmaskTemplateData(context, bitmaskType);
        DocFreeMarkerUtil.processTemplate("bitmask.html.ftl", templateData, writer);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, structureType);
        DocFreeMarkerUtil.processTemplate("structure.html.ftl", templateData, writer);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, unionType);
        DocFreeMarkerUtil.processTemplate("union.html.ftl", templateData, writer);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        final ChoiceTemplateData templateData = new ChoiceTemplateData(context, choiceType);
        DocFreeMarkerUtil.processTemplate("choice.html.ftl", templateData, writer);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, sqlDatabaseType);
        DocFreeMarkerUtil.processTemplate("sql_database.html.ftl", templateData, writer);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        final SqlTableTemplateData templateData = new SqlTableTemplateData(context, sqlTableType);
        DocFreeMarkerUtil.processTemplate("sql_table.html.ftl", templateData, writer);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioExtensionException
    {
        final ServiceTemplateData templateData = new ServiceTemplateData(context, serviceType);
        DocFreeMarkerUtil.processTemplate("service.html.ftl", templateData, writer);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioExtensionException
    {
        final PubsubTemplateData templateData = new PubsubTemplateData(context, pubsubType);
        DocFreeMarkerUtil.processTemplate("pubsub.html.ftl", templateData, writer);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {
        final InstantiateTypeTemplateData templateData =
                new InstantiateTypeTemplateData(context, instantiateType);
        DocFreeMarkerUtil.processTemplate("instantiate_type.html.ftl", templateData, writer);
    }

    static String getPackageHtmlLink(Package pkg, String htmlPackagesDirectory)
    {
        final String packageFileName = PackageFileNameMapper.getFileName(pkg);

        return StringJoinUtil.joinStrings(
                htmlPackagesDirectory, packageFileName + HTML_FILE_EXTENSION, File.separator);
    }

    private static final String HTML_FILE_EXTENSION = ".html";

    private final OutputFileManager outputFileManager;
    private final String htmlPackagesDirectory;
    private final Map<Package, List<AstNode>> nodesMap;
    private final PackageTemplateDataContext context;
    private final HeaderNavigationTemplateData headerNavigation;

    private PrintWriter writer = null;
}
