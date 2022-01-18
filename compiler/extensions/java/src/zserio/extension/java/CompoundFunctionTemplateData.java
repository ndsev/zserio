package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.TypeReference;
import zserio.ast.Function;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for compound functions.
 */
public final class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(JavaNativeMapper javaNativeMapper, CompoundType compoundType,
            ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<Function> functionList = compoundType.getFunctions();
        for (Function compoundFunction : functionList)
        {
            compoundFunctionList.add(new CompoundFunction(javaNativeMapper, compoundFunction,
                    javaExpressionFormatter));
        }
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(JavaNativeMapper javaNativeMapper, Function function,
                ExpressionFormatter javaExpressionFormatter) throws ZserioExtensionException
        {
            final TypeReference returnTypeReference = function.getReturnTypeReference();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(returnTypeReference);
            returnTypeInfo = new NativeTypeInfoTemplateData(nativeType, returnTypeReference);
            schemaName = function.getName();
            name = AccessorNameFormatter.getFunctionName(function);
            resultExpression = javaExpressionFormatter.formatGetter(function.getResultExpression());
        }

        public NativeTypeInfoTemplateData getReturnTypeInfo()
        {
            return returnTypeInfo;
        }

        public String getSchemaName()
        {
            return schemaName;
        }

        public String getName()
        {
            return name;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        private final NativeTypeInfoTemplateData returnTypeInfo;
        private final String schemaName;
        private final String name;
        private final String resultExpression;
    }

    private final List<CompoundFunction>    compoundFunctionList;
}
