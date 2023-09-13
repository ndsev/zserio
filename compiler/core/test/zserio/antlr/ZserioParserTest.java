package zserio.antlr;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

public class ZserioParserTest
{
    @Test
    public void emptyPackage()
    {
        checkParseTree("packageDeclaration", "", "(packageDeclaration <EOF>)");
    }

    @Test
    public void compatibilityVersionDirective()
    {
        checkParseTree("compatibilityVersionDirective", "zserio_compatibility_version(\"2.4.2\");",
                "(compatibilityVersionDirective zserio_compatibility_version ( \"2.4.2\" ) ;)");
    }

    @Test
    public void packageNameDefinition()
    {
        checkParseTree("packageNameDefinition", "package test.pkg;",
                "(packageNameDefinition package (id test) . (id pkg) ;)");

        assertParseError("packageDeclaration", "package test.*;", "mismatched input '*' expecting ID");
    }

    @Test
    public void importDeclaration()
    {
        checkParseTree("importDeclaration", "import pkg.*;",
                "(importDeclaration import (id pkg) . * ;)");
        checkParseTree("importDeclaration", "import pkg1.pkg2;",
                "(importDeclaration import (id pkg1) . (id pkg2) ;)");
        checkParseTree("importDeclaration", "import pkg1.pkg2.*;",
                "(importDeclaration import (id pkg1) . (id pkg2) . * ;)");

        assertParseError("importDeclaration", "import pkg;", "mismatched input ';' expecting '.'");
        assertParseError("importDeclaration", "import *;", "mismatched input '*' expecting ID");
    }

    @Test
    public void constDefinition()
    {
        checkParseTree("constDefinition", "const uint32 C = 10;",
                "(constDefinition const (typeInstantiation (typeReference (builtinType (intType uint32)))) " +
                        "(id C) = (expression (literal 10)) ;)");
    }

    @Test
    public void ruleDefinition()
    {
        checkParseTree("ruleDefinition", "rule \"rule-id-1\";",
                "(ruleDefinition rule (expression (literal \"rule-id-1\")) ;)");
    }

    @Test
    public void subtypeDeclaration()
    {
        checkParseTree("subtypeDeclaration", "subtype uint32 Subtype;",
                "(subtypeDeclaration subtype (typeReference (builtinType (intType uint32))) (id Subtype) ;)");
    }

    @Test
    public void templateParameters()
    {
        checkParseTree("templateParameters", "<TYPE1, TYPE2>",
                "(templateParameters < (id TYPE1) , (id TYPE2) >)");
    }

    @Test
    public void typeParameters()
    {
        checkParseTree("typeParameters", "(uint32 id1, bool id2)",
                "(typeParameters ( " +
                        "(parameterDefinition (typeReference (builtinType (intType uint32))) (id id1)) , " +
                        "(parameterDefinition (typeReference (builtinType (boolType bool))) (id id2)) ))");
    }

    @Test
    public void fieldAlignment()
    {
        checkParseTree("fieldAlignment", "align(15):",
                "(fieldAlignment align ( (expression (literal 15)) ) :)");
    }

    @Test
    public void fieldOffset()
    {
        checkParseTree("fieldOffset", "10:",
                "(fieldOffset (expression (literal 10)) :)");
        checkParseTree("fieldOffset", "id1:",
                "(fieldOffset (expression (id id1)) :)");
        checkParseTree("fieldOffset", "id1.id2:",
                "(fieldOffset (expression (expression (id id1)) . (id id2)) :)");

        assertParseError("fieldOffset", "uint32:", "mismatched input 'uint32' expecting {"); // ...
    }

    @Test
    public void fieldTypeId()
    {
        checkParseTree("fieldTypeId", "uint32 value",
                "(fieldTypeId (typeInstantiation (typeReference (builtinType (intType uint32)))) (id value))");
        checkParseTree("fieldTypeId", "uint32 value",
                "(fieldTypeId (typeInstantiation (typeReference (builtinType (intType uint32)))) (id value))");
        checkParseTree("fieldTypeId", "implicit uint32 value[]",
                "(fieldTypeId implicit (typeInstantiation (typeReference (builtinType (intType uint32)))) " +
                        "(id value) (fieldArrayRange [ ]))");
        checkParseTree("fieldTypeId", "uint32 value[22]",
                "(fieldTypeId (typeInstantiation (typeReference (builtinType (intType uint32)))) (id value) " +
                        "(fieldArrayRange [ (expression (literal 22)) ]))");
        checkParseTree("fieldTypeId", "packed uint64 value[]",
                "(fieldTypeId packed (typeInstantiation (typeReference (builtinType (intType uint64)))) " +
                "(id value) (fieldArrayRange [ ]))");
        checkParseTree("fieldTypeId", "Parameterized(param) field",
                "(fieldTypeId (typeInstantiation (typeReference (qualifiedName (id Parameterized))) " +
                        "(typeArguments ( (typeArgument (expression (id param))) ))) (id field))");
        checkParseTree("fieldTypeId", "Template<uint32> field",
                "(fieldTypeId (typeInstantiation (typeReference (qualifiedName (id Template)) " +
                        "(templateArguments < (templateArgument (typeReference " +
                                "(builtinType (intType uint32)))) >))) (id field))");

        assertParseError("fieldTypeId", "uint32 bool", "mismatched input 'bool' expecting {"); // ...
    }

