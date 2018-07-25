## Literals

The zserio syntax for literal values is similar to the Java syntax. There are no character literals, only
string literals with the usual escape syntax. Integer literals can use decimal, hexadecimal, octal or
binary notation.

Type        | Value
----------- | --------------------------------
Boolean     | `true`, `false`
Decimal     | `100`, `4711`, `255`, `-3`, `+2`
Hexadecimal | `0xCAFEBABE`, `0Xff`, `-0xEF`
Octal       | `044`, `0377`, `-010`
Binary      | `111b`, `110b`, `001B`, `-1010b`
Float       | `3.14`
String      | `"You"`

Hexadecimal digits and the `x` prefix as well as the `b` suffix for binary types are case-insensitive.
Signing literals by using `-` or `+` prefix. Signs are not applicable for string-literals.

[[top]](ZserioLanguageOverview.md#language-guide)
