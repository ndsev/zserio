package zserio.extension.python;

import java.util.List;
import java.util.Locale;

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
import zserio.ast.UnionType;
import zserio.ast.ZserioTemplatableType;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ZserioToolPrinter;

/**
 * Checks that Python code generator will not produce any clashes with auto-generated API helpers.
 */
class PythonApiClashChecker extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        // we don't need to check instantiations since they can clash only if 'instantiate' keyword is used
        // and we check such clashes in beginInstantiateType
        return false;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        if (pkg.getPackageName().isEmpty())
        {
            // default package is present, we need to check also top level package id
            checkTopLevelPackageId = true;
        }
        else
        {
            final int startIdx = checkTopLevelPackageId ? 0 : 1;
            final List<String> idList = pkg.getPackageName().getIdList();
            for (int i = startIdx; i < idList.size(); ++i)
            {
                final String id = idList.get(i);
                if (id.equals(ApiEmitter.API_FILENAME_ROOT))
                {
                    ZserioToolPrinter.printError(pkg.getLocation(),
                            "Cannot generate python package '" + id +  "' for package '" +
                            pkg.getPackageName() + "', since it would clash with auto-generated '" +
                            apiOutputFileNameLowerCase + "'! Please choose different package name.");
                    throw new ZserioExtensionException("Clash in generated code detected!");
                }
            }
        }
    }

    @Override
    public void beginConst(Constant constant) throws ZserioExtensionException
    {
        checkPackageSymbolName(constant);
    }

    @Override
    public void beginSubtype(Subtype subtype) throws ZserioExtensionException
    {
        checkPackageSymbolName(subtype);
    }

    @Override
    public void beginStructure(StructureType structureType) throws ZserioExtensionException
    {
        checkPackageSymbolName(structureType);
    }

    @Override
    public void beginChoice(ChoiceType choiceType) throws ZserioExtensionException
    {
        checkPackageSymbolName(choiceType);
    }

    @Override
    public void beginUnion(UnionType unionType) throws ZserioExtensionException
    {
        checkPackageSymbolName(unionType);
    }

    @Override
    public void beginEnumeration(EnumType enumType) throws ZserioExtensionException
    {
        checkPackageSymbolName(enumType);
    }

    @Override
    public void beginBitmask(BitmaskType bitmaskType) throws ZserioExtensionException
    {
        checkPackageSymbolName(bitmaskType);
    }

    @Override
    public void beginSqlTable(SqlTableType sqlTableType) throws ZserioExtensionException
    {
        checkPackageSymbolName(sqlTableType);
    }

    @Override
    public void beginSqlDatabase(SqlDatabaseType sqlDatabaseType) throws ZserioExtensionException
    {
        checkPackageSymbolName(sqlDatabaseType);
    }

    @Override
    public void beginService(ServiceType service) throws ZserioExtensionException
    {
        checkPackageSymbolName(service);
    }

    @Override
    public void beginPubsub(PubsubType pubsub) throws ZserioExtensionException
    {
        checkPackageSymbolName(pubsub);
    }

    @Override
    public void beginInstantiateType(InstantiateType instantiateType) throws ZserioExtensionException
    {
        checkPackageSymbolName(instantiateType);
    }

    private void checkPackageSymbolName(PackageSymbol packageSymbol) throws ZserioExtensionException
    {
        if (packageSymbol instanceof ZserioTemplatableType)
        {
            ZserioTemplatableType templatable = (ZserioTemplatableType)packageSymbol;
            if (!templatable.getTemplateParameters().isEmpty())
                return; // do not check templates
        }

        final String outputFileName = PythonDefaultEmitter.getOutputFileName(packageSymbol.getName());
        if (outputFileName.toLowerCase(Locale.ENGLISH).equals(apiOutputFileNameLowerCase))
        {
            ZserioToolPrinter.printError(packageSymbol.getLocation(),
                    "Cannot generate python source '" + outputFileName +  "' for symbol '" +
                    packageSymbol.getName() + "', since it would clash with auto-generated '" +
                    apiOutputFileNameLowerCase + "'! Please choose different name.");
            throw new ZserioExtensionException("Clash in generated code detected!");
        }
    }

    private static String apiOutputFileNameLowerCase =
            PythonDefaultEmitter.getOutputFileName(ApiEmitter.API_FILENAME_ROOT).toLowerCase(Locale.ENGLISH);
    private boolean checkTopLevelPackageId = false;
}