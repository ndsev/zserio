package zserio.ast;

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

    // TODO[Mi-L@] These methods should not be part of this AST node. Move to some utilities or let generators
    //             to make it by themselves.
    /**
     * Checks if this compound type needs children initialization method.
     *
     * This is called from C++ emitter to check if the compound type has some descendant with parameters (if
     * (some descendant has initialize method).
     *
     * @return true if this compound type has some descendant with parameters.
     */
    public boolean needsChildrenInitialization()
    {
        for (Field field : fields)
        {
            final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                final TypeInstantiation elementInstantiation = arrayInstantiation.getElementTypeInstantiation();
                fieldBaseType = elementInstantiation.getBaseType();
            }

            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                // compound type can have itself as an optional field
                if (!childCompoundType.getTypeParameters().isEmpty() ||
                        (childCompoundType != this && childCompoundType.needsChildrenInitialization()))
                    return true;
            }
        }

        return false;
    }

    /**
     * Checks if this compound type or any of its subfield contains some offset.
     *
     * @return true if this compound type contains some offset.
     */
    public boolean hasFieldWithOffset()
    {
        for (Field field : fields)
        {
            if (field.getOffsetExpr() != null)
                return true;

            final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                final TypeInstantiation elementInstantiation = arrayInstantiation.getElementTypeInstantiation();
                fieldBaseType = elementInstantiation.getBaseType();
            }

            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                // compound type can have itself as an optional field
                if (childCompoundType != this && childCompoundType.hasFieldWithOffset())
                    return true;
            }
        }

        return false;
    }

    /**
     * Checks the compound type.
     */
    void check()
    {
        checkSymbolNames();
        checkDirectRecursion();
        checkIndirectRecursion(this, this);
    }

    /**
     * Checks if no field is SQL table.
     */
    void checkTableFields()
    {
        // check if fields are not sql tables
        for (Field field : fields)
        {
            final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                final TypeInstantiation elementInstantiation = arrayInstantiation.getElementTypeInstantiation();
                fieldBaseType = elementInstantiation.getBaseType();
            }

            if (fieldBaseType instanceof SqlTableType)
                throw new ParserException(field, "Field '" + field.getName() +
                        "' cannot be a sql table!");
        }
    }

    /**
     * Checks validity of symbol names for most compound types. Can be overridden by descendants.
     */
    void checkSymbolNames()
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

    private void checkDirectRecursion()
    {
        // check recursive fields which are not arrays
        for (Field field : fields)
        {
            final ZserioType fieldBaseType = field.getTypeInstantiation().getBaseType();
            if (!field.isOptional() && !(fieldBaseType instanceof ArrayType))
            {
                if (fieldBaseType == this)
                {
                    // this field is not array or optional and it is recursive
                    throw new ParserException(field, "Field '" + field.getName() +
                            "' is recursive and neither optional nor array!");
                }
            }
        }
    }

    private static void checkIndirectRecursion(CompoundType outer, CompoundType inner)
    {
        for (Field field : inner.fields)
        {
            final TypeInstantiation typeInstantiation = field.getTypeInstantiation();
            ZserioType fieldBaseType = typeInstantiation.getBaseType();
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                final TypeInstantiation elementInstantiation = arrayInstantiation.getElementTypeInstantiation();
                fieldBaseType = elementInstantiation.getBaseType();
            }

            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                if (outer != inner && outer == childCompoundType)
                        throw new ParserException(field, "Indirect recursion between '" + outer.getName() +
                                "' and '" + inner.getName() + "'!");

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
