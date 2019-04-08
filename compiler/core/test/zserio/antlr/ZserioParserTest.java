package zserio.antlr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import static org.junit.Assert.*;
import org.junit.Test;

public class ZserioParserTest
{
    @Test
    public void emptyTranslationUnit()
    {
        checkParseTree("translationUnit", "", "(translationUnit <EOF>)");
    }

    @Test
    public void packageDeclaration()
    {
        checkParseTree("packageDeclaration", "package test.pkg;",
                "(packageDeclaration package (qualifiedName test . pkg) ;)");

        assertParseError("packageDeclaration", "package test.*;", "mismatched input '*' expecting ID");
    }

    @Test
    public void importDeclaration()
    {
        checkParseTree("importDeclaration", "import pkg.*;",
                "(importDeclaration import (importName pkg . *) ;)");
        checkParseTree("importDeclaration", "import pkg1.pkg2;",
                "(importDeclaration import (importName pkg1 . pkg2) ;)");
        checkParseTree("importDeclaration", "import pkg1.pkg2.*;",
                "(importDeclaration import (importName pkg1 . pkg2 . *) ;)");

        assertParseError("importDeclaration", "import pkg;", "mismatched input ';' expecting '.'");
        assertParseError("importDeclaration", "import *;", "mismatched input '*' expecting ID");
    }

    @Test
    public void constDeclaration()
    {
        checkParseTree("constDeclaration", "const uint32 C = 10;",
                "(constDeclaration const (typeName (builtinType (intType uint32))) C = " +
                        "(constantExpression (expression (primary (literal 10)))) ;)");
    }

    @Test
    public void subtypeDeclaration()
    {
        checkParseTree("subtypeDeclaration", "subtype uint32 Subtype;",
                "(subtypeDeclaration subtype (typeName (builtinType (intType uint32))) Subtype ;)");
    }

    @Test
    public void parameterList()
    {
        checkParseTree("parameterList", "(uint32 id1, bool id2)",
                "(parameterList ( (parameterDefinition (typeName (builtinType (intType uint32))) id1) , " +
                        "(parameterDefinition (typeName (builtinType (boolType bool))) id2) ))");
    }

    @Test
    public void fieldAlignment()
    {
        checkParseTree("fieldAlignment", "align(15):", "(fieldAlignment align ( 15 ) :)");

        assertParseError("fieldAlignment", "align(\"15\"):",
                "mismatched input '\"15\"' expecting DECIMAL_LITERAL");
        assertParseError("fieldAlignment", "align(15.4):", "mismatched input '15.4' expecting DECIMAL_LITERAL");
    }

    @Test
    public void fieldOffset()
    {
        checkParseTree("fieldOffset", "10:",
                "(fieldOffset (expression (primary (literal 10))) :)");
        checkParseTree("fieldOffset", "id1:",
                "(fieldOffset (expression (primary id1)) :)");
        checkParseTree("fieldOffset", "id1.id2:",
                "(fieldOffset (expression (expression (primary id1)) . id2) :)");

        assertParseError("fieldOffset", "uint32:", "mismatched input 'uint32' expecting {"); // expression
    }

    @Test
    public void fieldTypeId()
    {
        checkParseTree("fieldTypeId", "uint32 value",
                "(fieldTypeId (typeReference (builtinType (intType uint32))) value)");
        checkParseTree("fieldTypeId", "uint32 value",
                "(fieldTypeId (typeReference (builtinType (intType uint32))) value)");
        checkParseTree("fieldTypeId", "implicit uint32 value[]",
                "(fieldTypeId implicit (typeReference (builtinType (intType uint32))) value [ ])");
        checkParseTree("fieldTypeId", "uint32 value[22]",
                "(fieldTypeId (typeReference (builtinType (intType uint32))) value (fieldArrayRange [ " +
                        "(expression (primary (literal 22))) ]))");

        assertParseError("fieldTypeId", "uint32 bool", "mismatched input 'bool' expecting ID");
        assertParseError("fieldTypeId", "implicit uint32 value", "mismatched input '<EOF>' expecting '['");
    }

    @Test
    public void fieldInitializer()
    {
        checkParseTree("fieldInitializer", "= 12",
                "(fieldInitializer = (constantExpression (expression (primary (literal 12)))))");
        checkParseTree("fieldInitializer", "= \"string\"",
                "(fieldInitializer = (constantExpression (expression (primary (literal \"string\")))))");
    }

