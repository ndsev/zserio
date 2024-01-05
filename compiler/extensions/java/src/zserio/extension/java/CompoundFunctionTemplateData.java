package zserio.extension.java;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.CompoundType;
import zserio.ast.Function;
import zserio.ast.TypeReference;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;
import zserio.extension.java.types.JavaNativeType;

/**
 * FreeMarker template data for compound functions.
 */
public final class CompoundFunctionTemplateData
{
    public CompoundFunctionTemplateData(TemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        compoundFunctionList = new ArrayList<CompoundFunction>();
        final Iterable<Function> functionList = compoundType.getFunctions();
        for (Function compoundFunction : functionList)
            compoundFunctionList.add(new CompoundFunction(context, compoundFunction));
    }

    public Iterable<CompoundFunction> getList()
    {
        return compoundFunctionList;
    }

    public static final class CompoundFunction
    {
        public CompoundFunction(TemplateDataContext context, Function function) throws ZserioExtensionException
        {
            final TypeReference returnTypeReference = function.getReturnTypeReference();
            final JavaNativeMapper javaNativeMapper = context.getJavaNativeMapper();
            final JavaNativeType nativeType = javaNativeMapper.getJavaType(returnTypeReference);
            returnTypeInfo = new NativeTypeInfoTemplateData(nativeType, returnTypeReference);
            schemaName = function.getName();
            name = AccessorNameFormatter.getFunctionName(function);
            final ExpressionFormatter javaExpressionFormatter = context.getJavaExpressionFormatter();
            resultExpression = javaExpressionFormatter.formatGetter(function.getResultExpression());
            final ExpressionFormatter javaLambdaExpressionFormatter =
                    context.getJavaLambdaExpressionFormatter();
            lambdaResultExpression = javaLambdaExpressionFormatter.formatGetter(function.getResultExpression());
            docComments = DocCommentsDataCreator.createData(context, function);
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

        public String getLambdaResultExpression()
        {
            return lambdaResultExpression;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final NativeTypeInfoTemplateData returnTypeInfo;
        private final String schemaName;
        private final String name;
        private final String resultExpression;
        private final String lambdaResultExpression;
        private final DocCommentsTemplateData docComments;
    }

    private final List<CompoundFunction> compoundFunctionList;
}
