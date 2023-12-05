package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.DocComment;
import zserio.ast.Parameter;
import zserio.ast.TypeReference;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.python.types.PythonNativeType;

/**
 * FreeMarker template data for compound parameters, used from various template data.
 */
public final class CompoundParameterTemplateData
{
    public CompoundParameterTemplateData(TemplateDataContext context, CompoundType compoundType,
            ImportCollector importCollector) throws ZserioExtensionException
    {
        final List<Parameter> compoundParameterTypeList = compoundType.getTypeParameters();
        compoundParameterList = new ArrayList<CompoundParameter>(compoundParameterTypeList.size());
        final List<DocComment> compoundDocComments = compoundType.getDocComments();
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            compoundParameterList.add(new CompoundParameter(
                    context, compoundParameterType, importCollector, compoundDocComments));
        }
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public static final class CompoundParameter
    {
        public CompoundParameter(TemplateDataContext context, Parameter parameter,
                ImportCollector importCollector, List<DocComment> compoundDocComments)
                        throws ZserioExtensionException
        {
            name = parameter.getName();
            snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);
            final TypeReference parameterTypeReference = parameter.getTypeReference();
            final PythonNativeType nativeType = context.getPythonNativeMapper().getPythonType(
                    parameterTypeReference);
            importCollector.importType(nativeType);
            typeInfo = new NativeTypeInfoTemplateData(nativeType, parameterTypeReference);
            propertyName = AccessorNameFormatter.getPropertyName(parameter);

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

        public String getSnakeCaseName()
        {
            return snakeCaseName;
        }

        public NativeTypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final String name;
        private final String snakeCaseName;
        private final NativeTypeInfoTemplateData typeInfo;
        private final String propertyName;
        private final DocCommentsTemplateData docComments;
    }

    private final List<CompoundParameter> compoundParameterList;
}
