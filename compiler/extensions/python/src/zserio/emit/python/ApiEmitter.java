package zserio.emit.python;

import java.util.TreeMap;
import zserio.ast.ConstType;
import zserio.ast.EnumType;
import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.Root;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.emit.common.PackageMapper;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class ApiEmitter extends PythonDefaultEmitter
{
    public ApiEmitter(String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void endRoot(Root root) throws ZserioEmitException
    {
        for (ApiEmitterTemplateData packageTemplateData : packageMapping.values())
        {
            final PackageName packageName = packageTemplateData.getPackageName();
            processTemplate(API_TEMPLATE, packageTemplateData, packageName, API_FILENAME_ROOT);
        }
    }

    @Override
    public void beginPackage(Package zserioPackage) throws ZserioEmitException
    {
        final PackageMapper packageMapper = getTemplateDataContext().getPythonPackageMapper();
        final PackageName mappedPackageName = packageMapper.getPackageName(zserioPackage);

        if (mappedPackageName.isEmpty())
            addEmptyPackageMapping();
        else
            addPackageMapping(mappedPackageName);
    }

    @Override
    public void beginConst(ConstType constType) throws ZserioEmitException
    {
        addTypeMapping(constType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioEmitException
    {
        addTypeMapping(enumType);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioEmitException
    {
        addTypeMapping(subtype);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioEmitException
    {
        addTypeMapping(unionType);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioEmitException
    {
        addTypeMapping(structureType);
    }

    private void addTypeMapping(ZserioType zserioType) throws ZserioEmitException
    {
        final PackageMapper packageMapper = getTemplateDataContext().getPythonPackageMapper();
        final PackageName packageName = packageMapper.getPackageName(zserioType);
        ApiEmitterTemplateData packageTemplateData = packageMapping.get(packageName);
        if (packageTemplateData == null)
            throw new ZserioEmitException("ApiEmitter: Package not yet mapped!");
        packageTemplateData.addType(zserioType);
    }

    private void addEmptyPackageMapping() throws ZserioEmitException
    {
        if (!packageMapping.isEmpty())
            throw new ZserioEmitException("ApiEmitter: Empty package shall be first!");
        packageMapping.put(PackageName.EMPTY,
                new ApiEmitterTemplateData(getTemplateDataContext(), PackageName.EMPTY));
    }

    private void addPackageMapping(PackageName mappedPackageName)
    {
        ApiEmitterTemplateData prevPackageTemplateData = packageMapping.get(PackageName.EMPTY);
        final PackageName.Builder currentPackageNameBuilder = new PackageName.Builder();
        final TemplateDataContext context = getTemplateDataContext();
        for (String id : mappedPackageName.getIdList())
        {
            currentPackageNameBuilder.addId(id);
            ApiEmitterTemplateData apiEmitterTemplateData = packageMapping.get(currentPackageNameBuilder.get());
            if (apiEmitterTemplateData == null)
            {
                apiEmitterTemplateData = new ApiEmitterTemplateData(context, currentPackageNameBuilder.get());
                packageMapping.put(currentPackageNameBuilder.get(), apiEmitterTemplateData);
            }

            if (prevPackageTemplateData != null)
                prevPackageTemplateData.addSubpackage(id);
            prevPackageTemplateData = apiEmitterTemplateData;
        }
    }

    private static final String API_TEMPLATE = "api.py.ftl";
    private static final String API_FILENAME_ROOT = "api";

    private final TreeMap<PackageName, ApiEmitterTemplateData> packageMapping =
            new TreeMap<PackageName, ApiEmitterTemplateData>();
}
