package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import expressions.modulo_operator.ModuloFunction;

public class ModuloOperatorTest
{
    @Test
    public void isValueDivBy4()
    {
        final ModuloFunction moduloFunction = new ModuloFunction();
        assertEquals(true, moduloFunction.funcIsValueDivBy4());
    }
}