    @Test
    public void fieldInitializer()
    {
        checkParseTree("fieldInitializer", "= 12",
                "(fieldInitializer = (expression (literal 12)))");
        checkParseTree("fieldInitializer", "= \"string\"",
                "(fieldInitializer = (expression (literal \"string\")))");
    }

    @Test
    public void fieldOptionalClause()
    {
        checkParseTree("fieldOptionalClause", "if a != 0",
                "(fieldOptionalClause if (expression (expression (id a)) != " +
                        "(expression (literal 0))))");
    }

    @Test
    public void fieldConstraint()
    {
        checkParseTree("fieldConstraint", ": hasValue", "(fieldConstraint : (expression (id hasValue)))");
    }

    @Test
    public void structureFieldDefinition()
    {
        checkParseTree("structureFieldDefinition",
                "align (10):" +
                "offset:" +
                "    uint32 value = 10 if hasValue : value > 0;",
                "(structureFieldDefinition " +
                        "(fieldAlignment align ( (expression (literal 10)) ) :) " +
                        "(fieldOffset (expression (id offset)) :) " +
                        "(fieldTypeId (typeInstantiation (typeReference (builtinType (intType uint32)))) " +
                                "(id value)) " +
                        "(fieldInitializer = (expression (literal 10))) " +
                        "(fieldOptionalClause if (expression (id hasValue))) " +
                        "(fieldConstraint : (expression (expression (id value)) > " +
                                "(expression (literal 0)))) " +
                ";)");
        checkParseTree("structureFieldDefinition", "optional string description;",
                "(structureFieldDefinition optional " +
                "(fieldTypeId (typeInstantiation (typeReference (builtinType (stringType string)))) " +
                        "(id description)) ;)");
    }

    @Test
    public void choiceCases()
    {
        checkParseTree("choiceCases", "case 10: uint32 field;",
                "(choiceCases (choiceCase case (expression (literal 10)) :) " +
                "(choiceFieldDefinition (fieldTypeId (typeInstantiation (typeReference (builtinType " +
                        "(intType uint32)))) (id field))) ;)");
        checkParseTree("choiceCases",
                "case 10:\n" +
                "case 11:\n" +
                "case 22:\n" +
                "    string value;",
                "(choiceCases " +
                "(choiceCase case (expression (literal 10)) :) " +
                "(choiceCase case (expression (literal 11)) :) " +
                "(choiceCase case (expression (literal 22)) :) " +
                "(choiceFieldDefinition (fieldTypeId (typeInstantiation (typeReference " +
                        "(builtinType (stringType string)))) (id value))) ;)");
    }

    @Test
    public void choiceDefault()
    {
        checkParseTree("choiceDefault", "default: int8 field;",
                "(choiceDefault default : " +
                "(choiceFieldDefinition (fieldTypeId (typeInstantiation (typeReference " +
                        "(builtinType (intType int8)))) (id field))) ;)");
    }

    @Test
    public void enumDeclaration()
    {
        checkParseTree("enumDeclaration", "enum uint8 E { ONE = 1, TWO };",
                "(enumDeclaration enum (typeInstantiation (typeReference (builtinType (intType uint8)))) " +
                "(id E) { " +
                        "(enumItem (id ONE) = (expression (literal 1))) , " +
                        "(enumItem (id TWO)) " +
                "} ;)");

        checkParseTree("enumDeclaration", "enum uint8 E { @removed ONE = 1, @deprecated TWO };",
                "(enumDeclaration enum (typeInstantiation (typeReference (builtinType (intType uint8)))) " +
                "(id E) { " +
                        "(enumItem @removed (id ONE) = (expression (literal 1))) , " +
                        "(enumItem @deprecated (id TWO)) " +
                "} ;)");

        // trailing COMMA is allowed!
        checkParseTree("enumDeclaration", "enum uint8 E { ONE = 1, TWO, };",
                "(enumDeclaration enum (typeInstantiation (typeReference (builtinType (intType uint8)))) " +
                "(id E) { " +
                        "(enumItem (id ONE) = (expression (literal 1))) , " +
                        "(enumItem (id TWO)) , " +
                "} ;)");

        assertParseError("enumDeclaration", "enum uint8 E { @deprecated @removed ONE };",
                "extraneous input '@removed' expecting ID");
        assertParseError("enumDeclaration", "enum uint8 E { @removed @deprecated ONE };",
                "extraneous input '@deprecated' expecting ID");
    }

