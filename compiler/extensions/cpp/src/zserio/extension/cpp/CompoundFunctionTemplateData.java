package zserio.extension.cpp;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.DocComment;
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
    public CompoundFunctionTemplateData(TemplateDataContext context, CompoundType compoundType,
            IncludeCollector includeCollector) throws ZserioExtensionException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<Function> functionList = compoundType.getFunctions();
        for (Function function : functionList)
        {
            compoundFunctionList.add(new CompoundFunction(context, function, includeCollector));
        }
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static class CompoundFunction
    {
        public CompoundFunction(TemplateDataContext context, Function function,
                IncludeCollector includeCollector) throws ZserioExtensionException
        {
            final CppNativeMapper cppNativeMapper = context.getCppNativeMapper();
            final TypeReference returnTypeReference = function.getReturnTypeReference();
            final ZserioType returnBaseType = returnTypeReference.getBaseTypeReference().getType();
            if (returnBaseType instanceof StringType)
            {
                // we have to return strings as StringView because we are not able to find out whether
                // the expression always leads to some field or whether it contains any string constant
                // (the expression can contain e.g. another function call, constant, etc.)
                final NativeStringViewType nativeStringViewType = cppNativeMapper.getStringViewType();
                includeCollector.addHeaderIncludesForType(nativeStringViewType);
                returnTypeInfo = new NativeTypeInfoTemplateData(nativeStringViewType, returnTypeReference);
            }
            else
            {
                final CppNativeType returnNativeType = cppNativeMapper.getCppType(returnTypeReference);
                includeCollector.addHeaderIncludesForType(returnNativeType);
                returnTypeInfo = new NativeTypeInfoTemplateData(returnNativeType, returnTypeReference);
            }

            final ExpressionFormatter cppExpressionFormatter = context.getExpressionFormatter(includeCollector);
            resultExpression = cppExpressionFormatter.formatGetter(function.getResultExpression());
            schemaName = function.getName();
            name = AccessorNameFormatter.getFunctionName(function);

            final List<DocComment> functionDocComments = function.getDocComments();
            docComments = functionDocComments.isEmpty()
                    ? null : new DocCommentsTemplateData(context, functionDocComments);
        }

        public NativeTypeInfoTemplateData getReturnTypeInfo()
        {
            return returnTypeInfo;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        public String getSchemaName()
        {
            return schemaName;
        }

        public String getName()
        {
            return name;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final NativeTypeInfoTemplateData returnTypeInfo;
        private final String resultExpression;
        private final String schemaName;
        private final String name;
        private final DocCommentsTemplateData docComments;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
