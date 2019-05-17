package zserio.ast;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.Token;

import zserio.antlr.util.ParserException;
import zserio.ast.Package;
import zserio.ast.Scope;
import zserio.ast.ZserioScopedType;
import zserio.tools.HashUtil;

/**
 * AST abstract node for all Compound types.
 *
 * This is an abstract class for all Compound Zserio types (structure types, choice types, ...).
 */
public abstract class CompoundType extends AstNodeWithDoc implements ZserioScopedType, Comparable<CompoundType>
{
    /**
     * Constructor.
     *
     * @param token      ANTLR4 token to localize AST node in the sources.
     * @param pkg        Package to which belongs the compound type.
     * @param name       Name of the compound type.
     * @param parameters List of parameters for the compound type.
     * @param fields     List of all fields of the compound type.
     * @param functions  List of all functions of the compound type.
     * @param docComment Documentation comment belonging to this node.
     */
    CompoundType(Token token, Package pkg, String name, List<Parameter> parameters, List<Field> fields,
            List<FunctionType> functions, DocComment docComment)
    {
        super(token, docComment);

        this.pkg = pkg;
        this.name = name;
        this.parameters = parameters;
        this.fields = fields;
        this.functions = functions;
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        for (Parameter parameter : parameters)
            parameter.accept(visitor);

        for (Field field : fields)
            field.accept(visitor);

        for (FunctionType function : functions)
            function.accept(visitor);

        super.visitChildren(visitor);
    }

    @Override
    public int compareTo(CompoundType other)
    {
        final int result = getName().compareTo(other.getName());
        if (result != 0)
            return result;

        return getPackage().getPackageName().compareTo(other.getPackage().getPackageName());
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;

        if (other instanceof CompoundType)
            return compareTo((CompoundType)other) == 0;

        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = HashUtil.HASH_SEED;
        hash = HashUtil.hash(hash, getName());
        hash = HashUtil.hash(hash, getPackage().getPackageName());

        return hash;
    }

    @Override
    public Package getPackage()
    {
        return pkg;
    }

    @Override
    public String getName()
    {
        return name;
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
     * Gets all parameters associated to this compound type.
     *
     * Parameters are ordered according to their definition in Zserio source file.
     *
     * @return List of parameters which this compound type contains.
     */
    public List<Parameter> getParameters()
    {
        return Collections.unmodifiableList(parameters);
    }

    /**
     * Gets all functions associated to this compound type.
     *
     * Functions are ordered according to their definition in Zserio source file.
     *
     * @return List of functions which this compound type contains.
     */
    public List<FunctionType> getFunctions()
    {
        return Collections.unmodifiableList(functions);
    }

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
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
            if (fieldBaseType instanceof CompoundType)
            {
                final CompoundType childCompoundType = (CompoundType)fieldBaseType;
                // compound type can have itself as an optional field
                if (!childCompoundType.getParameters().isEmpty() ||
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

            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
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
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
            if (fieldBaseType instanceof SqlTableType)
                throw new ParserException(field, "Field '" + field.getName() +
                        "' cannot be a sql table!");
        }
    }

    private void checkDirectRecursion()
    {
        // check recursive fields which are not arrays
        for (Field field : fields)
        {
            if (!field.getIsOptional())
            {
                ZserioType fieldType = field.getFieldType();
                if (fieldType instanceof TypeInstantiation)
                    fieldType = ((TypeInstantiation)fieldType).getReferencedType();
                fieldType = TypeReference.resolveBaseType(fieldType);

                if (fieldType == this)
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
            final ZserioType fieldBaseType = TypeReference.resolveBaseType(field.getFieldReferencedType());
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
    private final List<Parameter> parameters;
    private final List<FunctionType> functions;
}
