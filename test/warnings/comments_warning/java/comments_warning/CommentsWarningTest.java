package comments_warning;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import test_utils.ZserioWarnings;

public class CommentsWarningTest
{
    @BeforeClass
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioWarnings();
    }

    @Test
    public void unresolvedSeeTagReference()
    {
        assertTrue(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:8:4: " +
                "Documentation: Unresolved referenced symbol 'Unexisting'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:15:4: " +
                "Documentation: Unresolved referenced symbol 'Unexisting' for type 'Table'!"));
    }

    @Test
    public void unusedFieldComments()
    {
        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:11:11: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:55:45: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:61:45: Documentation comment is not used!"));
    }

    @Test
    public void unusedStructCommentById()
    {
        final String warning = "unused_struct_comment_by_id.zs:3:8: Documentation comment is not used!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    public void unusedStructCommentMultipleComments()
    {
        final String warning = "unused_struct_comment_multiple_comments.zs:5:9: "
                + "Documentation comment is not used!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 7;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
