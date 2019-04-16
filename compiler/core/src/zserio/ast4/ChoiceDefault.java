package zserio.ast4;

import org.antlr.v4.runtime.Token;

/**
 * AST node for default case defined by choice types.
 */
public class ChoiceDefault extends AstNodeBase
{
    public ChoiceDefault(Token token, Field defaultField)
    {
        super(token);

        this.defaultField = defaultField;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitChoiceDefault(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        if (defaultField != null)
            defaultField.accept(visitor);
    }

    /**
     * Gets field defined by the default choice case.
     *
     * @return Field defined by the default choice case or null if the default case is not defined.
     */
    public Field getField()
    {
        return defaultField;
    }

    /**
     * Gets documentation comment associated to this RPC method.
     *
     * @return Documentation comment token associated to this RPC method.
     */
    /*public DocCommentToken getDocComment()
    {
        return getHiddenDocComment();
    }*/ // TODO:

    /**
     * Sets the choice type which is owner of the default case.
     *
     * @param choiceType Owner to set.
     */
    protected void setChoiceType(ChoiceType choiceType)
    {
        this.choiceType = choiceType;
    }

    private ChoiceType choiceType;
    private final Field defaultField;
}
