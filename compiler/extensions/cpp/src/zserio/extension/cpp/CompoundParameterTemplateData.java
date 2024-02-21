package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.DocComment;
import zserio.ast.Parameter;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.CompoundFieldTemplateData.NumBytes;
import zserio.extension.cpp.types.CppNativeType;

/**
 * FreeMarker template data for compound parameters.
 */
public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(TemplateDataContext context, CompoundType compoundType,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        final List<DocComment> compoundDocComments = compoundType.getDocComments();
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            final CompoundParameter parameter = new CompoundParameter(
                    context, compoundParameterType, includeCollector, compoundDocComments);

            compoundParameterList.add(parameter);
        }
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public static final class CompoundParameter
    {
        public CompoundParameter(TemplateDataContext context, Parameter parameter,
                IncludeCollector includeCollector, List<DocComment> compoundDocComments)
                throws ZserioExtensionException
        {
            final TypeReference parameterTypeReference = parameter.getTypeReference();
            final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
            final CppNativeType cppNativeType = cppNativeMapper.getCppType(parameterTypeReference);
            includeCollector.addHeaderIncludesForType(cppNativeType);

            final ZserioType parameterBaseType = parameterTypeReference.getBaseTypeReference().getType();

            name = parameter.getName();
            numBytes = new NumBytes(0);
            usesSharedPointer = (parameterBaseType instanceof CompoundType) &&
                    CompoundFieldTemplateData.isAboveTreshold(
                            context, (CompoundType)parameterBaseType, numBytes);

            typeInfo = new NativeTypeInfoTemplateData(cppNativeType, parameterTypeReference);
            getterName = AccessorNameFormatter.getGetterName(parameter);

            final List<DocComment> paramDocComments = new ArrayList<DocComment>();
            for (DocComment compoundDocComment : compoundDocComments)
            {
                final DocComment paramDocComment = compoundDocComment.findParamDoc(name);
                if (paramDocComment != null)
                    paramDocComments.add(paramDocComment);
            }
            docComments = DocCommentsDataCreator.createData(context, paramDocComments);
        }

        public String getName()
        {
            return name;
        }

        public NumBytes getNumBytes()
        {
            return numBytes;
        }

        public boolean getUsesSharedPointer()
        {
            return usesSharedPointer;
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
        private final NumBytes numBytes;
        private final boolean usesSharedPointer;
        private final NativeTypeInfoTemplateData typeInfo;
        private final String getterName;
        private final DocCommentsTemplateData docComments;
    }

    private final List<CompoundParameter> compoundParameterList;
}
