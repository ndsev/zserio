## Comments

### Standard Comments

Zserio supports the standard comment syntax of Java or C++. Single line comments start with `//` and extend
to the end of the line. A comments starting with `/*` is terminated by the next occurrence of `*/`, which may
or may not be on the same line.

**Example**
```
// This is a single-line comment.

/* This is an example
    of a multi-line comment
    spanning three lines. */
```

### Documentation Comments

To support inline documentation within a zserio module, multi-line comments starting with `/**` are treated as
special documentation comments. The idea and syntax is borrowed from Java(doc). A documentation comment is
associated to the following type or field definition. The documentation comment and the corresponding
definition may only be separated by whitespace.

**Example**

```
/**
* Traffic flow on roads.
*/
enum bit:2 Direction
{
    /** No traffic flow allowed. */
    NONE,

    /** Traffic allowed from start to end node. */
    POSITIVE,

    /** Traffic allowed from end to start node. */
    NEGATIVE,

    /** Traffic allowed in both directions. */
    BOTH
};
```

The documentation comments can contain special tags which is shown by the following example:

```
/**
* The tile contains a number of different elements
* grouped by feature classes, e.g. intersections, roads and
* so on...
*
* The presence of these members is indicated by the content
* mask in the header (please have a look at the
* following @see "documentation" headerDefinition page).
*
* @see headerDefinition
*
* @param level level number
* @param width width for the current tile
*
* @todo Update this comment.
*
* @deprecated
*/
```
The content of a documentation comment, excluding its delimiters, is parsed line by line. Each line is stripped
of leading whitespace, a sequence of asterisks (`*`), and more whitespace, if present. After stripping, a comment
is composed of one or more paragraphs, followed by zero or more tag blocks. Paragraphs are separated by blank
lines. The text in paragraphs can contain HTML formatting tags like `<ul>`, `<li>` or `</br>` directly.

A line starting with whitespace and a keyword preceded by an at-sign (`@`) is the beginning of a tag.

The following sections describe all supported tags in the documentation comments in detail.

#### See Tag

The `see` tag defines the link in generated documentation and has the following format:

```
@see "TEXT_ALIAS" TYPE.FIELD
```

The `TEXT_ALIAS` is text which will be shown in the generated documentation instead of the reference
`TYPE.FIELD`. This alias text is optional and can be omitted.

The `TYPE.FIELD` must be the valid reference to the field of the zserio type. The `FIELD` definition is
optional and can be omitted.

The `see` tag is the only tag which does not have to defined at the beginning of the line and can be embedded
directly in the comment text.

**Example**

```
/**
* Please see @see "black color" ColorEnumerationType.BLACK definition for more description.
*/
```

#### Param Tag

The `param` tag is used for documenting the arguments of a parameterized type. This tag has the following
format:

```
@param PARAM_NAME PARAM_DESCRIPTION
```

The `PARAM_NAME` defines the parameter name.

The `PARAM_DESCRIPTION` contains the parameter description. This description can be defined on multiple lines.

**Example**

```
/**
 * This type takes two arguments.
 *
 * @param arg1 The first argument.
 * @param arg2 The second argument.
 */
struct ParamType(Foo arg1, Bar arg2)
{
...
};
```

#### Todo Tag

The `todo` tag is used for documenting the action which should be done in the future. This tag has
the following format:

```
@todo ACTION_DESCRIPTION
```

The `ACTION_DESCRIPTION` contains arbitrary text. This text can be defined on multiple lines.

**Example**

```
/**
 * The text of the comment.
 *
 * @todo Don't forget to update this comment!
 */
```

#### Deprecated Tag

This tag assigns the documented zserio type as deprecated which means that this type is going to be invalid in
future versions of the schema. It has the following format:

```
@deprecated
```

[\[top\]](ZserioLanguageOverview.md#language-guide)
