package zserio.emit.doc;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
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
import zserio.emit.common.FileUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

class PackageEmitter extends DefaultTreeWalker
{
    public PackageEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector, SymbolCollector symbolCollector, PackageCollector packageCollector)
    {
        super();

        this.outputPathName = outputPathName;
        this.nodesMap = symbolCollector.getNodesMap();

        final String htmlRootDirectory = "..";
        final String htmlCurrentDirectory = DocDirectories.CONTENT_DIRECTORY;
        context = new TemplateDataContext(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector,
                packageCollector, htmlRootDirectory, htmlCurrentDirectory);
    }

    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        final String packageHtmlLink = getPackageHtmlLink(pkg, DocDirectories.CONTENT_DIRECTORY);
        final File outputFile = new File(outputPathName, packageHtmlLink);
        FileUtil.createOutputDirectory(outputFile);
        writer = FileUtil.createWriter(outputFile);

        final String outputDirectory = outputFile.getParent();
        context.getDocResourceManager().setCurrentOutputDir(Paths.get(outputDirectory));

        final BeginPackageTemplateData templateData = new BeginPackageTemplateData(context, pkg, nodesMap);
        DocFreeMarkerUtil.processTemplate("begin_package.html.ftl", templateData, writer);
    }

    @Override
    public void endPackage(Package pkg) throws ZserioEmitException
    {
        final EndPackageTemplateData templateData = new EndPackageTemplateData(context, pkg, nodesMap.get(pkg));
        DocFreeMarkerUtil.processTemplate("end_package.html.ftl", templateData, writer);
        writer.close();
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        final ConstantTemplateData templateData = new ConstantTemplateData(context, constant);
        DocFreeMarkerUtil.processTemplate("constant.html.ftl", templateData, writer);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitException
    {
        final SubtypeTemplateData templateData = new SubtypeTemplateData(context, subtype);
        DocFreeMarkerUtil.processTemplate("subtype.html.ftl", templateData, writer);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        final EnumerationTemplateData templateData = new EnumerationTemplateData(context, enumType);
        DocFreeMarkerUtil.processTemplate("enumeration.html.ftl", templateData, writer);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        final BitmaskTemplateData templateData = new BitmaskTemplateData(context, bitmaskType);
        DocFreeMarkerUtil.processTemplate("bitmask.html.ftl", templateData, writer);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, structureType);
        DocFreeMarkerUtil.processTemplate("structure.html.ftl", templateData, writer);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, unionType);
        DocFreeMarkerUtil.processTemplate("union.html.ftl", templateData, writer);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        final ChoiceTemplateData templateData = new ChoiceTemplateData(context, choiceType);
        DocFreeMarkerUtil.processTemplate("choice.html.ftl", templateData, writer);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, sqlDatabaseType);
        DocFreeMarkerUtil.processTemplate("sql_database.html.ftl", templateData, writer);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        final SqlTableTemplateData templateData = new SqlTableTemplateData(context, sqlTableType);
        DocFreeMarkerUtil.processTemplate("sql_table.html.ftl", templateData, writer);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        final ServiceTemplateData templateData = new ServiceTemplateData(context, serviceType);
        DocFreeMarkerUtil.processTemplate("service.html.ftl", templateData, writer);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        final PubsubTemplateData templateData = new PubsubTemplateData(context, pubsubType);
        DocFreeMarkerUtil.processTemplate("pubsub.html.ftl", templateData, writer);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioEmitException
    {
        final InstantiateTypeTemplateData templateData =
                new InstantiateTypeTemplateData(context, instantiateType);
        DocFreeMarkerUtil.processTemplate("instantiate_type.html.ftl", templateData, writer);
    }

    static String getPackageHtmlLink(Package pkg, String htmlContentDirectory)
    {
        final String packageFileName = PackageFileNameMapper.getFileName(pkg);

        return StringJoinUtil.joinStrings(htmlContentDirectory, packageFileName + HTML_FILE_EXTENSION,
                File.separator);
    }

    private static final String HTML_FILE_EXTENSION = ".html";

    private final String outputPathName;
    private final Map<Package, List<AstNode>> nodesMap;
    private final TemplateDataContext context;

    private PrintWriter writer = null;
}
