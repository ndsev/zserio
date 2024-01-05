package zserio.extension.python;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.PackageSymbol;
import zserio.ast.PubsubType;
import zserio.ast.Root;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioTemplatableType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.symbols.PythonNativeSymbol;
import zserio.tools.ZserioToolPrinter;

/**
 * Package with module clash checker.
 *
 * Checks that Python modules generated for each package symbol do not clash with generated packages.
 * Note that module names are created from package symbol names by converting to snake case to conform PEP-8.
 *
 * Note that this clashing doesn't cause problems on the file system, but it makes it almost impossible
 * to import the clashing modules since packages are always preferred by the Python import system.
 */
final class PythonPackageWithModuleClashChecker extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        // we need to check module names of template instantiations
        return true;
    }

    @Override
    public void endRoot(Root root) throws ZserioExtensionException
    {
        for (Map.Entry<PackageName, Set<PackageSymbol>> packageEntries : packageToPackageSymbolMap.entrySet())
        {
            final PackageName packageName = packageEntries.getKey();
            final Set<PackageSymbol> packageSymbols = packageEntries.getValue();
            final Set<String> childPackageIds = packageToChildPackageIdMap.get(packageName);
            if (childPackageIds != null)
            {
                for (PackageSymbol packageSymbol : packageSymbols)
                {
                    final PythonNativeSymbol nativeSymbol = pythonNativeMapper.getPythonSymbol(packageSymbol);
                    final String moduleName = nativeSymbol.getModuleName();
                    if (childPackageIds.contains(moduleName))
                    {
                        printErrorContext(packageSymbol);
                        ZserioToolPrinter.printError(packageSymbol.getLocation(),
                                "Module '" + PythonFullNameFormatter.getModuleFullName(nativeSymbol) +
                                        "' generated for package symbol '" + packageSymbol.getName() +
                                        "' clashes with equally named generated package!");

                        throw new ZserioExtensionException("Package with module name clashing detected!");
                    }
                }
            }
        }
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        PackageName.Builder packageNameBuilder = new PackageName.Builder();
        PackageName prevPackageName = PackageName.EMPTY;
        for (String id : pkg.getPackageName().getIdList())
        {
            packageNameBuilder.addId(id);
            Set<String> childPackageIds = packageToChildPackageIdMap.get(prevPackageName);
            if (childPackageIds == null)
            {
                childPackageIds = new HashSet<String>();
                packageToChildPackageIdMap.put(prevPackageName, childPackageIds);
            }

            prevPackageName = packageNameBuilder.get();
            childPackageIds.add(id);
        }
    }

    @Override
    public void beginConst(Constant constType) throws ZserioExtensionException
    {
        addPackageSymbol(constType);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        addPackageSymbol(subtype);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        addPackageSymbol(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        addPackageSymbol(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        addPackageSymbol(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        addPackageSymbol(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        addPackageSymbol(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        addPackageSymbol(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        addPackageSymbol(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType service) throws ZserioExtensionException
    {
        addPackageSymbol(service);
    }

    @Override
    public void beginPubsub(PubsubType pubsub) throws ZserioExtensionException
    {
        addPackageSymbol(pubsub);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {
        // instantiate type doesn't have it's own emitter since the instantiations are generated
        // by appropriate emitters for concrete templatable types, so we do not need to check it here
    }

    private void addPackageSymbol(PackageSymbol packageSymbol)
    {
        final PackageName packageName = packageSymbol.getPackage().getPackageName();
        Set<PackageSymbol> childSymbols = packageToPackageSymbolMap.get(packageName);
        if (childSymbols == null)
        {
            childSymbols = new HashSet<PackageSymbol>();
            packageToPackageSymbolMap.put(packageName, childSymbols);
        }

        childSymbols.add(packageSymbol);
    }

    private static void printErrorContext(PackageSymbol packageSymbol)
    {
        if (packageSymbol instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType templatable = (ZserioTemplatableType)packageSymbol;
            if (templatable.getTemplate() != null)
            {
                for (TypeReference instantiationReference :
                        templatable.getReversedInstantiationReferenceStack())
                {
                    ZserioToolPrinter.printError(instantiationReference.getLocation(),
                            "    In instantiation of '" + instantiationReference.getReferencedTypeName() +
                                    "' required from here");
                }
            }
        }
    }

    private final PythonNativeMapper pythonNativeMapper = new PythonNativeMapper();
    private final Map<PackageName, Set<String>> packageToChildPackageIdMap =
            new HashMap<PackageName, Set<String>>();
    private final Map<PackageName, Set<PackageSymbol>> packageToPackageSymbolMap =
            new HashMap<PackageName, Set<PackageSymbol>>();
};
