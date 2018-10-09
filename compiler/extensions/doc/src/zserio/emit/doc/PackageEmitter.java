package zserio.emit.doc;


import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import zserio.ast.Package;
import zserio.ast.Root;

import freemarker.template.Template;
import freemarker.template.TemplateException;



public class PackageEmitter extends DefaultHtmlEmitter
{
    private final Set<String> packages = new TreeSet<String>();


    public PackageEmitter(String outputPath)
    {
        super(outputPath);
    }


    @Override
    public void endRoot(Root root)
    {
        try
        {
            Template tpl = cfg.getTemplate("doc/package.html.ftl");
            openOutputFile(directory, "packages" + HTML_EXT);

            tpl.process(this, writer);
            writer.close();
        }
        catch (IOException exc)
        {
            throw new ZserioEmitDocException(exc);
        }
        catch (TemplateException exc)
        {
            throw new ZserioEmitDocException(exc);
        }
        finally
        {
            if (writer != null)
                writer.close();
        }
    }


    @Override
    public void beginPackage(Package packageToken)
    {
        super.beginPackage(packageToken);
        packages.add(getPackageName());
    }


    public Set<String> getPackages()
    {
        return packages;
    }

}
