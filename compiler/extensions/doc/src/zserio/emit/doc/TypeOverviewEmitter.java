package zserio.emit.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import zserio.ast.AstNode;
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
import zserio.tools.StringJoinUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TypeOverviewEmitter extends DefaultHtmlEmitter
{
    public TypeOverviewEmitter(String outputPath)
    {
        super(outputPath);
    }

    public Set<String> getPackageNames()
    {
        return packageNames;
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        try
        {
            for (Map.Entry<Package, List<AstNode>> packageEntry : packages.entrySet())
            {
                for (AstNode type : packageEntry.getValue())
                {
                    final String typeName = DocEmitterTools.getZserioName(type);
                    boolean isDoubleDefinedType = Boolean.TRUE.equals(doubleTypeNames.get(typeName));
                    LinkedType linkedType = new LinkedType(type, isDoubleDefinedType);
                    typeMap.put(getFullTypeName(typeName, packageEntry.getKey()), linkedType);
                }
            }

            Template tpl = cfg.getTemplate("doc/type_overview.html.ftl");
            openOutputFile(directory, "overview" + HTML_EXT);

            tpl.process(this, writer);
        }
        catch (IOException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        catch (TemplateException exception)
        {
            throw new ZserioEmitException(exception.getMessage());
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    @Override
    public void beginPackage(Package packageToken) throws ZserioEmitException
    {
        super.beginPackage(packageToken);
        localTypes = new ArrayList<AstNode>();
    }

    @Override
    public void endPackage(Package packageToken) throws ZserioEmitException
    {
        for (AstNode type : localTypes)
        {
            final String typeName = DocEmitterTools.getZserioName(type);
            if (doubleTypeNames.containsKey(typeName))
                doubleTypeNames.put(typeName, true);
            else
                doubleTypeNames.put(typeName, false);
        }
        String pkgName = currentPackage.getPackageName().toString();
        pkgName = pkgName.replace('.', '_');
        packageNames.add(pkgName);
        packages.put(currentPackage, localTypes);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioEmitException
    {
        localTypes.add(constant);
    }

    @Override
    public void beginSubtype(Subtype subType) throws ZserioEmitException
    {
        localTypes.add(subType);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        localTypes.add(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioEmitException
    {
        localTypes.add(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        localTypes.add(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        localTypes.add(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioEmitException
    {
        localTypes.add(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioEmitException
    {
        localTypes.add(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioEmitException
    {
        localTypes.add(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType serviceType) throws ZserioEmitException
    {
        localTypes.add(serviceType);
    }

    @Override
    public void beginPubsub(PubsubType pubsubType) throws ZserioEmitException
    {
        localTypes.add(pubsubType);
    }

    public Collection<LinkedType> getTypes()
    {
        return typeMap.values();
    }

    private String getFullTypeName(String typeName, Package pkg)
    {
        if (pkg.getPackageName().isEmpty())
            return typeName;

        return typeName + "." + getReversePackageName(pkg);
    }

    private static String getReversePackageName(Package pkg)
    {
        final List<String> packageIds = new ArrayList<String>();
        for (String id : pkg.getPackageName().getIdList())
            packageIds.add(id);
        final List<String> reversePackageIds = new ArrayList<String>();
        for (int i = packageIds.size() - 1; i >= 0; i--)
            reversePackageIds.add(packageIds.get(i));

        return StringJoinUtil.joinStrings(reversePackageIds, ".");
    }

    private final Map<String, LinkedType> typeMap = new TreeMap<String, LinkedType>();
    private final Map<String, Boolean> doubleTypeNames = new HashMap<String, Boolean>();
    private final HashSet<String> packageNames = new HashSet<String>();
    private final Map<Package, List<AstNode>> packages = new HashMap<Package, List<AstNode>>();
    private List<AstNode> localTypes = new ArrayList<AstNode>();
}
