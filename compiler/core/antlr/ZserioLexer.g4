lexer grammar ZserioLexer ;

channels { DOC }

// operators
AND             : '&' ;
ASSIGN          : '=' ;
BANG            : '!' ;
COLON           : ':' ;
COMMA           : ',' ;
DIVIDE          : '/' ;
DOT             : '.' ;
EQ              : '==' ;
GE              : '>=' ;
GT              : '>' ;
LBRACE          : '{' ;
LBRACKET        : '[' ;
LE              : '<=' ;
LOGICAL_AND     : '&&' ;
LOGICAL_OR      : '||' ;
LPAREN          : '(' ;
LSHIFT          : '<<' ;
LT              : '<' ;
MINUS           : '-' ;
MODULO          : '%' ;
MULTIPLY        : '*' ;
NE              : '!=' ;
OR              : '|' ;
PLUS            : '+' ;
QUESTIONMARK    : '?' ;
RBRACE          : '}' ;
RBRACKET        : ']' ;
RPAREN          : ')' ;
RSHIFT          : '>>' ;
SEMICOLON       : ';' ;
TILDE           : '~' ;
XOR             : '^' ;

// keywords
ALIGN               : 'align' ;
BIT_FIELD           : 'bit' ;
BOOL                : 'bool' ;
CASE                : 'case' ;
CHOICE              : 'choice' ;
CONST               : 'const' ;
DEFAULT             : 'default' ;
ENUM                : 'enum' ;
EXPLICIT            : 'explicit' ;
FLOAT16             : 'float16' ;
FLOAT32             : 'float32' ;
FLOAT64             : 'float64' ;
FUNCTION            : 'function' ;
IF                  : 'if' ;
IMPLICIT            : 'implicit' ;
IMPORT              : 'import' ;
INDEX               : '@index' ;
INT_FIELD           : 'int' ; // signed bitfield
INT16               : 'int16' ;
INT32               : 'int32' ;
INT64               : 'int64' ;
INT8                : 'int8' ;
LENGTHOF            : 'lengthof' ;
NUMBITS             : 'numbits' ;
ON                  : 'on' ;
OPTIONAL            : 'optional' ;
PACKAGE             : 'package' ;
RETURN              : 'return' ;
RPC                 : 'rpc' ;
SERVICE             : 'service' ;
SQL                 : 'sql' ;
SQL_DATABASE        : 'sql_database' ;
SQL_TABLE           : 'sql_table' ;
SQL_VIRTUAL         : 'sql_virtual' ;
SQL_WITHOUT_ROWID   : 'sql_without_rowid' ;
STREAM              : 'stream' ;
STRING              : 'string' ;
STRUCTURE           : 'struct' ;
SUBTYPE             : 'subtype' ;
SUM                 : 'sum' ;
UINT16              : 'uint16' ;
UINT32              : 'uint32' ;
UINT64              : 'uint64' ;
UINT8               : 'uint8' ;
UNION               : 'union' ;
USING               : 'using' ;
VALUEOF             : 'valueof' ;
VARINT              : 'varint' ;
VARINT16            : 'varint16' ;
VARINT32            : 'varint32' ;
VARINT64            : 'varint64' ;
VARUINT             : 'varuint' ;
VARUINT16           : 'varuint16' ;
VARUINT32           : 'varuint32' ;
VARUINT64           : 'varuint64' ;

// whitespaces
WS : [ \r\n\t\f] -> skip ; // TODO: what is the '\f'

// comments
DOC_COMMENT : '/**' .*? '*/' -> channel(DOC) ;
BLOCK_COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~[\r\n]* -> channel(HIDDEN) ;

// literals
BOOL_LITERAL : 'true' | 'false' ;

STRING_LITERAL : '"' ( '\\\\' | '\\"' | .)*? '"' ;

BINARY_LITERAL : [01]+ [bB] ;

OCTAL_LITERAL : '0' [0-7]+ ;

fragment HEX_PREFIX : '0' [xX] ;
fragment HEX_DIGIT : [0-9a-fA-F] ;
HEXADECIMAL_LITERAL : HEX_PREFIX HEX_DIGIT+ ;

fragment FLOAT_EXPONENT : [eE] [+\-]? [0-9]+ ;
fragment FLOAT_SUFFIX : [fF] ;
DOUBLE_LITERAL
    :   [0-9]+ FLOAT_EXPONENT
    |   [0-9]+ '.' [0-9]* FLOAT_EXPONENT?
    |   '.' [0-9]+ FLOAT_EXPONENT?
    ;
FLOAT_LITERAL : DOUBLE_LITERAL FLOAT_SUFFIX ;

DECIMAL_LITERAL
    :   [1-9][0-9]*
    |   '0' // special case - 0 is DECIMAL_LITERAL
    ;

// id
ID : [a-zA-Z_][a-zA-Z_0-9]* ;

// invalid input
INVALID_TOKEN : [a-zA-Z_0-9]+ ;
