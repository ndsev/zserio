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
            isConstStringReturnType = function.getResultExpression().getStringValue() != null;
            if (returnTypeReference.getBaseTypeReference().getType() instanceof StringType)
            {
                // we have to return strings as StringView because we are not able to find out whether
                // the expression always leads to some field or whether it contains any string constant
                // (the expression can contain e.g. another function call, constant, etc.)
                final NativeStringViewType nativeStringViewType = cppNativeMapper.getStringViewType();
                includeCollector.addHeaderIncludesForType(nativeStringViewType);
                returnTypeName = nativeStringViewType.getFullName();
                returnArgumentTypeName = nativeStringViewType.getArgumentTypeName();
                returnTypeInfo = new TypeInfoTemplateData(returnTypeReference, nativeStringViewType);

                if (isConstStringReturnType)
                {
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
                returnTypeName = returnNativeType.getFullName();
                returnArgumentTypeName = returnNativeType.getArgumentTypeName();
                returnTypeInfo = new TypeInfoTemplateData(returnTypeReference, returnNativeType);
                resultExpression = cppExpressionFormatter.formatGetter(function.getResultExpression());
                isSimpleReturnType = returnNativeType.isSimpleType();
            }

            schemaName = function.getName();
            name = AccessorNameFormatter.getFunctionName(function);
        }

        public String getReturnTypeName()
        {
            return returnTypeName;
        }

        public String getReturnArgumentTypeName()
        {
            return returnArgumentTypeName;
        }

        public TypeInfoTemplateData getReturnTypeInfo()
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

        public boolean getIsSimpleReturnType()
        {
            return isSimpleReturnType;
        }

        public String getSchemaName()
        {
            return schemaName;
        }

        public String getName()
        {
            return name;
        }

        private final String returnTypeName;
        private final String returnArgumentTypeName;
        private final TypeInfoTemplateData returnTypeInfo;
        private final String resultExpression;
        private final boolean isConstStringReturnType;
        private final boolean isSimpleReturnType;
        private final String schemaName;
        private final String name;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
