package zserio.emit.doc;

import zserio.ast.ArrayType;
import zserio.ast.BitFieldType;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.ServiceType;
import zserio.ast.SqlConstraint;
import zserio.ast.ZserioType;
import zserio.ast.EnumType;
import zserio.ast.Expression;
import zserio.ast.Field;
import zserio.ast.StdIntegerType;
import zserio.ast.Subtype;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
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
        ZserioType type = f.getFieldType();

        type = TypeReference.resolveType(type);
        if (type instanceof ArrayType)
        {
            result = "[";
            Expression expr = ((ArrayType) type).getLengthExpression();
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

    public static String getTypeName(ZserioType t) throws ZserioEmitException
    {
        String result = null;

        // t = TypeReference.resolveType(t);
        if (t instanceof StdIntegerType)
        {
          result = getTypeName((StdIntegerType) t);
        }
        else if (t instanceof VarIntegerType)
        {
          result = getTypeName((VarIntegerType) t);
        }
        else if (t instanceof BitFieldType)
        {
            result = getTypeName((BitFieldType) t);
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
        else if (t instanceof Subtype)
        {
            Subtype subtype = (Subtype) t;
            result = subtype.getName();
        }
        else if (t instanceof ConstType)
        {
            ConstType consttype = (ConstType) t;
            result = consttype.getName();
        }
        else if (t instanceof ServiceType)
        {
            result = ((ServiceType)t).getName();
        }
        else if (t instanceof TypeInstantiation)
        {
            TypeInstantiation inst = (TypeInstantiation) t;
            CompoundType compound = inst.getBaseType();
            result = compound.getName();
        }
        else if (t instanceof ArrayType)
        {
            // don't HTML-escape the result - it gets escaped in the call
            return getTypeName(TypeReference.resolveBaseType(((ArrayType) t).getElementType()));
        }
        else if (t instanceof TypeReference)
        {
            TypeReference reference = (TypeReference) t;
            result = reference.getName();
        }
        else
        {
            ZserioType res = TypeReference.resolveType(t);
            result = res.getName();
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

    private static String getTypeName(BitFieldType t) throws ZserioEmitException
    {
        String rawName = "", parameterizedName = "";

        rawName = (t.isSigned()) ? "int" : "bit";

        final DocExpressionFormattingPolicy policy = new DocExpressionFormattingPolicy();
        final ExpressionFormatter expressionFormatter = new ExpressionFormatter(policy);
        Expression expression = t.getLengthExpression();
        parameterizedName = expressionFormatter.formatGetter(expression);
        parameterizedName = "<" + parameterizedName + ">";

        return rawName + parameterizedName;
    }

    private final ExpressionFormatter expressionFormatter;
}
