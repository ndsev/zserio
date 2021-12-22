package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zserio.ast.TemplateArgument;
import zserio.ast.TypeReference;
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioTypeUtil;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for template instantiations.
 */
public class TemplateInstantiationTemplateData
{
    public TemplateInstantiationTemplateData(TemplateDataContext context, ZserioTemplatableType template,
            List<TemplateArgument> templateArguments, IncludeCollector includeCollector)
                    throws ZserioExtensionException
    {
        templateName = ZserioTypeUtil.getFullName(template);
        templateArgumentTypeInfos = new ArrayList<NativeTypeInfoTemplateData>();
        final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
        for (TemplateArgument templateArgument : templateArguments)
        {
            final CppNativeType argumentNativeType = cppNativeMapper.getCppType(
                    templateArgument.getTypeReference());
            templateArgumentTypeInfos.add(new NativeTypeInfoTemplateData(
                    argumentNativeType, templateArgument.getTypeReference()));
            if (context.getWithTypeInfoCode())
            {
                // includes of template arguments types are needed only in typeInfo
                includeCollector.addCppIncludesForType(argumentNativeType);
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
            ZserioTemplatableType templatable, IncludeCollector includeCollector)
                    throws ZserioExtensionException
    {
        if (templatable.getTemplate() == null)
            return null;

        final Iterator<TypeReference> instantiationReferenceIterator =
                templatable.getInstantiationReferenceStack().iterator();
        if (!instantiationReferenceIterator.hasNext())
            return null; // should not occur

        return new TemplateInstantiationTemplateData(context, templatable.getTemplate(),
                instantiationReferenceIterator.next().getTemplateArguments(), includeCollector);
    }

    private final String templateName;
    private final List<NativeTypeInfoTemplateData> templateArgumentTypeInfos;
}
