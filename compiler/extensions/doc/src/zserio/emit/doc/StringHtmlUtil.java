package zserio.emit.doc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The common string manipulation functions which handle special HTML characters.
 */
public class StringHtmlUtil
{
    /**
     * Converts invalid HTML characters into escape sequences.
     *
     * @param string String to convert.
     *
     * @return Converted string for HTML.
     */
    public static String escapeForHtml(String string)
    {
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
            output.append(escapeForHtml(string.charAt(i)));

        return output.toString();
    }

    /**
     * Converts invalid HTML characters in documentation comments into escape sequences.
     *
     * Documentation comments can contain HTML tags. These tags must be identified and passed through without
     * escaping.
     *
     * @param string Documentation comments to convert.
     *
     * @return Converted string for HTML.
     */
    public static String escapeCommentsForHtml(String string)
    {
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
        {
            final String htmlTag = findHtmlTag(string, i);
            if (htmlTag != null)
            {
                // this is HTML tag, skip escaping
                output.append(htmlTag);
                i += htmlTag.length() - 1;
            }
            else
            {
                // this is normal character to escape
                output.append(escapeForHtml(string.charAt(i)));
            }
        }

        return output.toString();
    }

    private static String escapeForHtml(char character)
    {
        switch (character)
        {
        case '<':
            return "&lt;";

        case '>':
            return "&gt;";

        case '&':
            return "&amp;";

        case '"':
            return "&quot;";

        default:
            return Character.toString(character);
        }
    }

    private static String findHtmlTag(String string, int startIndex)
    {
        if (string.charAt(startIndex) != '<')
            return null;

        StringBuilder foundTagBuilder = new StringBuilder("<");
        String foundTag = null;
        boolean foundStartWithParams = false;
        for (int i = startIndex + 1; i < string.length(); ++i)
        {
            final char character = string.charAt(i);
            if (!foundStartWithParams && character == ' ')
            {
                // start tag with parameters
                if (!startHtmlTagList.contains(foundTagBuilder.toString() + ">"))
                    break;

                foundTagBuilder.append(character);
                foundStartWithParams = true;
            }
            else
            {
                foundTagBuilder.append(character);
                if (character == '>')
                {
                    if (foundStartWithParams)
                    {
                        // start tag with parameters
                        foundTag = foundTagBuilder.toString();
                    }
                    else
                    {
                        // start tag without parameters or end tag
                        final String foundString = foundTagBuilder.toString();
                        if (startHtmlTagList.contains(foundString) || endHtmlTagList.contains(foundString))
                            foundTag = foundString;
                    }
                    break;
                }
            }
        }

        return foundTag;
    }

    private static final String[] startHtmlTags = new String[]
    {
        "<a>",
        "<abbr>",
        "<address>",
        "<area>",
        "<article>",
        "<aside>",
        "<audio>",
        "<b>",
        "<base>",
        "<bdi>",
        "<bdo>",
        "<blockquote>",
        "<body>",
        "<br>",
        "<button>",
        "<canvas>",
        "<caption>",
        "<cite>",
        "<code>",
        "<col>",
        "<colgroup>",
        "<data>",
        "<datalist>",
        "<dd>",
        "<del>",
        "<details>",
        "<dfn>",
        "<dialog>",
        "<div>",
        "<dl>",
        "<dt>",
        "<em>",
        "<embed>",
        "<fieldset>",
        "<figcaption>",
        "<figure>",
        "<footer>",
        "<form>",
        "<h1>",
        "<h2>",
        "<h3>",
        "<h4>",
        "<h5>",
        "<h6>",
        "<head>",
        "<header>",
        "<hgroup>",
        "<hr>",
        "<html>",
        "<i>",
        "<iframe>",
        "<img>",
        "<input>",
        "<ins>",
        "<kbd>",
        "<keygen>",
        "<label>",
        "<legend>",
        "<li>",
        "<link>",
        "<main>",
        "<map>",
        "<mark>",
        "<menu>",
        "<menuitem>",
        "<meta>",
        "<meter>",
        "<nav>",
        "<noscript>",
        "<object>",
        "<ol>",
        "<optgroup>",
        "<option>",
        "<output>",
        "<p>",
        "<param>",
        "<pre>",
        "<progress>",
        "<q>",
        "<rb>",
        "<rp>",
        "<rt>",
        "<rtc>",
        "<ruby>",
        "<s>",
        "<samp>",
        "<script>",
        "<section>",
        "<select>",
        "<small>",
        "<source>",
        "<span>",
        "<strong>",
        "<style>",
        "<sub>",
        "<summary>",
        "<sup>",
        "<table>",
        "<tbody>",
        "<td>",
        "<template>",
        "<textarea>",
        "<tfoot>",
        "<th>",
        "<thead>",
        "<time>",
        "<title>",
        "<tr>",
        "<track>",
        "<u>",
        "<ul>",
        "<var>",
        "<video>",
        "<wbr>",
    };

    private static final String[] endHtmlTags = new String[]
    {
        "</a>",
        "</abbr>",
        "</address>",
        "</area>",
        "</article>",
        "</aside>",
        "</audio>",
        "</b>",
        "</base>",
        "</bdi>",
        "</bdo>",
        "</blockquote>",
        "</body>",
        "</br>",
        "</button>",
        "</canvas>",
        "</caption>",
        "</cite>",
        "</code>",
        "</col>",
        "</colgroup>",
        "</data>",
        "</datalist>",
        "</dd>",
        "</del>",
        "</details>",
        "</dfn>",
        "</dialog>",
        "</div>",
        "</dl>",
        "</dt>",
        "</em>",
        "</embed>",
        "</fieldset>",
        "</figcaption>",
        "</figure>",
        "</footer>",
        "</form>",
        "</h1>",
        "</h2>",
        "</h3>",
        "</h4>",
        "</h5>",
        "</h6>",
        "</head>",
        "</header>",
        "</hgroup>",
        "</hr>",
        "</html>",
        "</i>",
        "</iframe>",
        "</img>",
        "</input>",
        "</ins>",
        "</kbd>",
        "</keygen>",
        "</label>",
        "</legend>",
        "</li>",
        "</link>",
        "</main>",
        "</map>",
        "</mark>",
        "</menu>",
        "</menuitem>",
        "</meta>",
        "</meter>",
        "</nav>",
        "</noscript>",
        "</object>",
        "</ol>",
        "</optgroup>",
        "</option>",
        "</output>",
        "</p>",
        "</param>",
        "</pre>",
        "</progress>",
        "</q>",
        "</rb>",
        "</rp>",
        "</rt>",
        "</rtc>",
        "</ruby>",
        "</s>",
        "</samp>",
        "</script>",
        "</section>",
        "</select>",
        "</small>",
        "</source>",
        "</span>",
        "</strong>",
        "</style>",
        "</sub>",
        "</summary>",
        "</sup>",
        "</table>",
        "</tbody>",
        "</td>",
        "</template>",
        "</textarea>",
        "</tfoot>",
        "</th>",
        "</thead>",
        "</time>",
        "</title>",
        "</tr>",
        "</track>",
        "</u>",
        "</ul>",
        "</var>",
        "</video>",
        "</wbr>"
    };

    private static final Set<String> startHtmlTagList = new HashSet<String>(Arrays.asList(startHtmlTags));
    private static final Set<String> endHtmlTagList = new HashSet<String>(Arrays.asList(endHtmlTags));
}
