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
import zserio.ast.PackageName;
import zserio.ast.PubsubType;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.emit.common.FileUtil;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;
import zserio.tools.StringJoinUtil;

class PackageEmitter extends HtmlDefaultEmitter
{
    public PackageEmitter(String outputPathName, Parameters extensionParameters, boolean withSvgDiagrams,
            UsedByCollector usedByCollector, SymbolCollector symbolCollector)
    {
        super(outputPathName, extensionParameters, withSvgDiagrams, usedByCollector);

        this.nodesMap = symbolCollector.getNodesMap();

        final String directoryPrefix = ".." + File.separator;
        context = new TemplateDataContext(getWithSvgDiagrams(), getUsedByCollector(), getResourceManager(), ".",
                directoryPrefix + SYMBOL_COLLABORATION_DIRECTORY);
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioEmitException
    {
        final PackageName packageName = pkg.getPackageName();
        final String packageHtmlLink = getPackageHtmlLink(packageName, HTML_CONTENT_DIRECTORY);
        final File outputFile = new File(getOutputPathName(), packageHtmlLink);
        FileUtil.createOutputDirectory(outputFile);
        writer = FileUtil.createWriter(outputFile);

        final String outputDirectory = outputFile.getParent();
        getResourceManager().setCurrentOutputDir(Paths.get(outputDirectory));

        final BeginPackageTemplateData templateData =
                new BeginPackageTemplateData(context, pkg, nodesMap);
        processHtmlTemplate("begin_package.html.ftl", templateData, writer);
    }

    @Override
    public void endPackage(Package pkg) throws ZserioEmitException
    {
        final EndPackageTemplateData templateData =
                new EndPackageTemplateData(context, pkg, nodesMap.get(pkg));
        processHtmlTemplate("end_package.html.ftl", templateData, writer);
        writer.close();
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        final ConstantTemplateData templateData = new ConstantTemplateData(context, constant);
        processHtmlTemplate("constant.html.ftl", templateData, writer);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitException
    {
        final SubtypeTemplateData templateData = new SubtypeTemplateData(context, subtype);
        processHtmlTemplate("subtype.html.ftl", templateData, writer);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        final EnumerationTemplateData templateData = new EnumerationTemplateData(context, enumType);
        processHtmlTemplate("enumeration.html.ftl", templateData, writer);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        final BitmaskTemplateData templateData = new BitmaskTemplateData(context, bitmaskType);
        processHtmlTemplate("bitmask.html.ftl", templateData, writer);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, structureType);
        processHtmlTemplate("structure.html.ftl", templateData, writer);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, unionType);
        processHtmlTemplate("union.html.ftl", templateData, writer);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        final ChoiceTemplateData templateData = new ChoiceTemplateData(context, choiceType);
        processHtmlTemplate("choice.html.ftl", templateData, writer);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        final CompoundTypeTemplateData templateData = new CompoundTypeTemplateData(context, sqlDatabaseType);
        processHtmlTemplate("sql_database.html.ftl", templateData, writer);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        final SqlTableTemplateData templateData = new SqlTableTemplateData(context, sqlTableType);
        processHtmlTemplate("sql_table.html.ftl", templateData, writer);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        final ServiceTemplateData templateData = new ServiceTemplateData(context, serviceType);
        processHtmlTemplate("service.html.ftl", templateData, writer);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        final PubsubTemplateData templateData = new PubsubTemplateData(context, pubsubType);
        processHtmlTemplate("pubsub.html.ftl", templateData, writer);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioEmitException
    {
        final InstantiateTypeTemplateData templateData =
                new InstantiateTypeTemplateData(context, instantiateType);
        processHtmlTemplate("instantiate_type.html.ftl", templateData, writer);
    }

    public static String getPackageHtmlLink(PackageName packageName, String htmlContentDirectory)
    {
        final String packageNameString = (packageName.isEmpty()) ? DEFAULT_PACKAGE_FILE_NAME :
            packageName.toString();

        return StringJoinUtil.joinStrings(htmlContentDirectory, packageNameString + HTML_FILE_EXTENSION,
                File.separator);
    }

    private final Map<Package, List<AstNode>> nodesMap;
    private final TemplateDataContext context;

    private PrintWriter writer;
}
