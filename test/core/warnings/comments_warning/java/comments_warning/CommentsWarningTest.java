package comments_warning;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import test_utils.ZserioErrorOutput;

public class CommentsWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void docCommentFormat()
    {
        assertTrue(zserioWarnings.isPresent("doc_comment_format.zs:4:4: "
                + "Documentation: no viable alternative at input")); // '\\n * /**' won't work under Windows
    }

    @Test
    public void markdownCommentWithWrongTerminator()
    {
        assertTrue(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:3:1: "
                + "Markdown documentation comment should be terminated by '!*/'."));

        assertTrue(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:7:1: "
                + "Markdown documentation comment should be terminated by '!*/'."));

        assertTrue(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:21:1: "
                + "Markdown documentation comment should be terminated by '!*/'."));

        assertTrue(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:26:5: "
                + "Markdown documentation comment should be terminated by '!*/'."));

        assertTrue(zserioWarnings.isPresent("markdown_comment_with_wrong_terminator.zs:38:5: "
                + "Markdown documentation comment should be terminated by '!*/'."));
    }

    @Test
    public void unresolvedMarkdownSeeTagReference()
    {
        assertTrue(zserioWarnings.isPresent("unresolved_markdown_see_tag_reference.zs:7:5: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_markdown_see_tag_reference.zs:14:41: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unknown'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_markdown_see_tag_reference.zs:22:34: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unknown'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_markdown_see_tag_reference.zs:27:23: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_markdown_see_tag_reference.zs:33:6: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_markdown_see_tag_reference.zs:38:20: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unknown'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_markdown_see_tag_reference.zs:42:46: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unknown.Unknown'!"));
    }

    @Test
    public void unresolvedSeeTagInTemplatedStruct()
    {
        assertTrue(zserioWarnings.isPresent("unresolved_see_tag_in_templated_struct.zs:3:5: "
                + "Documentation: Unresolved referenced symbol 'unknown'!"));
    }

    @Test
    public void unresolvedSeeTagReference()
    {
        assertTrue(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:8:4: "
                + "Documentation: Unresolved referenced symbol 'Unexisting'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:9:4: "
                + "Documentation: Unresolved referenced symbol 'comments_warning.unexisting_package'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:16:4: "
                + "Documentation: Unresolved referenced symbol 'unexisting' for type 'Table'!"));

        assertTrue(zserioWarnings.isPresent("unresolved_see_tag_reference.zs:17:4: "
                + "Documentation: Unresolved referenced symbol 'unexisting' for type 'Table'!"));
    }

    @Test
    public void unusedFieldComments()
    {
        assertTrue(
                zserioWarnings.isPresent("unused_field_comments.zs:11:11: Documentation comment is not used."));

        assertTrue(
                zserioWarnings.isPresent("unused_field_comments.zs:55:45: Documentation comment is not used."));

        assertTrue(
                zserioWarnings.isPresent("unused_field_comments.zs:61:45: Documentation comment is not used."));
    }

    @Test
    public void unusedStructCommentById()
    {
        final String warning = "unused_struct_comment_by_id.zs:3:8: Documentation comment is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    @Test
    public void unusedStructCommentMultipleComments()
    {
        final String warning = "unused_struct_comment_multiple_comments.zs:5:9: "
                + "Documentation comment is not used.";
        assertTrue(zserioWarnings.isPresent(warning));
    }

    private static ZserioErrorOutput zserioWarnings;
}
