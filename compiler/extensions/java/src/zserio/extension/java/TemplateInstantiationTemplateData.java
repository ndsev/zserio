package zserio.extension.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zserio.ast.TemplateArgument;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for template instantiations.
 */
public class TemplateInstantiationTemplateData
{
    public TemplateInstantiationTemplateData(TemplateDataContext context, ZserioTemplatableType template,
            List<TemplateArgument> templateArguments) throws ZserioExtensionException
    {
        templateName = ZserioTypeUtil.getFullName(template);
        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        templateArgumentTypeInfos = new ArrayList<NativeTypeInfoTemplateData>();
        for (TemplateArgument templateArgument : templateArguments)
        {
            final TypeReference argumentReferencedType = templateArgument.getTypeReference();
            final JavaNativeType argumentNativeType = javaNativeMapper.getJavaType(
                    argumentReferencedType);
            this.templateArgumentTypeInfos.add(new NativeTypeInfoTemplateData(argumentNativeType,
                    argumentReferencedType));
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
            ZserioTemplatableType templatable) throws ZserioExtensionException
    {
        if (templatable.getTemplate() == null)
            return null;

        final Iterator<TypeReference> instantiationReferenceIterator =
                templatable.getInstantiationReferenceStack().iterator();
        if (!instantiationReferenceIterator.hasNext())
            return null; // should not occur

        return new TemplateInstantiationTemplateData(context, templatable.getTemplate(),
                instantiationReferenceIterator.next().getTemplateArguments());
    }

    private final String templateName;
    private final List<NativeTypeInfoTemplateData> templateArgumentTypeInfos;
}
