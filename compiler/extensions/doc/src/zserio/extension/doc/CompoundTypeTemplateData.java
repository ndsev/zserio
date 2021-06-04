package zserio.extension.doc;

import java.util.ArrayList;
import java.util.List;

import zserio.ast.ArrayInstantiation;
import zserio.ast.CompoundType;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.Function;
import zserio.ast.Parameter;
import zserio.ast.ParameterizedTypeInstantiation;
import zserio.ast.TypeInstantiation;
import zserio.ast.SqlConstraint;
import zserio.ast.TemplateParameter;
import zserio.extension.common.ExpressionFormatter;
import zserio.extension.common.ZserioExtensionException;

/**
 * Base FreeMarker template data for compounds in the package used by Package emitter.
 */
public class CompoundTypeTemplateData extends PackageTemplateDataBase
{
    public CompoundTypeTemplateData(PackageTemplateDataContext context, CompoundType compoundType)
            throws ZserioExtensionException
    {
        super(context, compoundType);

        for (TemplateParameter templateParameter : compoundType.getTemplateParameters())
            templateParameters.add(new TemplateParameterTemplateData(context, templateParameter));

        for (Parameter parameter : compoundType.getTypeParameters())
            parameters.add(new ParameterTemplateData(context, parameter));

        for (Field field : compoundType.getFields())
            fields.add(new FieldTemplateData(context, compoundType, field));

        for (Function function : compoundType.getFunctions())
            functions.add(new FunctionTemplateData(context, compoundType, function));
    }

    public Iterable<TemplateParameterTemplateData> getTemplateParameters()
    {
        return templateParameters;
    }

    public Iterable<ParameterTemplateData> getParameters()
    {
        return parameters;
    }

    public Iterable<FieldTemplateData> getFields()
    {
        return fields;
    }

    public Iterable<FunctionTemplateData> getFunctions()
    {
        return functions;
    }

    public static class TemplateParameterTemplateData
    {
        public TemplateParameterTemplateData(PackageTemplateDataContext context, TemplateParameter templateParameter)
        {
            name = templateParameter.getName();
        }

        public String getName()
        {
            return name;
        }

        private final String name;
    }

    public static class ParameterTemplateData
    {
        public ParameterTemplateData(PackageTemplateDataContext context, Parameter parameter)
        {
            symbol = SymbolTemplateDataCreator.createData(context, parameter.getTypeReference());
            name = parameter.getName();
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public String getName()
        {
            return name;
        }

        private final SymbolTemplateData symbol;
        private final String name;
    }

    public static class FieldTemplateData
    {
        public FieldTemplateData(PackageTemplateDataContext context, CompoundType compoundType, Field field)
                throws ZserioExtensionException
        {
            symbol = SymbolTemplateDataCreator.createData(context, compoundType, field);
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            typeSymbol = SymbolTemplateDataCreator.createData(context, typeInstantiation);
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();

            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                isArrayImplicit = arrayInstantiation.isImplicit();
                isArrayPacked = arrayInstantiation.isPacked();
                arrayRange = "[" +
                        formatExpression(arrayInstantiation.getLengthExpression(), docExpressionFormatter) +
                        "]";
                typeInstantiation = arrayInstantiation.getElementTypeInstantiation();
            }
            else
            {
                isArrayImplicit = false;
                isArrayPacked = false;
                arrayRange = "";
            }

            if (typeInstantiation instanceof ParameterizedTypeInstantiation)
            {
                final ParameterizedTypeInstantiation parameterizedTypeInstantiation =
                        (ParameterizedTypeInstantiation)typeInstantiation;
                for (Expression typeArgument : parameterizedTypeInstantiation.getTypeArguments())
                {
                    typeArguments.add(docExpressionFormatter.formatGetter(typeArgument));
                }
            }
            if (typeInstantiation instanceof DynamicBitFieldInstantiation)
            {
                final DynamicBitFieldInstantiation dynamicBitFieldInstantiation =
                        (DynamicBitFieldInstantiation)typeInstantiation;
                dynamicBitFieldLengthExpression = formatExpression(
                        dynamicBitFieldInstantiation.getLengthExpression(), docExpressionFormatter);
            }
            else
            {
                dynamicBitFieldLengthExpression = "";
            }

