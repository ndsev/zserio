package zserio.emit.doc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The class converts the classic documentation comment text into HTML format.
 */
class DocClassicToHtmlConverter
{
    /**
     * Converts invalid HTML characters in documentation comment text into escape sequences.
     *
     * Documentation comment text can contain HTML tags. These tags must be identified and passed through
     * without escaping.
     *
     * @param text Documentation comment text to convert.
     *
     * @return Converted string for HTML.
     */
    public static String convert(String text)
    {
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < text.length(); i++)
        {
            final String htmlTag = findHtmlTag(text, i);
            if (htmlTag != null)
            {
                // this is HTML tag, skip escaping
                output.append(htmlTag);
                i += htmlTag.length() - 1;
            }
            else
            {
                // this is normal character to escape
                output.append(escapeForHtml(text.charAt(i)));
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

    private static String findHtmlTag(String text, int startIndex)
    {
        if (text.charAt(startIndex) != '<')
            return null;

        StringBuilder foundTagBuilder = new StringBuilder("<");
        String foundTag = null;
        boolean foundStartWithParams = false;
        for (int i = startIndex + 1; i < text.length(); ++i)
        {
            final char character = text.charAt(i);
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
