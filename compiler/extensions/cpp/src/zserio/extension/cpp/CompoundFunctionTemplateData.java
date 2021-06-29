package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.TypeReference;
import zserio.ast.Function;
import zserio.ast.StringType;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.cpp.types.CppNativeType;
import zserio.extension.cpp.types.NativeStringViewType;

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
            if (returnTypeReference.getBaseTypeReference().getType() instanceof StringType)
            {
                final NativeStringViewType nativeStringViewType = cppNativeMapper.getStringViewType();
                includeCollector.addHeaderIncludesForType(nativeStringViewType);
                returnTypeName = nativeStringViewType.getFullName();

                if (function.getResultExpression().getStringValue() != null)
                {
                    // it's a constant string
                    resultExpression = nativeStringViewType.formatLiteral(
                            function.getResultExpression().getStringValue());
                }
                else
                {
                    resultExpression = cppExpressionFormatter.formatGetter(function.getResultExpression());
                }
                isSimpleReturnType = nativeStringViewType.isSimpleType();
            }
            else
            {
                final CppNativeType returnNativeType = cppNativeMapper.getCppType(returnTypeReference);
                includeCollector.addHeaderIncludesForType(returnNativeType);
                returnTypeName = returnNativeType.getArgumentTypeName();
                resultExpression = cppExpressionFormatter.formatGetter(function.getResultExpression());
                isSimpleReturnType = returnNativeType.isSimpleType();
            }

            name = AccessorNameFormatter.getFunctionName(function);
        }

        public String getReturnTypeName()
        {
            return returnTypeName;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        public boolean getIsSimpleReturnType()
        {
            return isSimpleReturnType;
        }

        public String getName()
        {
            return name;
        }

        private final String returnTypeName;
        private final String resultExpression;
        private final boolean isSimpleReturnType;
        private final String name;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