            docComments = new DocCommentsTemplateData(context, field.getDocComments());
            isVirtual = field.isVirtual();
            isAutoOptional = field.isOptional() && field.getOptionalClauseExpr() == null;
            alignmentExpression = formatExpression(field.getAlignmentExpr(), docExpressionFormatter);
            constraintExpression = formatExpression(field.getConstraintExpr(), docExpressionFormatter);
            initializerExpression = formatExpression(field.getInitializerExpr(), docExpressionFormatter);
            optionalClauseExpression = formatExpression(field.getOptionalClauseExpr(), docExpressionFormatter);
            offsetExpression = formatExpression(field.getOffsetExpr(), docExpressionFormatter);
            final SqlConstraint sqlConstraint = field.getSqlConstraint();
            sqlConstraintExpression = (sqlConstraint == null) ? "" :
                formatExpression(sqlConstraint.getConstraintExpr(), docExpressionFormatter);
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public SymbolTemplateData getTypeSymbol()
        {
            return typeSymbol;
        }

        public Iterable<String> getTypeArguments()
        {
            return typeArguments;
        }

        public String getDynamicBitFieldLengthExpression()
        {
            return dynamicBitFieldLengthExpression;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        public boolean getIsVirtual()
        {
            return isVirtual;
        }

        public boolean getIsAutoOptional()
        {
            return isAutoOptional;
        }

        public String getAlignmentExpression()
        {
            return alignmentExpression;
        }

        public String getConstraintExpression()
        {
            return constraintExpression;
        }

        public boolean getIsArrayImplicit()
        {
            return isArrayImplicit;
        }

        public boolean getIsArrayPacked()
        {
            return isArrayPacked;
        }

        public String getArrayRange()
        {
            return arrayRange;
        }

        public String getInitializerExpression()
        {
            return initializerExpression;
        }

        public String getOptionalClauseExpression()
        {
            return optionalClauseExpression;
        }

        public String getOffsetExpression()
        {
            return offsetExpression;
        }

        public String getSqlConstraintExpression()
        {
            return sqlConstraintExpression;
        }

        private String formatExpression(Expression expression, ExpressionFormatter docExpressionFormatter)
                throws ZserioExtensionException
        {
            return (expression == null) ? "" : docExpressionFormatter.formatGetter(expression);
        }

        private final SymbolTemplateData symbol;
        private final SymbolTemplateData typeSymbol;
        private final List<String> typeArguments = new ArrayList<String>();
        private final String dynamicBitFieldLengthExpression;
        private final DocCommentsTemplateData docComments;
        private final boolean isVirtual;
        private final boolean isAutoOptional;
        private final String alignmentExpression;
        private final String constraintExpression;
        private final boolean isArrayImplicit;
        private final boolean isArrayPacked;
        private final String arrayRange;
        private final String initializerExpression;
        private final String optionalClauseExpression;
        private final String offsetExpression;
        private final String sqlConstraintExpression;
    }

    public static class FunctionTemplateData
    {
        public FunctionTemplateData(PackageTemplateDataContext context, CompoundType compoundType, Function function)
                throws ZserioExtensionException
        {
            symbol = SymbolTemplateDataCreator.createData(context, compoundType, function);
            returnSymbol = SymbolTemplateDataCreator.createData(context, function.getReturnTypeReference());
            final ExpressionFormatter docExpressionFormatter = context.getExpressionFormatter();
            resultExpression = docExpressionFormatter.formatGetter(function.getResultExpression());
            docComments = new DocCommentsTemplateData(context, function.getDocComments());
        }

        public SymbolTemplateData getSymbol()
        {
            return symbol;
        }

        public SymbolTemplateData getReturnSymbol()
        {
            return returnSymbol;
        }

        public String getResultExpression()
        {
            return resultExpression;
        }

        public DocCommentsTemplateData getDocComments()
        {
            return docComments;
        }

        private final SymbolTemplateData symbol;
        private final SymbolTemplateData returnSymbol;
        private final String resultExpression;
        private final DocCommentsTemplateData docComments;
    }

    private final List<TemplateParameterTemplateData> templateParameters =
            new ArrayList<TemplateParameterTemplateData>();
    private final List<ParameterTemplateData> parameters = new ArrayList<ParameterTemplateData>();
    private final List<FieldTemplateData> fields = new ArrayList<FieldTemplateData>();
    private final List<FunctionTemplateData> functions = new ArrayList<FunctionTemplateData>();
};
