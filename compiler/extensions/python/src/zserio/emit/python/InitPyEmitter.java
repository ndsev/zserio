package zserio.emit.python;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.emit.common.PackageMapper;
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
        final PackageMapper packageMapper = getTemplateDataContext().getPythonPackageMapper();
        final PackageName mappedPackageName = packageMapper.getPackageName(zserioPackage);

        final PackageName.Builder topLevelFolder = new PackageName.Builder();
        final Iterator<String> mappedIdsIterator = mappedPackageName.getIdList().iterator();
        if (mappedIdsIterator.hasNext())
            topLevelFolder.addId(mappedIdsIterator.next());

        if (!topLevelFolders.contains(PackageName.EMPTY) && topLevelFolders.add(topLevelFolder.get()))
        {
            final Object templateData = new PythonTemplateData();
            processTemplate(INIT_PY_TEMPLATE, templateData, topLevelFolder.get(), INIT_PY_FILENAME_ROOT);
        }
    }

    private static final String INIT_PY_TEMPLATE = "__init__.py.ftl";
    private static final String INIT_PY_FILENAME_ROOT = "__init__";
    private final Set<PackageName> topLevelFolders = new HashSet<PackageName>();
}
