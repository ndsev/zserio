package zserio.emit.python;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.emit.common.ZserioEmitException;
import zserio.tools.Parameters;

public class InitPyEmitter extends PythonDefaultEmitter
{
    public InitPyEmitter (String outputPath, Parameters extensionParameters)
    {
        super(outputPath, extensionParameters);
    }

    @Override
    public void beginPackage(Package zserioPackage) throws ZserioEmitException
    {
        super.beginPackage(zserioPackage);

        final PackageName packageName = zserioPackage.getPackageName();
        if (packageName.isEmpty())
        {
            processPackage(packageName);
        }
        else
        {
            final PackageName.Builder packageNameBuilder = new PackageName.Builder();
            final Iterator<String> idsIterator = packageName.getIdList().iterator();
            while (idsIterator.hasNext())
            {
                packageNameBuilder.addId(idsIterator.next());

                final PackageName builtPackageName = packageNameBuilder.get();
                if (processedPackages.add(builtPackageName))
                    processPackage(builtPackageName);
            }
        }
    }

    private void processPackage(PackageName packageName) throws ZserioEmitException
    {
        final Object templateData = new PythonTemplateData(getTemplateDataContext());
        processTemplate(INIT_PY_TEMPLATE, templateData, packageName, INIT_PY_FILENAME_ROOT);
    }

    private static final String INIT_PY_TEMPLATE = "__init__.py.ftl";
    private static final String INIT_PY_FILENAME_ROOT = "__init__";
    private final Set<PackageName> processedPackages= new HashSet<PackageName>();
}
