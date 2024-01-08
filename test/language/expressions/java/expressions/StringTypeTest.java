package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.string_type.CHOOSER;
import expressions.string_type.STRING_CONSTANT;
import expressions.string_type.StringTypeExpression;

public class StringTypeTest
{
    @Test
    public void returnValue()
    {
        final StringTypeExpression stringTypeExpression = createStringTypeExpression(true);
        assertEquals(VALUE, stringTypeExpression.funcReturnValue());
    }

    @Test
    public void returnDefaultValue()
    {
        final StringTypeExpression stringTypeExpression = createStringTypeExpression(true);
        assertEquals(CHOOSER.CHOOSER ? STRING_CONSTANT.STRING_CONSTANT
                                     : FALSE + SPACE + STRING_CONSTANT.STRING_CONSTANT,
                stringTypeExpression.funcReturnDefaultValue());
    }

    @Test
    public void returnDefaultChosen()
    {
        final StringTypeExpression stringTypeExpression = createStringTypeExpression(true);
        assertEquals(CHOOSER.CHOOSER ? CHOSEN + SPACE + STRING_CONSTANT.STRING_CONSTANT : "",
                stringTypeExpression.funcReturnDefaultChosen());
    }

    @Test
    public void appendix()
    {
        final StringTypeExpression stringTypeExpression = createStringTypeExpression(false);
        assertEquals(APPEND + IX, stringTypeExpression.funcAppendix());
    }

    @Test
    public void appendToConst()
    {
        final StringTypeExpression stringTypeExpression = createStringTypeExpression(false);
        assertEquals(STRING_CONSTANT.STRING_CONSTANT + UNDERSCORE + APPEND + IX,
                stringTypeExpression.funcAppendToConst());
    }

    @Test
    public void valueOrLiteral()
    {
        final StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
        assertEquals(VALUE, stringTypeExpression1.funcValueOrLiteral());
        final StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
        assertEquals(LITERAL, stringTypeExpression2.funcValueOrLiteral());
    }

    @Test
    public void valueOrLiteralExpression()
    {
        final StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
        assertEquals(VALUE, stringTypeExpression1.funcValueOrLiteralExpression());
        final StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
        assertEquals(LITERAL + SPACE + EXPRESSION, stringTypeExpression2.funcValueOrLiteralExpression());
    }

    @Test
    public void valueOrConst()
    {
        final StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
        assertEquals(VALUE, stringTypeExpression1.funcValueOrConst());
        final StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
        assertEquals(STRING_CONSTANT.STRING_CONSTANT, stringTypeExpression2.funcValueOrConst());
    }

    @Test
    public void valueOrConstExpression()
    {
        final StringTypeExpression stringTypeExpression1 = createStringTypeExpression(true);
        assertEquals(VALUE, stringTypeExpression1.funcValueOrConstExpression());
        final StringTypeExpression stringTypeExpression2 = createStringTypeExpression(false);
        assertEquals(STRING_CONSTANT.STRING_CONSTANT + SPACE + EXPRESSION,
                stringTypeExpression2.funcValueOrConstExpression());
    }

    private static StringTypeExpression createStringTypeExpression(boolean hasValue)
    {
        final StringTypeExpression stringTypeExpression = new StringTypeExpression();
        stringTypeExpression.setHasValue(hasValue);
        if (hasValue)
            stringTypeExpression.setValue(VALUE);
        return stringTypeExpression;
    }

    private static final String VALUE = "value";
    private static final String APPEND = "append";
    private static final String IX = "ix";
    private static final String LITERAL = "literal";
    private static final String EXPRESSION = "expression";
    private static final String FALSE = "false";
    private static final String CHOSEN = "chosen";
    private static final String SPACE = " ";
    private static final String UNDERSCORE = "_";
}
