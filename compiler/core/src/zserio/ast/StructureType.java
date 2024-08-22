package zserio.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for Structure types.
 *
 * Structure types are Zserio types as well.
 */
public final class StructureType extends CompoundType
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
        super.check();

        checkSymbolNames();
        checkSqlTableFields();

        checkExtendedFields();

        checkImplicitArrays();

        checkDuplicatedOffsets();
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

    private void checkExtendedFields()
    {
        // once first extended field is found, all following fields must be also extended
        Field firstExtendedField = null;
        for (Field field : getFields())
        {
            // nested fields cannot contain extended fields
            if (containsExtendedField(field))
            {
                final ParserStackedException stackedException = new ParserStackedException(
                        field.getLocation(), "Field '" + field.getName() + "' contains an extended field!");
                trackExtendedField(field, stackedException);
                throw stackedException;
            }

            if (firstExtendedField == null)
            {
                if (field.isExtended())
                    firstExtendedField = field;
            }
            else
            {
                if (!field.isExtended())
                {
                    final ParserStackedException stackedException = new ParserStackedException(
                            field.getLocation(),
                            "Field '" + field.getName() + "' follows an extended field "
                                    + "and is not marked as extended!");
                    stackedException.pushMessage(
                            firstExtendedField.getLocation(), "    extended field used here");
                    throw stackedException;
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

    private void checkDuplicatedOffsets()
    {
        final List<Expression> offsetExpressions = new ArrayList<Expression>();
        for (Field field : getFields())
        {
            if (field.getOffsetExpr() != null)
            {
                final Expression fieldOffsetExpression = field.getOffsetExpr();
                for (Expression offsetExpression : offsetExpressions)
                {
                    if (ExpressionComparator.equals(offsetExpression, fieldOffsetExpression))
                    {
                        final ParserStackedException exception = new ParserStackedException(
                                fieldOffsetExpression.getLocation(), "Duplicated offset expression!");
                        exception.pushMessage(offsetExpression.getLocation(), "    First used here!");
                        throw exception;
                    }
                }

                offsetExpressions.add(field.getOffsetExpr());
            }
        }
    }
}
