package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.TypeReference;
import zserio.ast.ZserioType;
import zserio.ast.Function;
import zserio.ast.StringType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeStringViewType;

/**
 * FreeMarker template data for compound functions.
 */
public class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(CppNativeMapper cppNativeMapper, CompoundType compoundType,
            ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                    throws ZserioExtensionException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<Function> functionList = compoundType.getFunctions();
        for (Function function : functionList)
        {
            compoundFunctionList.add(new CompoundFunction(function, cppNativeMapper,
                    cppExpressionFormatter, includeCollector));
        }
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(Function function, CppNativeMapper cppNativeMapper,
                ExpressionFormatter cppExpressionFormatter, IncludeCollector includeCollector)
                        throws ZserioExtensionException
        {
            final TypeReference returnTypeReference = function.getReturnTypeReference();
            final ZserioType returnBaseType = returnTypeReference.getBaseTypeReference().getType();
            isConstStringReturnType = function.getResultExpression().getStringValue() != null;
            if (returnBaseType instanceof StringType)
            {
                // we have to return strings as StringView because we are not able to find out whether
                // the expression always leads to some field or whether it contains any string constant
                // (the expression can contain e.g. another function call, constant, etc.)
                final NativeStringViewType nativeStringViewType = cppNativeMapper.getStringViewType();
                includeCollector.addHeaderIncludesForType(nativeStringViewType);
                returnTypeInfo = new NativeTypeInfoTemplateData(nativeStringViewType, returnTypeReference);

                if (isConstStringReturnType)
                {
                    resultExpression = nativeStringViewType.formatLiteral(
                            function.getResultExpression().getStringValue());
                }
                else
                {
                    resultExpression = cppExpressionFormatter.formatGetter(function.getResultExpression());
                }
            }
            else
            {
                final CppNativeType returnNativeType = cppNativeMapper.getCppType(returnTypeReference);
                includeCollector.addHeaderIncludesForType(returnNativeType);
                returnTypeInfo = new NativeTypeInfoTemplateData(returnNativeType, returnTypeReference);
                resultExpression = cppExpressionFormatter.formatGetter(function.getResultExpression());
            }

            schemaName = function.getName();
            name = AccessorNameFormatter.getFunctionName(function);
        }

        public NativeTypeInfoTemplateData getReturnTypeInfo()
        {
            return returnTypeInfo;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        public boolean getIsConstStringReturnType()
        {
            return isConstStringReturnType;
        }

        public String getSchemaName()
        {
            return schemaName;
        }

        public String getName()
        {
            return name;
        }

        private final NativeTypeInfoTemplateData returnTypeInfo;
        private final String resultExpression;
        private final boolean isConstStringReturnType;
        private final String schemaName;
        private final String name;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
