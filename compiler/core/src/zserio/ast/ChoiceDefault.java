package zserio.ast;

import zserio.antlr.ZserioParserTokenTypes;
import zserio.antlr.util.BaseTokenAST;
import zserio.antlr.util.ParserException;

/**
 * AST node for default case defined by choice types.
 */
public class ChoiceDefault extends TokenAST
{
    /**
     * Sets the choice type which is owner of the default case.
     *
     * @param choiceType Owner to set.
     */
    public void setChoiceType(ChoiceType choiceType)
    {
        this.choiceType = choiceType;
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

    @Override
    protected boolean evaluateChild(BaseTokenAST child) throws ParserException
    {
        switch (child.getType())
        {
        case ZserioParserTokenTypes.FIELD:
            if (!(child instanceof Field))
                return false;
            defaultField = (Field)child;
            choiceType.addField(defaultField);
            break;

        default:
            return false;
        }

        return true;
    }

    private static final long serialVersionUID = 5346352027746617282L;

    private ChoiceType  choiceType;
    private Field       defaultField;
}
