package zserio.extension.python;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.extension.common.DefaultTreeWalker;
import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ZserioToolPrinter;

/**
 * Top level package name clash checker.
 *
 * Checks that top level packages don't clash with used Python modules or packages - e.g. 'typing'.
 *
 * Note: Regarding 'typing', consider if importing 'typing' in zserio runtime __int__.py could help.
 */
final class PythonTopLevelPackageClashChecker extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    @Override
    public void beginPackage(Package pkg) throws ZserioExtensionException
    {
        final PackageName packageName = pkg.getPackageName();
        if (!packageName.isEmpty())
        {
            final String topLevelId = packageName.getIdList().get(0);
            if (topLevelId.equals(TYPING_MODULE))
            {
                ZserioToolPrinter.printError(pkg.getLocation(),
                        "Top level package '" + topLevelId + "' clashes with Python '" + TYPING_MODULE +
                        "' module which is used by generated code.");
                throw new ZserioExtensionException("Top level package clash detected!");
            }
        }
    }

    private static final String TYPING_MODULE = "typing";
}