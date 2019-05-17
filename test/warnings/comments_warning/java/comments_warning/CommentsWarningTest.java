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
        final String warning = "unresolved_see_tag_reference.zs:6:4: " +
                "Documentation: Unresolved referenced symbol 'Unexisting' for type 'Test'!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedFieldComments()
    {
        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:11:11: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
                "unused_field_comments.zs:18:5: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
                "unused_field_comments.zs:26:5: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
                "unused_field_comments.zs:35:9: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
                "unused_field_comments.zs:43:5: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
                "unused_field_comments.zs:52:9: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
                "unused_field_comments.zs:54:9: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
                "unused_field_comments.zs:55:18: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:60:9: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:61:18: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:64:14: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:74:9: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:75:18: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:81:18: Documentation comment is not used!"));

        assertTrue(zserioWarnings.isPresent(
            "unused_field_comments.zs:91:5: Documentation comment is not used!"));
    }

    @Test
    public void unusedStructCommentById()
    {
        final String warning = "unused_struct_comment_by_id.zs:3:8: Documentation comment is not used!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    public void unusedStructCommentMultipleComments()
    {
        final String warning = "unused_struct_comment_multiple_comments.zs:3:1: "
                + "Documentation comment is not used!";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void checkNumberOfWarnings()
    {
        final int expectedNumberOfWarnings = 18;
        assertEquals(expectedNumberOfWarnings, zserioWarnings.getCount());
    }

    private static ZserioWarnings zserioWarnings;
}
