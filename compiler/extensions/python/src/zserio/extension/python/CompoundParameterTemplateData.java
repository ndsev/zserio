package zserio.extension.python;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
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
        for (Parameter compoundParameterType : compoundParameterTypeList)
        {
            compoundParameterList.add(new CompoundParameter(
                    context, compoundParameterType, importCollector));
        }
    }

    public Iterable<CompoundParameter> getList()
    {
        return compoundParameterList;
    }

    public static class CompoundParameter
    {
        public CompoundParameter(TemplateDataContext context, Parameter parameter,
                ImportCollector importCollector) throws ZserioExtensionException
        {
            name = parameter.getName();
            snakeCaseName = PythonSymbolConverter.toLowerSnakeCase(name);
            final TypeReference parameterTypeReference = parameter.getTypeReference();
            final PythonNativeType nativeType = context.getPythonNativeMapper().getPythonType(
                    parameterTypeReference);
            importCollector.importType(nativeType);
            typeInfo = new TypeInfoTemplateData(parameterTypeReference, nativeType);
            pythonTypeName = PythonFullNameFormatter.getFullName(nativeType);
            propertyName = AccessorNameFormatter.getPropertyName(parameter);
        }

        public String getName()
        {
            return name;
        }

        public String getSnakeCaseName()
        {
            return snakeCaseName;
        }

        public TypeInfoTemplateData getTypeInfo()
        {
            return typeInfo;
        }

        public String getPythonTypeName()
        {
            return pythonTypeName;
        }

        public String getPropertyName()
        {
            return propertyName;
        }

        private final String name;
        private final String snakeCaseName;
        private final TypeInfoTemplateData typeInfo;
        private final String pythonTypeName;
        private final String propertyName;
    }

    private final List<CompoundParameter> compoundParameterList;
}
