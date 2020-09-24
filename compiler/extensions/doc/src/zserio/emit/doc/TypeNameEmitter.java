package zserio.emit.doc;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.CompoundType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldInstantiation;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.FixedBitFieldType;
import zserio.ast.ServiceType;
import zserio.ast.SqlConstraint;
import zserio.ast.TypeInstantiation;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.StdIntegerType;
import zserio.ast.Subtype;
import zserio.ast.VarIntegerType;
import zserio.emit.common.ExpressionFormatter;
import zserio.emit.common.ZserioEmitException;

public class TypeNameEmitter
{
    public TypeNameEmitter(ExpressionFormatter expressionFormatter)
    {
        this.expressionFormatter = expressionFormatter;
    }

    public String getOffset(Field f) throws ZserioEmitException
    {
        String result = "";
        Expression offset = f.getOffsetExpr();
        if (offset != null)
        {
            result = expressionFormatter.formatGetter(offset) + ":";
        }
        return result;
    }

    public String getArrayRange(Field f) throws ZserioEmitException
    {
        String result = null;
        final TypeInstantiation typeInstantiation = f.getTypeInstantiation();
        if (typeInstantiation instanceof ArrayInstantiation)
        {
            result = "[";
            Expression expr = ((ArrayInstantiation)typeInstantiation).getLengthExpression();
            if (expr != null)
            {
                result += expressionFormatter.formatGetter(expr);
            }
            result += "]";
        }

        return result;
    }

    public String getInitializer(Field field) throws ZserioEmitException
    {
        String result = "";
        Expression expr = field.getInitializerExpr();
        if (expr != null)
        {
            result += " = " + expressionFormatter.formatGetter(expr);
        }

        return StringHtmlUtil.escapeForHtml(result);
    }

    public String getOptionalClause(Field field) throws ZserioEmitException
    {
        String result = "";
        Expression expr = field.getOptionalClauseExpr();
        if (expr != null)
        {
            result = " if " + expressionFormatter.formatGetter(expr);
        }

        return StringHtmlUtil.escapeForHtml(result);
    }

    public String getConstraint(Field field) throws ZserioEmitException
    {
      String result = "";
      Expression expr = field.getConstraintExpr();
      if (expr != null)
      {
        result = " : " + expressionFormatter.formatGetter(expr);
      }

      return StringHtmlUtil.escapeForHtml(result);
    }

    public String getSqlConstraint(Field field) throws ZserioEmitException
    {
        final SqlConstraint sqlConstraint = field.getSqlConstraint();
        if (sqlConstraint == null)
            return "";

        final String result = expressionFormatter.formatGetter(sqlConstraint.getConstraintExpr());

        return StringHtmlUtil.escapeForHtml(result);
    }

    public boolean getIsVirtual(Field field)
    {
      return field.getIsVirtual();
    }

    public static String getTypeName(AstNode t) throws ZserioEmitException
    {
        String result = null;

        if (t instanceof ArrayInstantiation)
        {
            // don't HTML-escape the result - it gets escaped in the call
            final TypeInstantiation elementTypeInstantiation =
                    ((ArrayInstantiation)t).getElementTypeInstantiation();
            return getTypeName(elementTypeInstantiation);
        }
        else if (t instanceof DynamicBitFieldInstantiation)
        {
            return StringHtmlUtil.escapeForHtml(getTypeName((DynamicBitFieldInstantiation)t));
        }
        else if (t instanceof TypeInstantiation)
        {
            // use the underlying type bellow
            t = ((TypeInstantiation)t).getType();
        }

        if (t instanceof StdIntegerType)
        {
            result = getTypeName((StdIntegerType) t);
        }
        else if (t instanceof VarIntegerType)
        {
            result = getTypeName((VarIntegerType) t);
        }
        else if (t instanceof FixedBitFieldType)
        {
            result = getTypeName((FixedBitFieldType) t);
        }
        else if (t instanceof DynamicBitFieldType)
        {
            // used only when no instantiation is available
            result = getTypeName((DynamicBitFieldType) t);
        }
        else if (t instanceof CompoundType)
        {
            CompoundType compound = (CompoundType) t;
            result = compound.getName();
        }
        else if (t instanceof EnumType)
        {
            EnumType enumeration = (EnumType) t;
            result = enumeration.getName();
        }
        else if (t instanceof BitmaskType)
        {
            BitmaskType bitmask = (BitmaskType) t;
            result = bitmask.getName();
        }
        else if (t instanceof Subtype)
        {
            Subtype subtype = (Subtype) t;
            result = subtype.getName();
        }
        else if (t instanceof Constant)
        {
            Constant consttype = (Constant) t;
            result = consttype.getName();
        }
        else if (t instanceof ServiceType)
        {
            result = ((ServiceType)t).getName();
        }
        else if (t instanceof ZserioType)
        {
            result = ((ZserioType)t).getName();
        }
        else
        {
            throw new ZserioEmitException("Unexpected zserio type or symbol '" + t.getClass().getName() + "'!");
        }

        return StringHtmlUtil.escapeForHtml(result);
    }

    private static String getTypeName(StdIntegerType t)
    {
        return t.getName();
    }

    private static String getTypeName(VarIntegerType t)
    {
        return t.getName();
    }

    private static String getTypeName(FixedBitFieldType t) throws ZserioEmitException
    {
        final String rawName = (t.isSigned()) ? "int" : "bit";

        return rawName + ":" + t.getBitSize();
    }

    private static String getTypeName(DynamicBitFieldInstantiation t) throws ZserioEmitException
    {
        String rawName = "", parameterizedName = "";

        rawName = getTypeName(t.getBaseType());

        final DocExpressionFormattingPolicy policy = new DocExpressionFormattingPolicy();
        final ExpressionFormatter expressionFormatter = new ExpressionFormatter(policy);
        Expression expression = t.getLengthExpression();
        parameterizedName = (expression == null) ? "" : expressionFormatter.formatGetter(expression);
        parameterizedName = "<" + parameterizedName + ">";

        return rawName + parameterizedName;
    }

    private static String getTypeName(DynamicBitFieldType t) throws ZserioEmitException
    {
        return (t.isSigned()) ? "int" : "bit";
    }

    private final ExpressionFormatter expressionFormatter;
}
