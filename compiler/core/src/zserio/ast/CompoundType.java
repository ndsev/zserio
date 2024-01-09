package zserio.ast;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * AST abstract node for all Compound types.
 *
 * This is an abstract class for all Compound Zserio types (structure types, choice types, ...).
 */
public abstract class CompoundType extends TemplatableType
{
    /**
     * Constructor.
     *
     * @param location AST node location.
     * @param pkg Package to which belongs the compound type.
     * @param name Name of the compound type.
     * @param templateParameters List of template parameters.
     * @param typeParameters List of parameters for the compound type.
     * @param fields List of all fields of the compound type.
     * @param functions List of all functions of the compound type.
     * @param docComments List of documentation comments belonging to this node.
     */
    CompoundType(AstLocation location, Package pkg, String name, List<TemplateParameter> templateParameters,
            List<Parameter> typeParameters, List<Field> fields, List<Function> functions,
            List<DocComment> docComments)
    {
        super(location, templateParameters, docComments);

        this.pkg = pkg;
        this.name = name;
        this.typeParameters = typeParameters;
        this.fields = fields;
        this.functions = functions;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (Parameter parameter : typeParameters)
            parameter.accept(visitor);

        for (Field field : fields)
            field.accept(visitor);

        for (Function function : functions)
            function.accept(visitor);
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public String getName()
    {
        // if this is a template instantiation, return its name
        final String instantiationName = getInstantiationName();

        return (instantiationName != null) ? instantiationName : name;
    }

    @Override
    public Scope getScope()
    {
        return scope;
    }

    /**
     * Gets all fields associated to this compound type.
     *
     * Fields are ordered according to their definition in Zserio source file.
     *
     * @return List of fields which this compound type contains.
     */
    public List<Field> getFields()
    {
        return Collections.unmodifiableList(fields);
    }

    /**
     * Gets all type parameters associated to this compound type.
     *
     * Parameters are ordered according to their definition in Zserio source file.
     *
     * @return List of type parameters which this compound type contains.
     */
    public List<Parameter> getTypeParameters()
    {
        return Collections.unmodifiableList(typeParameters);
    }

    /**
     * Gets all functions associated to this compound type.
     *
     * Functions are ordered according to their definition in Zserio source file.
     *
     * @return List of functions which this compound type contains.
     */
    public List<Function> getFunctions()
    {
        return Collections.unmodifiableList(functions);
    }

    // TODO[Mi-L@] This method should not be part of this AST node. Move to some utilities or let generators
    //             to make it by themselves.
    /**
     * Checks if this compound type needs children initialization method.
     *
     * This is called from C++ extension to check if the compound type has some descendant with parameters
     * (if some descendant has initialize method).
     *
     * @return true if this compound type has some descendant with parameters.
     */
    public boolean needsChildrenInitialization()
    {
        for (Field field : fields)
        {
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
                typeInstantiation = ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation();

            final ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                // compound type can have itself as an optional field
                if (!childCompoundType.getTypeParameters().isEmpty() ||
                        (childCompoundType != this && childCompoundType.needsChildrenInitialization()))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if this compound type is packable.
     *
     * The compound type is packable if any of its subfields contains some packable field.
     *
     * @return True if this compound type contains some packable field.
     */
    public boolean isPackable()
    {
        return hasPackableField();
    }

    /**
     * Checks the compound type.
     */
    void check()
    {
        checkDirectRecursion();
        checkIndirectRecursion(this, this);
    }

    /**
     * Checks validity of symbol names for none-SQL compound types.
     */
    protected void checkSymbolNames()
    {
        // parameters and fields cannot clash (difference only in case of the first letter is still clash!)
        ScopeSymbolValidator validator = new ScopeSymbolValidator();
        for (Parameter param : typeParameters)
            validator.validate(param.getName(), param);
        for (Field field : fields)
            validator.validate(field.getName(), field);

        // function names cannot clash (difference only in case of the first letter is still clash!)
        validator = new ScopeSymbolValidator();
        for (Function function : functions)
            validator.validate(function.getName(), function);
    }

    /**
     * Checks validity of symbol names for SQL compound types.
     */
    protected void checkSqlSymbolNames()
    {
        final SqlIdentifierValidator validator = new SqlIdentifierValidator();
        for (Field field : fields)
            validator.validateSymbol(field.getName(), field);
    }

    /**
     * Checks if no field is SQL table.
     */
    protected void checkSqlTableFields()
    {
        for (Field field : fields)
        {
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
                typeInstantiation = ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation();

            final ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (fieldBaseType instanceof SqlTableType)
                throw new ParserException(field, "Field '" + field.getName() + "' cannot be a sql table!");
        }
    }

    /**
     * Checks if all fields are SQL tables.
     */
    protected void checkNonSqlTableFields()
    {
        for (Field field : fields)
        {
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
                typeInstantiation = ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation();

            final ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (!(fieldBaseType instanceof SqlTableType))
                throw new ParserException(field, "Field '" + field.getName() + "' is not a sql table!");
        }
    }

    protected boolean hasPackableField()
    {
        for (Field field : fields)
        {
            // prevent cycle in recursion
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                typeInstantiation = ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation();
            }
            final ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                if (childCompoundType == this)
                    continue;
            }

            if (field.isPackable())
                return true;
        }

        return false;
    }

    protected boolean containsExtendedField(Field field)
    {
        final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
        ZserioType fieldBaseType = typeInstantiation.getBaseType();
        final boolean isArray = typeInstantiation instanceof ArrayInstantiation;
        if (isArray)
        {
            final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
            fieldBaseType = arrayInstantiation.getElementTypeInstantiation().getBaseType();
        }

        if (fieldBaseType instanceof CompoundType)
        {
            final CompoundType childCompoundType = (CompoundType)fieldBaseType;
            for (Field childField : childCompoundType.getFields())
            {
                if (childField.isExtended())
                    return true;

                if (childCompoundType != this && childCompoundType.containsExtendedField(childField))
                    return true;
            }
        }

        return false;
    }

    protected void trackExtendedField(Field field, ParserStackedException stackedException)
    {
        final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
        ZserioType fieldBaseType = typeInstantiation.getBaseType();
        if (typeInstantiation instanceof ArrayInstantiation)
        {
            final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
            fieldBaseType = arrayInstantiation.getElementTypeInstantiation().getBaseType();
        }

        if (fieldBaseType instanceof CompoundType)
        {
            final CompoundType childCompoundType = (CompoundType)fieldBaseType;
            for (Field childField : childCompoundType.getFields())
            {
                if (childField.isExtended())
                {
                    stackedException.pushMessage(childField.getLocation(), "    extended field used here");
                }
                else if (childCompoundType != this && childCompoundType.containsExtendedField(childField))
                {
                    stackedException.pushMessage(childField.getLocation(), "    extended field used here");
                    childCompoundType.trackExtendedField(childField, stackedException);
                }
            }
        }
    }

    protected boolean hasBranchWithoutImplicitArray()
    {
        throw new InternalError("CompoundType.hasBranchWithoutImplicitArray() is not implemented!");
    }

    protected boolean hasFieldBranchWithoutImplicitArray(Field field)
    {
        final boolean implicitCanBeEmpty = false;
        if (hasFieldEmptyBranch(field, implicitCanBeEmpty))
            return true;

        final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
        ZserioType fieldBaseType = typeInstantiation.getBaseType();
        if (typeInstantiation instanceof ArrayInstantiation)
        {
            final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
            if (arrayInstantiation.isImplicit())
                return false;

            if (arrayInstantiation.getLengthExpression() == null || // auto array can be empty
                    arrayInstantiation.getLengthExpression().getIntegerLowerBound() == null ||
                    arrayInstantiation.getLengthExpression().getIntegerLowerBound().compareTo(
                            BigInteger.ZERO) <= 0)
            {
                return true; // may be empty array
            }

            fieldBaseType = arrayInstantiation.getElementTypeInstantiation().getBaseType();
        }

        if (fieldBaseType instanceof CompoundType)
        {
            final CompoundType childCompoundType = (CompoundType)fieldBaseType;
            if (childCompoundType != this) // prevent recursion (can occur in case of non-empty array)
                return childCompoundType.hasBranchWithoutImplicitArray();
        }

        return true;
    }

    protected void trackImplicitArray(ParserStackedException stackedException)
    {
        for (Field field : fields)
        {
            if (!hasFieldBranchWithoutImplicitArray(field))
            {
                stackedException.pushMessage(field.getLocation(), "    implicit array is used here");

                TypeInstantiation typeInstantiation = field.getTypeInstantiation();
                if (typeInstantiation instanceof ArrayInstantiation)
                {
                    typeInstantiation = ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation();
                }

                final ZserioType fieldBaseType = typeInstantiation.getBaseType();
                if (fieldBaseType instanceof CompoundType)
                {
                    final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                    if (childCompoundType != this)
                        childCompoundType.trackImplicitArray(stackedException);
                }

                break;
            }
        }
    }

    protected boolean hasEmptyBranch(boolean imlicitCanBeEmpty)
    {
        throw new InternalError("CompoundType.hasEmptyBranch() is not implemented!");
    }

    protected boolean hasFieldEmptyBranch(Field field, boolean implicitCanBeEmpty)
    {
        if (field.isOptional())
        {
            if (field.getOptionalClauseExpr() != null)
                return true; // non-auto optional, thus can be empty
            else
                return false; // auto optional, needs at least one byte
        }

        final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
        ZserioType fieldBaseType = typeInstantiation.getBaseType();
        if (typeInstantiation instanceof ArrayInstantiation)
        {
            final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;

            // we don't care about multiple consecutive implicit arrays
            // (first array will read rest of the buffer and remaining implicit arrays will be empty)
            if (arrayInstantiation.isImplicit())
                return implicitCanBeEmpty;

            if (arrayInstantiation.getLengthExpression() == null)
                return false; // auto array, needs at least size

            if (arrayInstantiation.getLengthExpression().getIntegerLowerBound() == null ||
                    arrayInstantiation.getLengthExpression().getIntegerLowerBound().compareTo(
                            BigInteger.ZERO) <= 0)
            {
                return true; // may be empty array
            }

            final TypeInstantiation elementInstantiation = arrayInstantiation.getElementTypeInstantiation();
            fieldBaseType = elementInstantiation.getBaseType();
        }

        if (fieldBaseType instanceof CompoundType)
        {
            final CompoundType childCompoundType = (CompoundType)fieldBaseType;
            if (childCompoundType != this) // prevent recursion (can occur in case of non-empty array)
                return childCompoundType.hasEmptyBranch(implicitCanBeEmpty);
            // note: never ending recursion should have been caught by checkDirectRecursion
        }

        return false;
    }

    private void checkDirectRecursion()
    {
        // check recursive fields which are not arrays
        for (Field field : fields)
        {
            if (field.isOptional())
                continue;

            final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            ZserioType fieldBaseType = typeInstantiation.getBaseType();

            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;

                if (arrayInstantiation.getLengthExpression() == null || // auto or implicit array can be empty
                        arrayInstantiation.getLengthExpression().getIntegerLowerBound() == null ||
                        arrayInstantiation.getLengthExpression().getIntegerLowerBound().compareTo(
                                BigInteger.ZERO) <= 0)
                {
                    continue; // may be empty array
                }

                fieldBaseType = arrayInstantiation.getElementTypeInstantiation().getBaseType();
            }

            if (fieldBaseType == this)
            {
                // this field is not array or optional and it is recursive
                throw new ParserException(field,
                        "Field '" + field.getName() +
                                "' is recursive and neither optional nor array which can be empty!");
            }
        }
    }

    private static void checkIndirectRecursion(CompoundType outer, CompoundType inner)
    {
        for (Field field : inner.fields)
        {
            TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                typeInstantiation = ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation();
            }

            final ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                if (outer != inner && outer == childCompoundType)
                    throw new ParserException(field,
                            "Indirect recursion between '" + outer.getName() + "' and '" + inner.getName() +
                                    "'!");

                if (inner != childCompoundType)
                    checkIndirectRecursion(outer, childCompoundType);
            }
        }
    }

    private final Scope scope = new Scope(this);
    private final Package pkg;
    private final String name;

    private final List<Field> fields;
    private final List<Parameter> typeParameters;
    private final List<Function> functions;
}
