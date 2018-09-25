package comments_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class CommentsErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void see_tag_error()
    {
        final String error = "see_tag_error.zs:6:4: Unresolved referenced symbol 'Unexisting' for type 'Test'!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unknown_tag_error()
    {
        final String error = "unknown_tag_error.zs:6:4: unexpected char: '@'";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unkown_tag_matching_prefix_error()
    {
        final String error = "unknown_tag_matching_prefix_error.zs:8:7: expecting 'e', found 'a'";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
