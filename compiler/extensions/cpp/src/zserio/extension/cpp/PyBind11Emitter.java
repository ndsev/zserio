package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.Root;
import zserio.ast.StructureType;
import zserio.ast.UnionType;
import zserio.ast.ZserioType;
import zserio.extension.common.OutputFileManager;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.symbols.CppNativeSymbol;
import zserio.extension.cpp.types.CppNativeType;

/**
 * Emits Python binding based on pybind11 library.
 */
class PyBind11Emitter extends CppDefaultEmitter
{
    public PyBind11Emitter(OutputFileManager outputFileManager, CppExtensionParameters cppParameters)
    {
        super(outputFileManager, cppParameters);
    }

    @Override
    public void endRoot(Root root) throws ZserioExtensionException
    {
        for (Map.Entry<PackageName, PyBind11TemplateData> packageMappingEntry : packageMapping.entrySet())
        {
            final PackageName packageName = packageMappingEntry.getKey();

            final PyBind11TemplateData templateData = packageMappingEntry.getValue();
            processHeaderTemplate(PYBIND11_HEADER_TEMPLATE, templateData, packageName, PYBIND11_FILENAME_ROOT);
            processSourceTemplate(PYBIND11_SOURCE_TEMPLATE, templateData, packageName, PYBIND11_FILENAME_ROOT);
        }

        processSourceTemplate(PYBIND11_MODULE_SOURCE_TEMPLATE,
                new PyBind11ModuleTemplateData(getTemplateDataContext(), root, packageMapping),
                PackageName.EMPTY, PYBIND11_MODULE_FILENAME_ROOT);

        processPyTemplate(SETUP_PY_TEMPLATE, new SetupPyTemplateData(getTemplateDataContext(), root),
                PackageName.EMPTY, SETUP_PY_FILENAME_ROOT);
    }

    @Override
    public void beginPackage(Package zserioPackage) throws ZserioExtensionException
    {
        super.beginPackage(zserioPackage);

        final PackageName packageName = zserioPackage.getPackageName();
        if (packageName.isEmpty())
            addEmptyPackageMapping();
        else
            addPackageMapping(packageName);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        addTypeMapping(choiceType);
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        addConstantMapping(constant);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        addTypeMapping(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        addTypeMapping(bitmaskType);
    }

    //@Override
    //public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    //{
    //    addTypeMapping(subtype);
    //}

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        addTypeMapping(unionType);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        addTypeMapping(structureType);
    }

    public static class PyBind11ModuleTemplateData extends CppTemplateData
    {
        public PyBind11ModuleTemplateData(TemplateDataContext context,
                Root root, Map<PackageName, PyBind11TemplateData> packageMapping)
        {
            super(context);

            rootPackageName = root.getRootPackage().getPackageName().isEmpty() ?
                    "" : root.getRootPackage().getPackageName().getIdList().get(0);
            for (Map.Entry<PackageName, PyBind11TemplateData> packageMappingEntry : packageMapping.entrySet())
            {
                final PackageName packageName = packageMappingEntry.getKey();
                if (packageName.getIdList().size() <= 1)
                {
                    // empty package should be also covered here
                    if (packageName.isEmpty())
                    {
                        packageIncludes.add(PYBIND11_FILENAME_ROOT + ".h");
                        packagePrefixes.add("");
                    }
                    else
                    {
                        packageIncludes.add(packageName.toString() + "/" + PYBIND11_FILENAME_ROOT + ".h");
                        packagePrefixes.add(packageName.toString() + "::");
                    }
                }
            }
        }

        public String getRootPackageName()
        {
            return rootPackageName;
        }

        public List<String> getPackageIncludes()
        {
            return packageIncludes;
        }

        public List<String> getPackagePrefixes()
        {
            return packagePrefixes;
        }

        private final String rootPackageName;
        private final List<String> packageIncludes = new ArrayList<String>();
        private final List<String> packagePrefixes = new ArrayList<String>();
    }