    @Test
    public void sqlTableFieldDefinition()
    {
        checkParseTree("sqlTableFieldDefinition", "uint32 field;",
                "(sqlTableFieldDefinition (typeInstantiation (typeReference (builtinType (intType uint32)))) " +
                        "(id field) ;)");

        checkParseTree("sqlTableFieldDefinition", "sql_virtual uint32 field;",
                "(sqlTableFieldDefinition sql_virtual (typeInstantiation (typeReference " +
                            "(builtinType (intType uint32)))) (id field) ;)");

        checkParseTree("sqlTableFieldDefinition", "uint32 field sql \"PRIMARY KEY\";",
                "(sqlTableFieldDefinition (typeInstantiation (typeReference (builtinType (intType uint32)))) " +
                        "(id field) (sqlConstraint sql (expression (literal \"PRIMARY KEY\"))) ;)");
    };

    @Test
    public void sqlConstraintDefinition()
    {
        checkParseTree("sqlConstraintDefinition", "sql \"languageid='languageCode', notindexed='frequency'\";",
                "(sqlConstraintDefinition (sqlConstraint sql " +
                        "(expression (literal \"languageid='languageCode', notindexed='frequency'\"))) ;)");
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
                "(sqlDatabaseFieldDefinition (typeInstantiation (typeReference (qualifiedName " +
                        "(id SomeTable)))) (id someTable) ;)");

        checkParseTree("sqlDatabaseFieldDefinition", "org.pkg.SomeTable someTable;",
                "(sqlDatabaseFieldDefinition (typeInstantiation (typeReference " +
                        "(qualifiedName (id org) . (id pkg) . (id SomeTable)))) (id someTable) ;)");
    }

    @Test
    public void serviceMethodDefinition()
    {
        checkParseTree("serviceMethodDefinition", "Response method(Request);",
                "(serviceMethodDefinition (typeReference (qualifiedName (id Response))) " +
                        "(id method) ( (typeReference (qualifiedName (id Request))) ) ;)");
    }

    @Test
    public void pubsubMessageDefinition()
    {
        checkParseTree("pubsubMessageDefinition", "publish topic(\"pubsub/test\") MessageType messageId;",
                "(pubsubMessageDefinition (topicDefinition publish topic ( (expression " +
                        "(literal \"pubsub/test\")) )) " +
                        "(typeReference (qualifiedName (id MessageType))) (id messageId) ;)");

        checkParseTree("pubsubMessageDefinition", "subscribe topic(\"pubsub/test\") MessageType messageId;",
                "(pubsubMessageDefinition (topicDefinition subscribe topic ( (expression " +
                        "(literal \"pubsub/test\")) )) " +
                        "(typeReference (qualifiedName (id MessageType))) (id messageId) ;)");

        checkParseTree("pubsubMessageDefinition", "topic(\"pubsub/test\") MessageType messageId;",
                "(pubsubMessageDefinition (topicDefinition topic ( (expression " +
                        "(literal \"pubsub/test\")) )) " +
                        "(typeReference (qualifiedName (id MessageType))) (id messageId) ;)");
    }

    @Test
    public void functionDefinition()
    {
        checkParseTree("functionDefinition", "function RetType testFunc() { return 0; }",
                "(functionDefinition function (functionType (typeReference (qualifiedName (id RetType)))) " +
                        "(functionName (id testFunc)) ( ) " +
                        "(functionBody { return (expression (literal 0)) ; }))");
        assertParseError("functionDefinition", "function Parameterized() testFunc() { return 0; }",
                "missing ID at '('");
        assertParseError("functionDefinition", "function Parameterized testFunc(int32) { return 0; }",
                "extraneous input 'int32' expecting ')'");
        assertParseError("functionDefinition", "function Parameterized testFunc(int32 arg) { return 0; }",
                "mismatched input 'int32' expecting {"); // TODO: why antlr4 does NOT report "expecting ')'"?
        assertParseError("functionDefinition", "function int32 testFunc(int32 arg) { return 0; }",
                "mismatched input 'int32' expecting ')'"); // TODO: and why this works?
    }

    protected static class ThrowingErrorListener extends BaseErrorListener
    {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                int line, int charPositionInLine, String msg,
                RecognitionException e) throws ParseCancellationException
        {
            throw new ParseCancellationException(msg);
        }
    }

    private static ZserioParser createParser(String input)
    {
        final ThrowingErrorListener throwingErrorListener = new ThrowingErrorListener();
        final ZserioLexer lexer = new ZserioLexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(throwingErrorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final ZserioParser parser = new ZserioParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(throwingErrorListener);
        return parser;
    }

    private static void assertParseError(String ruleName, String input, String errorSubstring)
    {
        final ZserioParser parser = createParser(input);
        String error = "";
        try
        {
            Method rule = ZserioParser.class.getMethod(ruleName);
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

        assertFalse(error.isEmpty(), "Expecting parser error!");
        assertThat(error, containsString(errorSubstring));
    }

    private static void checkParseTree(String ruleName, String input, String stringTree)
    {
        final ZserioParser parser = createParser(input);
        try
        {
            Method rule = ZserioParser.class.getMethod(ruleName);
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
