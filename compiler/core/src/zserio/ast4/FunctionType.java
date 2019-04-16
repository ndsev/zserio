package zserio.ast4;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.Token;

import zserio.tools.ZserioToolPrinter;

/**
 * AST node for function types.
 *
 * Function types are Zserio types as well.
 */
public class FunctionType extends AstNodeBase implements ZserioType
{
    public FunctionType(Token token, Package pkg, ZserioType returnType, String name,
            Expression resultExpression)
    {
        super(token);

        this.pkg = pkg;
        this.returnType = returnType;
        this.name = name;
        this.resultExpression = resultExpression;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitFunction(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        returnType.accept(visitor);
        resultExpression.accept(visitor);
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

    /*@Override
    public void callVisitor(ZserioTypeVisitor visitor)
    {
        visitor.visitFunctionType(this);
    }*/

    /**
     * Gets unresolved function return Zserio type.
     *
     * @return Unresolved Zserio type defining the function return type.
     */
    public ZserioType getReturnType()
    {
        return returnType;
    }

    /**
     * Gets expression which represents function result.
     *
     * @return Function result expression.
     */
    public Expression getResultExpression()
    {
        return resultExpression;
    }

    @Override
    protected void check() throws ParserException
    {
        final ZserioType resolvedTypeReference = TypeReference.resolveType(returnType);

        // check result expression type
        final ZserioType resolvedReturnType = TypeReference.resolveBaseType(resolvedTypeReference);
        ExpressionUtil.checkExpressionType(resultExpression, resolvedReturnType);

        // fill used type list
        /*if (!ZserioTypeUtil.isBuiltIn(resolvedTypeReference))
            usedTypeList.add(resolvedTypeReference);*/ // TODO:

        // check usage of unconditional optional fields (this is considered as a warning)
        if (!resultExpression.containsFunctionCall() && !resultExpression.containsTernaryOperator())
        {
            final Set<Field> referencedFields = resultExpression.getReferencedSymbolObjects(Field.class);
            for (Field referencedField : referencedFields)
            {
                if (referencedField.getIsOptional())
                    ZserioToolPrinter.printWarning(resultExpression, "Function '" + name + "' contains " +
                            "unconditional optional fields.");
            }
        }
    }

    private final Package pkg;
    private final ZserioType returnType;
    private final String name;
    private final Expression resultExpression;

    private final List<ZserioType> usedTypeList = new ArrayList<ZserioType>();
}
