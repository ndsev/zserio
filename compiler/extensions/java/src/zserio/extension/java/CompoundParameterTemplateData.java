package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.DocComment;
import zserio.ast.Parameter;
import zserio.ast.TypeReference;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for compound parameters.
 */
public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        compoundName = compoundType.getName();

        final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        final List<DocComment> compoundDocComments = compoundType.getDocComments();
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            final CompoundParameter parameter = new CompoundParameter(
                    context, javaNativeMapper, compoundParameterType, compoundDocComments);
            compoundParameterList.add(parameter);
        }
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public String getCompoundName()
    {
        return compoundName;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(TemplateDataContext context, JavaNativeMapper javaNativeMapper,
                Parameter parameter, List<DocComment> compoundDocComments) throws ZserioExtensionException
        {
            name = parameter.getName();
            final TypeReference referencedType = parameter.getTypeReference();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(referencedType);
            typeInfo = new NativeTypeInfoTemplateData(nativeType, referencedType);
            getterName = AccessorNameFormatter.getGetterName(parameter);

            final List<DocComment> paramDocComments = new ArrayList<DocComment>();
            for (DocComment compoundDocComment : compoundDocComments)
            {
                final DocComment paramDocComment = compoundDocComment.findParamDoc(name);
                if (paramDocComment != null)
                    paramDocComments.add(paramDocComment);
            }
            docComments = paramDocComments.isEmpty()
                    ? null : new DocCommentsTemplateData(context, paramDocComments);
        }

        public String getName()
        {
            return name;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public String getGetterName()
        {
            return getterName;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final NativeTypeInfoTemplateData typeInfo;
        private final String getterName;
        private final DocCommentsTemplateData docComments;
    }

    private final String compoundName;
    private final List<CompoundParameter> compoundParameterList;
}