    @Test
    public void fieldOptionalClause()
    {
        checkParseTree("fieldOptionalClause", "if a != 0",
                "(fieldOptionalClause if (expression (expression (primary a)) != " +
                        "(expression (primary (literal 0)))))");
    }

    @Test
    public void fieldConstraint()
    {
        checkParseTree("fieldConstraint", ": hasValue", "(fieldConstraint : (expression (primary hasValue)))");
    }

    @Test
    public void structureFieldDefinition()
    {
        checkParseTree("structureFieldDefinition",
                "align (10):" +
                "offset:" +
                "    uint32 value = 10 if hasValue : value > 0;",
                "(structureFieldDefinition " +
                        "(fieldAlignment align ( 10 ) :) " +
                        "(fieldOffset (expression (primary offset)) :) " +
                        "(fieldTypeId (typeReference (builtinType (intType uint32))) value) " +
                        "(fieldInitializer = (constantExpression (expression (primary (literal 10))))) " +
                        "(fieldOptionalClause if (expression (primary hasValue))) " +
                        "(fieldConstraint : (expression (expression (primary value)) > " +
                                "(expression (primary (literal 0))))) " +
                        ";)");
        checkParseTree("structureFieldDefinition", "optional string description;",
                "(structureFieldDefinition optional " +
                "(fieldTypeId (typeReference (builtinType (stringType string))) description) ;)");

        // cannot use both optional keyword and fieldOptionalClause
        assertParseError("structureDeclaration",
                "struct OptionalError { bool hasField; optional uint32 field if hasField; }",
                "mismatched input 'if' expecting {");
    }

    @Test
    public void choiceCases()
    {
        checkParseTree("choiceCases", "case 10: uint32 field;",
                "(choiceCases (choiceCase case (expression (primary (literal 10))) :) " +
                "(choiceFieldDefinition (fieldTypeId (typeReference (builtinType (intType uint32))) " +
                        "field) ;))");
        checkParseTree("choiceCases",
                "case 10:\n" +
                "case 11:\n" +
                "case 22:\n" +
                "    string value;",
                "(choiceCases " +
                "(choiceCase case (expression (primary (literal 10))) :) " +
                "(choiceCase case (expression (primary (literal 11))) :) " +
                "(choiceCase case (expression (primary (literal 22))) :) " +
                "(choiceFieldDefinition (fieldTypeId (typeReference (builtinType (stringType string))) " +
                        "value) ;))");
    }

    @Test
    public void choiceDefault()
    {
        checkParseTree("choiceDefault", "default: int8 field;",
                "(choiceDefault default : " +
                "(choiceFieldDefinition (fieldTypeId (typeReference (builtinType (intType int8))) field) ;))");
    }

    @Test
    public void enumDeclaration()
    {
        checkParseTree("enumDeclaration", "enum uint8 E { ONE = 1, TWO }",
                "(enumDeclaration enum (typeName (builtinType (intType uint8))) E " +
                "{ (enumItem ONE = (constantExpression (expression (primary (literal 1))))) , " +
                        "(enumItem TWO) })");

        // trailing COMMA is allowed!
        checkParseTree("enumDeclaration", "enum uint8 E { ONE = 1, TWO, }",
                "(enumDeclaration enum (typeName (builtinType (intType uint8))) E " +
                "{ (enumItem ONE = (constantExpression (expression (primary (literal 1))))) , " +
                         "(enumItem TWO) , })");
    }

    @Test
    public void sqlTableFieldDefinition()
    {
        checkParseTree("sqlTableFieldDefinition", "uint32 field;",
                "(sqlTableFieldDefinition (typeReference (builtinType (intType uint32))) field ;)");

        checkParseTree("sqlTableFieldDefinition", "sql_virtual uint32 field;",
                "(sqlTableFieldDefinition sql_virtual (typeReference (builtinType (intType uint32))) field ;)");

        checkParseTree("sqlTableFieldDefinition", "uint32 field sql \"PRIMARY KEY\";",
                "(sqlTableFieldDefinition (typeReference (builtinType (intType uint32))) field " +
                        "(sqlConstraint sql \"PRIMARY KEY\") ;)");
    };

