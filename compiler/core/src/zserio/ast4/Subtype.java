package zserio.ast4;

import org.antlr.v4.runtime.Token;

import zserio.ast4.TypeReference;
import zserio.ast4.ZserioType;

public class Subtype extends AstNodeBase implements ZserioType
{
    public Subtype(Token token, Package pkg, ZserioType targetType, String name)
    {
        super(token);

        this.pkg = pkg;
        this.targetType = targetType;
        this.name = name;
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

    /**
     * Gets the target type.
     *
     * @return Type referenced by this subtype.
     */
    public ZserioType getTargetType()
    {
        return targetType;
    }

    /**
     * Gets target base type.
     *
     * @return Resolved base type of the target type.
     */
    public ZserioType getTargetBaseType()
    {
        return targetBaseType;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitSubtype(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        targetType.accept(visitor);
    }

    /**
     * Resolves the subtype to a defined type called at the end of linking phase.
     *
     * @return Resolved base type of this subtype.
     *
     * @throws ParserException When cyclic definition is detected.
     */
    protected ZserioType resolve() throws ParserException
    {
        if (resolvingState == ResolvingState.RESOLVED)
            return targetBaseType;

        // detect cycles in subtype definitions
        if (resolvingState == ResolvingState.RESOLVING)
            throw new ParserException(this, "Cyclic dependency detected in subtype '" +
                    getName() + "' definition!");

        resolvingState = ResolvingState.RESOLVING;

        // base type can be only type reference or a defined type.
        if (targetType instanceof TypeReference)
        {
            final ZserioType referencedTargetType = ((TypeReference)targetType).getReferencedType();
            if (referencedTargetType instanceof Subtype)
                targetBaseType = ((Subtype)referencedTargetType).resolve();
            else
                targetBaseType = referencedTargetType;
        }
        else // built-in type
        {
            targetBaseType = targetType;
        }

        resolvingState = ResolvingState.RESOLVED;

        return targetBaseType;
    }

    private enum ResolvingState
    {
        UNRESOLVED,
        RESOLVING,
        RESOLVED
    };

    private final Package pkg;

    private final ZserioType targetType;
    private final String name;
    private ResolvingState resolvingState = ResolvingState.UNRESOLVED;
    private ZserioType targetBaseType;

    /*private final List<ConstType> usedByConstList = new ArrayList<ConstType>();
    private final SortedSet<CompoundType> usedByCompoundList = new TreeSet<CompoundType>();
    private final SortedSet<ServiceType> usedByServiceList = new TreeSet<ServiceType>();
    private final List<ZserioType> usedTypeList = new ArrayList<ZserioType>();*/
}