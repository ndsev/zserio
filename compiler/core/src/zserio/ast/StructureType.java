package zserio.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zserio.ast.ParameterizedTypeInstantiation.InstantiatedParameter;
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
     * @param location AST node location.
     * @param pkg Package to which belongs the structure type.
     * @param name Name of the structure type.
     * @param templateParameters List of template parameters.
     * @param typeParameters List of parameters for the structure type.
     * @param fields List of all fields of the structure type.
     * @param functions List of all functions of the structure type.
     * @param docComments List of documentation comments belonging to this node.
     */
    public StructureType(AstLocation location, Package pkg, String name,
            List<TemplateParameter> templateParameters, List<Parameter> typeParameters, List<Field> fields,
            List<Function> functions, List<DocComment> docComments)
    {
        super(location, pkg, name, templateParameters, typeParameters, fields, functions, docComments);
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitStructureType(this);
    }

    @Override
    StructureType instantiateImpl(List<TemplateArgument> templateArguments, Package instantiationPackage)
    {
        final List<Parameter> instantiatedTypeParameters = new ArrayList<Parameter>();
        for (Parameter typeParameter : getTypeParameters())
        {
            instantiatedTypeParameters.add(
                    typeParameter.instantiate(getTemplateParameters(), templateArguments));
        }

        final List<Field> instantiatedFields = new ArrayList<Field>();
        for (Field field : getFields())
            instantiatedFields.add(field.instantiate(getTemplateParameters(), templateArguments));

        final List<Function> instantiatedFunctions = new ArrayList<Function>();
        for (Function function : getFunctions())
            instantiatedFunctions.add(function.instantiate(getTemplateParameters(), templateArguments));

        return new StructureType(getLocation(), instantiationPackage, getName(),
                new ArrayList<TemplateParameter>(), instantiatedTypeParameters, instantiatedFields,
                instantiatedFunctions, getDocComments());
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

        // check implicit arrays
        checkImplicitArrays();
    }

    @Override
    protected boolean hasBranchWithoutImplicitArray()
    {
        // all fields must have branch without implicit array
        for (Field field : getFields())
        {
            if (!hasFieldBranchWithoutImplicitArray(field))
                return false;
        }

        return true;
    }

    @Override
    protected boolean hasEmptyBranch(boolean implicitCanBeEmpty)
    {
        // all fields must have empty branch
        for (Field field : getFields())
        {
            if (!hasFieldEmptyBranch(field, implicitCanBeEmpty))
                return false;
        }

        return true;
    }

    private void checkOptionalFields()
    {
        for (Field field : getFields())
            checkOptionalField(field);
    }

    private static void checkOptionalField(Field field)
    {
        final Set<Field> referencedFields = getReferencedParameterFields(field);

        // find out parameter which is optional field
        boolean hasDifferentReferencedOptionals = false;
        Field referencedOptionalField = null;
        for (Field referencedField : referencedFields)
        {
            if (referencedField.isOptional())
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
            if (!field.isOptional())
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

    private void checkImplicitArrays()
    {
        // once first unconditional implicit array is found, all following fields must provide an alternative
        // to become empty
        boolean hasImplicitArray = false;
        for (Field field : getFields())
        {
            if (!hasImplicitArray)
            {
                hasImplicitArray = !hasFieldBranchWithoutImplicitArray(field);
            }
            else
            {
                final boolean implicitCanBeEmpty = true;
                if (!hasFieldEmptyBranch(field, implicitCanBeEmpty))
                {
                    final ParserStackedException stackedException = new ParserStackedException(
                            field.getLocation(), "Field '" + field.getName() + "' follows an implicit array!");
                    trackImplicitArray(stackedException);
                    throw stackedException;
                }
            }
        }
    }

    private static Set<Field> getReferencedParameterFields(Field field)
    {
        final Set<Field> referencedFields = new HashSet<Field>();
        final TypeInstantiation fieldTypeInstantiation = field.getTypeInstantiation();
        if (fieldTypeInstantiation instanceof ParameterizedTypeInstantiation)
        {
            final Iterable<InstantiatedParameter> instantiatedParameters =
                    ((ParameterizedTypeInstantiation)fieldTypeInstantiation).getInstantiatedParameters();
            for (InstantiatedParameter instantiatedParameter : instantiatedParameters)
            {
                final Expression argumentExpression = instantiatedParameter.getArgumentExpression();
                referencedFields.addAll(argumentExpression.getReferencedSymbolObjects(Field.class));
            }
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
