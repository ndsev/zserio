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
import zserio.ast.ZserioType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.symbols.PythonNativeSymbol;
import zserio.tools.ZserioToolPrinter;

/**
 * Modules clash checker.
 *
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
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        addSymbol(constant, pythonNativeMapper.getPythonSymbol(constant));
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        addZserioType(subtype);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        addZserioType(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        addZserioType(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        addZserioType(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        addZserioType(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        addZserioType(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        addZserioType(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        addZserioType(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType service) throws ZserioExtensionException
    {
        addZserioType(service);
    }

    @Override
    public void beginPubsub(PubsubType pubsub) throws ZserioExtensionException
    {
        addZserioType(pubsub);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {
        // instantiate type doesn't have it's own emitter since the instantiations are generated
        // by appropriate emitters for concrete templatable types, so we do not need to check it here
    }

    private void addZserioType(ZserioType zserioType) throws ZserioExtensionException
    {
        addSymbol(zserioType, pythonNativeMapper.getPythonType(zserioType));
    }

    private void addSymbol(PackageSymbol packageSymbol, PythonNativeSymbol pythonNativeSymbol)
            throws ZserioExtensionException
    {
        final String moduleName = pythonNativeSymbol.getModuleName();
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

    private final PythonNativeMapper pythonNativeMapper = new PythonNativeMapper();
    private final Map<String, PackageSymbol> packageSymbolMap = new HashMap<String, PackageSymbol>();
}
