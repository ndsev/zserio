package functions_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class FunctionsWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void optionalReferencesInFunction()
    {
        String warning = "optional_references_in_function.zs:11:16: Function "
                + "'suspicionFunction' contains reference to optional field 'additionalValue'.";
        assertTrue(zserioWarnings.isPresent(warning));

        warning = "optional_references_in_function.zs:16:16: Function "
                + "'autoSuspicionFunction' contains reference to optional field 'autoAdditionalValue'.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
