## Expressions

The semantics of expression and the precedence rules for operators is the same as in Java, except where stated
otherwise. Zserio has a number of special operators which will be explained in detail below.

The following Java operators have no counterpart in zserio: `++`, `--`, `>>>` and `instanceof`.

### Unary Operators

#### Boolean Negation
The negation operator `!` is defined for boolean expressions.

#### Integer Operators
For integer expressions, there are `+` (unary plus), `-` (unary minus) and `~` (bitwise complement).

#### lengthof Operator

The `lengthof` operator may be applied to an array member and returns the actual length (i.e. number
of elements of an array. Thus, given `int32 a[5]`, the expression `lengthof` a evaluates to `5`. This is not
particularly useful for fixed or variable length arrays, but it is the only way to refer to the length of an
implicit length array.

**Example**
```
struct LengthOfOperator
{
    uint8   implicitArray[];

    function uint32 getLengthOfImplicitArray()
    {
        return lengthof(implicitArray);
    }
};
```

#### sum Operator

The `sum` operator is defined for arrays with integer element type (this includes bit fields). `sum(a)`
evaluates to the sum of all elements of the array `a`.

**Example**
```
struct SumOperator
{
    uint8   fixedArray[10];

    function uint16 getSumFixedArray()
    {
        return sum(fixedArray);
    }
};
```

#### numbits Operator

The `numbits(value)` operator is defined for unsigned integers as minimum number of bits required to encode
`value-1`. The returned number is of type `uint8`. The `numbits` operator returns `1` if applied to value `0`
or `1`.

The following table shows the results of the `numbits` operator applied some common values:

```
numbits(0) = 1
numbits(1) = 1
numbits(2) = 1
numbits(3) = 2
numbits(4) = 2
numbits(8) = 3
numbits(16) = 4
```

**Example**
```
struct NumBitsOperator
{
    uint8   value8;

    function uint8 getNumBits8()
    {
        return numbits(value8);
    }
};
```

### Binary Operators

#### Arithmetic Operators

The integer arithmetic operations include `+` (addition), `-` (subtraction), `*` (multiplication),
`/` (division), `%` (modulo). In addition, zserio also supports shift operators `<<` and `>>`.

#### Relational Operators

The following relational operators for integer expressions are supported: `==` (equal to),
`!=` (not equal to), `<` (less than), `<=` (less than or equal), `>` (greater than),
`>=` (greater than or equal).

The equality operators `==` and `!=` may be applied to any type.

#### Boolean Operators

The boolean operators `&&` (and) and `||` (or) may be applied to boolean expressions.

##### Bit Operators

The bit operators `&` (bitwise and), `|` (bitwise or), `^` (bitwise exclusive or) may be applied to integer
types.

#### Postfix Operators

The postfix operators include `[]` (array index), `()` (instantiation with argument list or function call) and
`.` (member access).

### Ternary Operators

A conditional expression `booleanExpr ? expr1 : expr2` has the value of `expr1` when `booleanExpr` is true.
Otherwise, it has the value of `expr2`.

### Operator Precedence

In the following list, operators are grouped by precedence in ascending order. Operators on the bottom line
have the highest precedence and are evaluated first. All operators on the same line have the same precedence
and are evaluated left to right, except assignment operators which are evaluated right to left.

- `?` `:`
- `||`
- `&&`
- `|`
- `^`
- `&`
- `==` `!=`
- `<` `>` `<=` `>=`
- `<<` `>>`
- `+` `-`
- `*` `/` `%`
- unary `+` `-` `~` `!`
- `lengthof` `sum` `numbits`
- `[]` `()` `.`

[\[top\]](ZserioLanguageOverview.md#language-guide)
