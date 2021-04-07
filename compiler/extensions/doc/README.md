# Documentation Generator for Zserio

Zserio extension which generates HTML documentation from the Zserio schema.

The documentation contains all entities defined in the schema together with well formatted comments,
provides easy navigation via cross references and implements a simple quick search.

It can also generate collaboration diagrams as SVG images.

## Documentation comments

Zserio language distinguish two formats of documentation comments -
[classic documentation comment](../../../doc/ZserioLanguageOverview.md#classic-documentation-comments) `/** */`
and [markdown documentation comment](../../../doc/ZserioLanguageOverview.md#markdown-documentation-comments)
`/*! */`.

Both types of comments can be written either as *sticky* comments or *floating* comments. Sticky comment is
simply a comment, which is not followed by a blank line and thus it's stuck to the commented entity.
Documentation Generator renders such comments in a comment-like style within the rendered code section.
On the other hand, floating comments (which are followed by a blank line) are treated as self-standing comments
and are rendered as well formatted text before the code section.

The logic of *sticky* and *floating* comments allows authors of the schema to control rendering of the
HTML documentation. Since the floating comments are treated as self-standing comments, they can be used
as self-standing sections wherever in the document, even at the beginning and at the end.

Documentation Generator also distinguish one-liner comments, which are rendered as one-liners also in the
resulting HTML documentation.

## Resources in Markdown Comments

Documentation Generator handles resources referenced in markdown comments. When the resource is available on
the local file system during generation, it's copied to the generated HTML documentation. When it is another
Zserio source file from the current schema, it's recognized and the links are properly mapped to point to
the generated HTML. When the resource is a markdown document, the document is converted to HTML too and is
saved within the generated HTML documentation. The generator fires a warning when some resource is missing or
when there are other problems with resources.
