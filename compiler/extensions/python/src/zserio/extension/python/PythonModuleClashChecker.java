package zserio.extension.python;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PackageSymbol;
import zserio.ast.PubsubType;
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
import zserio.tools.ZserioToolPrinter;

/**
 * Checks that Python modules generated for each package symbol do not clash on the file system.
 * Note that module names are created from package symbol names by converting to snake case to conform PEP-8.
 */
class PythonModuleClashChecker extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        // we need to check module names of template instantiations
        return true;
    }

    @Override
    public void endPackage(Package packageToken) throws ZserioExtensionException
    {
        packageSymbolMap.clear();
    }

    @Override
    public void beginConst(Constant constType) throws ZserioExtensionException
    {
        addSymbol(constType);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        addSymbol(subtype);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        addSymbol(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        addSymbol(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        addSymbol(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        addSymbol(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        addSymbol(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        addSymbol(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        addSymbol(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType service) throws ZserioExtensionException
    {
        addSymbol(service);
    }

    @Override
    public void beginPubsub(PubsubType pubsub) throws ZserioExtensionException
    {
        addSymbol(pubsub);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {
        // instantiate type doesn't have it's own emitter since the instantiations are generated
        // by appropriate emitters for concrete templatable types, so we do not need to check it here
    }

    private void addSymbol(PackageSymbol packageSymbol) throws ZserioExtensionException
    {
        final String moduleName = PythonSymbolConverter.packageSymbolToModuleName(packageSymbol.getName());
        final PackageSymbol clashingPackageSymbol = packageSymbolMap.put(moduleName, packageSymbol);
        if (clashingPackageSymbol != null)
        {
            printErrorContext(packageSymbol);

            ZserioToolPrinter.printError(packageSymbol.getLocation(),
                    "Module '" + moduleName + "' generated for package symbol '" + packageSymbol.getName() +
                    "' clashes with module generated for package symbol '" + clashingPackageSymbol.getName() +
                    "' defined at " + clashingPackageSymbol.getLocation().getLine() + ":" +
                    clashingPackageSymbol.getLocation().getColumn() + "!");
            throw new ZserioExtensionException("Module name clashing detected!");
        }
    }

    private static void printErrorContext(PackageSymbol packageSymbol)
    {
        if (packageSymbol instanceof ZserioTemplatableType)
        {
            final ZserioTemplatableType templatable = (ZserioTemplatableType)packageSymbol;
            if (templatable.getTemplate() != null)
            {
                final ArrayDeque<TypeReference> reversedStack = new ArrayDeque<TypeReference>();
                for (TypeReference instantiationReference : templatable.getInstantiationReferenceStack())
                    reversedStack.push(instantiationReference);
                for (TypeReference instantiationReference : reversedStack)
                {
                    ZserioToolPrinter.printError(instantiationReference.getLocation(),
                            "In instantiation of '" + instantiationReference.getReferencedTypeName() +
                            "' required from here");
                }
            }
        }
    }

    private final Map<String, PackageSymbol> packageSymbolMap = new HashMap<String, PackageSymbol>();
}
