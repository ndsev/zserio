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
SEMICOLON       : ';' ;
TILDE           : '~' ;
XOR             : '^' ;

// keywords
ALIGN               : 'align' ;
BIT_FIELD           : 'bit' ;
BOOL                : 'bool' ;
BITMASK             : 'bitmask' ;
BYTES               : 'bytes' ;
CASE                : 'case' ;
CHOICE              : 'choice' ;
CONST               : 'const' ;
DEFAULT             : 'default' ;
ENUM                : 'enum' ;
EXPLICIT            : 'explicit' ;
EXTERN              : 'extern' ;
FLOAT16             : 'float16' ;
FLOAT32             : 'float32' ;
FLOAT64             : 'float64' ;
FUNCTION            : 'function' ;
IF                  : 'if' ;
IMPLICIT            : 'implicit' ;
IMPORT              : 'import' ;
INDEX               : '@index' ;
INSTANTIATE         : 'instantiate' ;
INT_FIELD           : 'int' ; // signed bitfield
INT16               : 'int16' ;
INT32               : 'int32' ;
INT64               : 'int64' ;
INT8                : 'int8' ;
ISSET               : 'isset' ;
LENGTHOF            : 'lengthof' ;
NUMBITS             : 'numbits' ;
ON                  : 'on' ;
OPTIONAL            : 'optional' ;
PACKAGE             : 'package' ;
PACKED              : 'packed' ;
PUBSUB              : 'pubsub' ;
PUBLISH             : 'publish' ;
RETURN              : 'return' ;
RULE                : 'rule' ;
RULE_GROUP          : 'rule_group' ;
SERVICE             : 'service' ;
SQL                 : 'sql' ;
SQL_DATABASE        : 'sql_database' ;
SQL_TABLE           : 'sql_table' ;
SQL_VIRTUAL         : 'sql_virtual' ;
SQL_WITHOUT_ROWID   : 'sql_without_rowid' ;
STRING              : 'string' ;
STRUCTURE           : 'struct' ;
SUBSCRIBE           : 'subscribe' ;
SUBTYPE             : 'subtype' ;
TOPIC               : 'topic' ;
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
VARSIZE             : 'varsize' ;
VARUINT             : 'varuint' ;
VARUINT16           : 'varuint16' ;
VARUINT32           : 'varuint32' ;
VARUINT64           : 'varuint64' ;
COMPAT_VERSION      : 'zserio_compatibility_version' ;

// whitespaces
WS : [\r\n\f\t ] -> skip ;

// comments
DOC_COMMENT : '/**' .*? '*/' -> channel(DOC) ;
MARKDOWN_COMMENT : '/*!' .*? '!'?'*/' -> channel(DOC) ;
BLOCK_COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;
LINE_COMMENT : '//' ~[\r\n\f]* -> channel(HIDDEN) ;

// literals
BOOL_LITERAL : 'true' | 'false' ;

fragment STRING_CHARACTER
    :   ~["\\\r\n\f]
    |   '\\' ["\\rnft]
    |   '\\u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    |   '\\x' HEX_DIGIT HEX_DIGIT
    |   '\\0' [0-3]? OCTAL_DIGIT OCTAL_DIGIT?
    ;
STRING_LITERAL : '"' STRING_CHARACTER* '"' ;

BINARY_LITERAL : [01]+ [bB] ;

fragment OCTAL_DIGIT : [0-7] ;
OCTAL_LITERAL : '0' OCTAL_DIGIT+ ;

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

// invalid string literal
INVALID_STRING_LITERAL : '"' (~["])* '"'?;

// invalid token
INVALID_TOKEN : [a-zA-Z_0-9]+ ;