    public static class SetupPyTemplateData extends CppTemplateData
    {
        public SetupPyTemplateData(TemplateDataContext context, Root root)
        {
            super(context);
            this.rootPackageName = root.getRootPackage().getPackageName().isEmpty() ?
                    "" : root.getRootPackage().getPackageName().getIdList().get(0);
        }

        public String getRootPackageName()
        {
            return rootPackageName;
        }

        private final String rootPackageName;
    }

    private void addEmptyPackageMapping() throws ZserioExtensionException
    {
        if (!packageMapping.isEmpty())
            throw new ZserioExtensionException("PyBind11Emitter: Empty package shall be first!");

        packageMapping.put(PackageName.EMPTY, new PyBind11TemplateData(
                getTemplateDataContext(), PackageName.EMPTY));
    }

    private void addPackageMapping(PackageName mappedPackageName)
    {
        PyBind11TemplateData prevPackageTemplateData = packageMapping.get(PackageName.EMPTY);
        final PackageName.Builder currentPackageNameBuilder = new PackageName.Builder();
        final TemplateDataContext context = getTemplateDataContext();
        for (String id : mappedPackageName.getIdList())
        {
            currentPackageNameBuilder.addId(id);
            PyBind11TemplateData pybind11EmitterTemplateData =
                    packageMapping.get(currentPackageNameBuilder.get());
            if (pybind11EmitterTemplateData == null)
            {
                pybind11EmitterTemplateData = new PyBind11TemplateData(
                        context, currentPackageNameBuilder.get());
                packageMapping.put(currentPackageNameBuilder.get(), pybind11EmitterTemplateData);
            }

            if (prevPackageTemplateData != null)
                prevPackageTemplateData.addSubpackage(currentPackageNameBuilder.get());
            prevPackageTemplateData = pybind11EmitterTemplateData;
        }
    }

    private void addTypeMapping(ZserioType zserioType) throws ZserioExtensionException
    {
        final CppNativeType nativeType =
                getTemplateDataContext().getCppNativeMapper().getCppType(zserioType);
        addCppTypeMapping(nativeType, zserioType);
    }

    private void addConstantMapping(Constant constant) throws ZserioExtensionException
    {
        final CppNativeSymbol nativeSymbol =
                getTemplateDataContext().getCppNativeMapper().getCppSymbol(constant);
        addCppSymbolMapping(nativeSymbol);
    }

    private void addCppTypeMapping(CppNativeType nativeType, ZserioType baseType)
            throws ZserioExtensionException
    {
        final PyBind11TemplateData packageTemplateData = packageMapping.get(nativeType.getPackageName());
        if (packageTemplateData == null)
            throw new ZserioExtensionException("PyBind11Emitter: Package not yet mapped!");

        packageTemplateData.addCppType(nativeType, baseType);
    }

    private void addCppSymbolMapping(CppNativeSymbol nativeSymbol) throws ZserioExtensionException
    {
        final PyBind11TemplateData packageTemplateData = packageMapping.get(nativeSymbol.getPackageName());
        if (packageTemplateData == null)
            throw new ZserioExtensionException("PyBind11Emitter: Package not yet mapped!");

        packageTemplateData.addCppSymbol(nativeSymbol);
    }

    private static final String PYBIND11_HEADER_TEMPLATE = "ZserioPyBind11.h.ftl";
    private static final String PYBIND11_SOURCE_TEMPLATE = "ZserioPyBind11.cpp.ftl";
    private static final String PYBIND11_MODULE_SOURCE_TEMPLATE = "ZserioPyBind11Module.cpp.ftl";
    private static final String SETUP_PY_TEMPLATE = "zserio_setup.py.ftl";
    private static final String PYBIND11_FILENAME_ROOT = "ZserioPyBind11";
    private static final String PYBIND11_MODULE_FILENAME_ROOT = "ZserioPyBind11Module";
    private static final String SETUP_PY_FILENAME_ROOT = "zserio_setup";

    private final TreeMap<PackageName, PyBind11TemplateData> packageMapping =
            new TreeMap<PackageName, PyBind11TemplateData>();
}
