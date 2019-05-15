package zserio.ast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for Structure types.
 *
 * Structure types are Zserio types as well.
 */
public class StructureType extends CompoundType
{
    /**
     * Constructor.
     *
     * @param token      ANTLR4 token to localize AST node in the sources.
     * @param pkg        Package to which belongs the structure type.
     * @param name       Name of the structure type.
     * @param parameters List of parameters for the structure type.
     * @param fields     List of all fields of the structure type.
     * @param functions  List of all functions of the structure type.
     * @param docComment Documentation comment belonging to this node.
     */
    public StructureType(Token token, Package pkg, String name, List<Parameter> parameters, List<Field> fields,
            List<FunctionType> functions, DocComment docComment)
    {
        super(token, pkg, name, parameters, fields, functions, docComment);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitStructureType(this);
    }

    @Override
    void check()
    {
        // evaluates common compound type
        super.check();

        // check that no field is SQL table
        checkTableFields();

        // check optional clause of all fields
        checkOptionalFields();

        // check that implicit array field is the last one in the structure
        checkImplicitArrayFields();
    }

    private void checkOptionalFields()
    {
        for (Field field : getFields())
            checkOptionalField(field);
    }

    private void checkImplicitArrayFields()
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

    private static void checkOptionalField(Field field)
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
                throw new ParserException(field, "Parameterized field '" + field.getName() +
                        "' is not optional but uses optional parameters!");
            }
            else
            {
                if (hasDifferentReferencedOptionals ||
                        haveFieldsDifferentOptionals(field, referencedOptionalField))
                {
                    // there are at least two parameters which are field and which have different optional
                    // clauses OR optional clause of parameter is not the same as optional clause of field
                    ZserioToolPrinter.printWarning(field, "Parameterized field '" + field.getName() +
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
        if (optionalClause1 != null && optionalClause2 != null &&
                optionalClause1.toString().equals(optionalClause2.toString()))
            return false;

        return true;
    }
}
