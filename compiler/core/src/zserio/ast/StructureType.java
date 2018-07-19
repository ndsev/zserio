package zserio.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for structure types.
 *
 * Structure types are Zserio types as well.
 */
public class StructureType extends CompoundType
{
    @Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitStructureType(this);
    }

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.ID:
            if (!(child instanceof IdToken))
                return false;
            setName(child.getText());
            break;

        case ZserioParserTokenTypes.PARAM:
            if (!(child instanceof Parameter))
                return false;
            addParameter((Parameter)child);
            break;

        case ZserioParserTokenTypes.FIELD:
            if (!(child instanceof Field))
                return false;
            addField((Field)child);
            break;

        case ZserioParserTokenTypes.FUNCTION:
            if (!(child instanceof FunctionType))
                return false;
            addFunction((FunctionType)child);
            break;

        default:
            return false;
        }

        return true;
    }

    @Override
    protected void evaluate() throws ParserException
    {
        evaluateHiddenDocComment(this);
        setDocComment(getHiddenDocComment());
    }

    @Override
    protected void check() throws ParserException
    {
        super.check();

        // check that no field is SQL table
        checkTableFields();

        // check optional clause of all fields
        checkOptionalFields();

        // check that implicit array field is the last one in the structure
        checkImplicitArrayFields();
    }

    private void checkOptionalFields() throws ParserException
    {
        for (Field field : getFields())
            checkOptionalField(field);
    }

    private void checkImplicitArrayFields() throws ParserException
    {
        final List<Field> fields = getFields();
        final int numFields = fields.size();
        for (int i = 0; i < numFields; ++i)
        {
            final ZserioType fieldType = fields.get(i).getFieldType();
            if (fieldType instanceof ArrayType)
            {
                final ArrayType arrayField = (ArrayType)fieldType;
                if (arrayField.isImplicit() && i != (numFields - 1))
                    throw new ParserException(arrayField, "Implicit array must be defined at the end " +
                            "of structure!");
            }
        }
    }

    private static void checkOptionalField(Field field) throws ParserException
    {
        final Set<Field> referencedFields = getReferencedParameterFields(field);

        // find out parameter which is optional field
        boolean hasDifferentReferencedOptionals = false;
        Field referencedOptionalField = null;
        for (Field referencedField : referencedFields)
        {
            if (referencedField.getIsOptional())
            {
                if (referencedOptionalField == null)
                {
                    referencedOptionalField = referencedField;
                }
                else
                {
                    if (haveFieldsDifferentOptionals(referencedField, referencedOptionalField))
                        hasDifferentReferencedOptionals = true;
                }
            }
        }

        if (referencedOptionalField != null)
        {
            // there is at least one parameter which is optional field
            if (!field.getIsOptional())
            {
                // but this field is not optional => ERROR
                throw new ParserException(field, "Parametrized field '" + field.getName() +
                        "' is not optional but uses optional parameters!");
            }
            else
            {
                if (hasDifferentReferencedOptionals ||
                        haveFieldsDifferentOptionals(field, referencedOptionalField))
                {
                    // there are at least two parameters which are field and which have different optional
                    // clauses OR optional clause of parameter is not the same as optional clause of field
                    ZserioToolPrinter.printWarning(field, "Parametrized field '" + field.getName() +
                            "' has different optional clause than parameters.");
                }
            }
        }
    }

    private static Set<Field> getReferencedParameterFields(Field field)
    {
        final Iterable<TypeInstantiation.InstantiatedParameter> instantiatedParameters =
                field.getInstantiatedParameters();
        final Set<Field> referencedFields = new HashSet<Field>();
        for (TypeInstantiation.InstantiatedParameter instantiatedParameter : instantiatedParameters)
        {
            final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
            referencedFields.addAll(argumentExpression.getReferencedSymbolObjects(Field.class));
        }

        return referencedFields;
    }

    private static boolean haveFieldsDifferentOptionals(Field field1, Field field2)
    {
        final Expression optionalClause1 = field1.getOptionalClauseExpr();
        final Expression optionalClause2 = field2.getOptionalClauseExpr();
        if (optionalClause1 != null && optionalClause2 != null && optionalClause1.equals(optionalClause2))
            return false;

        return true;
    }

    private static final long serialVersionUID = 7339295016544090386L;
}
