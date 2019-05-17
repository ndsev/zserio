package zserio.ast;

import org.antlr.v4.runtime.Token;


/**
 * AST node for default case defined by choice types.
 */
public class ChoiceDefault extends AstNodeWithDoc
{
    /**
     * Constructor.
     *
     * @param token        ANTLR4 token to localize AST node in the sources.
     * @param defaultField Default field associated to this default case or null if it's not defined.
     * @param docComment   Documentation comment belonging to this node.
     */
    public ChoiceDefault(Token token, Field defaultField, DocComment docComment)
    {
        super(token, docComment);

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

        super.visitChildren(visitor);
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

    private final Field defaultField;
}
