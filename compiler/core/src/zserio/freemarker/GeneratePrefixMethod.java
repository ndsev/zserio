package zserio.freemarker;

import java.util.List;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class GeneratePrefixMethod implements TemplateMethodModelEx
{
    public GeneratePrefixMethod(String freemarkerName, String joiner)
    {
        this.freemarkerName = freemarkerName;
        this.joiner = joiner;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object exec(@SuppressWarnings("rawtypes") List args) throws TemplateModelException
    {
        return execImpl((List<TemplateModel>)args);
    }

    private Object execImpl(List<TemplateModel> args) throws TemplateModelException
    {
        if (args.size() != 1)
        {
            throw new TemplateModelException(freemarkerName + " requires exactly one argument");
        }

        TemplateModel model = args.get(0);
        if (!(model instanceof TemplateScalarModel))
        {
            throw new TemplateModelException(freemarkerName + " requires one string argument");
        }

        final TemplateScalarModel stringModel = (TemplateScalarModel)model;
        final String string = stringModel.getAsString();
        if (string != null && string.length() > 0)
        {
            return new SimpleScalar(string + joiner);
        }

        // empty package name -> return an empty string
        return SimpleScalar.EMPTY_STRING;
    }

    final String freemarkerName;
    final String joiner;
}
