package zserio.extension.python;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zserio.ast.TemplateArgument;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for template instantiations.
 */
public final class TemplateInstantiationTemplateData
{
    public TemplateInstantiationTemplateData(TemplateDataContext context, ZserioTemplatableType template,
            List<TemplateArgument> templateArguments, ImportCollector importCollector)
                    throws ZserioExtensionException
    {
        templateName = ZserioTypeUtil.getFullName(template);
        final PythonNativeMapper pythonNativeMapper = context.getPythonNativeMapper();
        templateArgumentTypeInfos = new ArrayList<NativeTypeInfoTemplateData>();
        for (TemplateArgument templateArgument : templateArguments)
        {
            final TypeReference argumentTypeReference = templateArgument.getTypeReference();
            final PythonNativeType argumentNativeType = pythonNativeMapper.getPythonType(argumentTypeReference);
            templateArgumentTypeInfos.add(new NativeTypeInfoTemplateData(argumentNativeType,
                    argumentTypeReference));
            if (context.getWithTypeInfoCode())
            {
                // imports of template arguments types are needed only in type_info
                importCollector.importType(argumentNativeType);
            }
        }
    }

    public String getTemplateName()
    {
        return templateName;
    }

    public Iterable<NativeTypeInfoTemplateData> getTemplateArgumentTypeInfos()
    {
        return templateArgumentTypeInfos;
    }

    static TemplateInstantiationTemplateData create(TemplateDataContext context,
            ZserioTemplatableType templatable, ImportCollector importCollector) throws ZserioExtensionException
    {
        if (templatable.getTemplate() == null)
            return null;

        final Iterator<TypeReference> instantiationReferenceIterator =
                templatable.getInstantiationReferenceStack().iterator();
        if (!instantiationReferenceIterator.hasNext())
            return null; // should not occur

        return new TemplateInstantiationTemplateData(context, templatable.getTemplate(),
                instantiationReferenceIterator.next().getTemplateArguments(), importCollector);
    }

    private final String templateName;
    private final List<NativeTypeInfoTemplateData> templateArgumentTypeInfos;
}
