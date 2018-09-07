package zserio.emit.doc;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import antlr.collections.AST;
import zserio.ast.ZserioException;
import zserio.ast.Package;
import zserio.ast.ZserioType;
import zserio.tools.PackageManager;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class OverviewEmitter extends DefaultHtmlEmitter
{
    public OverviewEmitter(String outputPath)
    {
        super(outputPath);
    }

    public Set<String> getPackageNames()
    {
        return packageNames;
    }

    @Override
    public void endRoot()
    {
        try
        {
            for (String pkgName: PackageManager.get().getPackageNames())
            {
                Package pkg = PackageManager.get().lookup(pkgName);
                for (String typeName : pkg.getLocalTypeNames())
                {
                    boolean isDoubleDefinedType = Boolean.TRUE.equals(doubleTypeNames.get(typeName));
                    ZserioType t = pkg.getLocalType(typeName);
                    LinkedType linkedType = new LinkedType(t, isDoubleDefinedType);
                    typeMap.put(getFullTypeName(typeName, pkg), linkedType);
                }
            }

            Template tpl = cfg.getTemplate("doc/overview.html.ftl");
            openOutputFile(directory, "overview" + HTML_EXT);

            tpl.process(this, writer);
        }
        catch (IOException exc)
        {
            throw new ZserioException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioException(exc);
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }

    @Override
    public void endPackage(AST p)
    {
        for (String typeName : currentPackage.getLocalTypeNames())
        {
            if (doubleTypeNames.containsKey(typeName))
                doubleTypeNames.put(typeName, true);
            else
                doubleTypeNames.put(typeName, false);
        }
        String pkgName = currentPackage.getPackageName();
        pkgName = pkgName.replace('.', '_');
        packageNames.add(pkgName);
    }

    public Collection<LinkedType> getTypes()
    {
        return typeMap.values();
    }

    private String getFullTypeName(String typeName, Package pkg)
    {
        if (pkg == PackageManager.get().defaultPackage)
            return typeName;

        return typeName + "." + pkg.getReversePackageName();
    }

    private final Map<String, LinkedType> typeMap = new TreeMap<String, LinkedType>();
    private final Map<String, Boolean> doubleTypeNames = new HashMap<String, Boolean>();
    private final HashSet<String> packageNames = new HashSet<String>();
}
