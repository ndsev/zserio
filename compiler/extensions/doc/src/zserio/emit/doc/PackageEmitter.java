package zserio.emit.doc;


import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import antlr.collections.AST;
import zserio.ast.ZserioException;
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
    public void endRoot()
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
        packages.add(getPackageName());
    }


    public Set<String> getPackages()
    {
        return packages;
    }

}