    @Test
    public void sqlConstraintDefinition()
    {
        checkParseTree("sqlConstraintDefinition", "sql \"languageid='languageCode', notindexed='frequency'\";",
                "(sqlConstraintDefinition (sqlConstraint sql " +
                        "\"languageid='languageCode', notindexed='frequency'\") ;)");
    }

    @Test
    public void sqlWithoutRowId()
    {
        checkParseTree("sqlWithoutRowId", "sql_without_rowid;", "(sqlWithoutRowId sql_without_rowid ;)");
    }

    @Test
    public void sqlDatabaseFieldDefinition()
    {
        checkParseTree("sqlDatabaseFieldDefinition", "SomeTable someTable;",
                "(sqlDatabaseFieldDefinition (sqlTableReference (qualifiedName SomeTable)) someTable ;)");

        checkParseTree("sqlDatabaseFieldDefinition", "org.pkg.SomeTable someTable;",
                "(sqlDatabaseFieldDefinition (sqlTableReference (qualifiedName org . pkg . SomeTable)) " +
                        "someTable ;)");
    }

    @Test
    public void rpcDeclaration()
    {
        checkParseTree("rpcDeclaration", "rpc Response method(Request);",
                "(rpcDeclaration rpc (qualifiedName Response) method ( (qualifiedName Request) ) ;)");
        checkParseTree("rpcDeclaration", "rpc stream Response method(Request);",
                "(rpcDeclaration rpc stream (qualifiedName Response) method ( (qualifiedName Request) ) ;)");
        checkParseTree("rpcDeclaration", "rpc Response method(stream Request);",
                "(rpcDeclaration rpc (qualifiedName Response) method ( stream (qualifiedName Request) ) ;)");
        checkParseTree("rpcDeclaration", "rpc stream Response method(stream Request);",
                "(rpcDeclaration rpc stream (qualifiedName Response) method " +
                        "( stream (qualifiedName Request) ) ;)");
    }

    @Test
    public void functionDefinition()
    {
        checkParseTree("functionDefinition", "function RetType testFunc() { return 0; }",
                "(functionDefinition function (functionType (typeName (qualifiedName RetType))) " +
                        "(functionName testFunc) ( ) " +
                        "(functionBody { return (expression (primary (literal 0))) ; }))");
        assertParseError("functionDefinition", "function Parameterized() testFunc() { return 0; }",
                "missing ID at '('");
        assertParseError("functionDefinition", "function Parameterized testFunc(int32) { return 0; }",
                "extraneous input 'int32' expecting ')'");
        assertParseError("functionDefinition", "function Parameterized testFunc(int32 arg) { return 0; }",
                "mismatched input 'int32' expecting {"); // TODO: why antlr4 does NOT report "expecting ')'"?
        assertParseError("functionDefinition", "function int32 testFunc(int32 arg) { return 0; }",
                "mismatched input 'int32' expecting ')'"); // TODO: and why this works?
    }

    private static class ThrowingErrorListener extends BaseErrorListener
    {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine, String msg,
                RecognitionException e) throws ParseCancellationException
        {
            throw new ParseCancellationException(msg);
        }
    }

    private Zserio4Parser createParser(String input)
    {
        final ThrowingErrorListener throwingErrorListener = new ThrowingErrorListener();
        final Zserio4Lexer lexer = new Zserio4Lexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(throwingErrorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final Zserio4Parser parser = new Zserio4Parser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(throwingErrorListener);
        return parser;
    }

    private void assertParseError(String ruleName, String input, String errorSubstring)
    {
        final Zserio4Parser parser = createParser(input);
        String error = "";
        try
        {
            Method rule = Zserio4Parser.class.getMethod(ruleName);
            rule.invoke(parser);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            if (e.getCause() instanceof ParseCancellationException)
                error = e.getCause().getMessage();
            else
                throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(e);
        }

        assertFalse("Expecting parser error!", error.isEmpty());
        assertTrue("\"" + error + "\" does not contain \"" + errorSubstring + "\"",
                error.contains(errorSubstring));
    }

    private void checkParseTree(String ruleName, String input, String stringTree)
    {
        final Zserio4Parser parser = createParser(input);
        try
        {
            Method rule = Zserio4Parser.class.getMethod(ruleName);
            ParseTree tree = (ParseTree)(rule.invoke(parser));
            assertEquals(stringTree, tree.toStringTree(parser));
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e.getCause());
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(e);
        }
    }
}
