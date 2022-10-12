package comments_warning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import test_utils.ZserioErrorOutput;

public class DocCommentMissingWarningTest
{
    @BeforeAll
    public static void readZserioWarnings() throws IOException
    {
        zserioWarnings = new ZserioErrorOutput();
    }

    @Test
    public void compatibilityVersion()
    {
        assertTrue(zserioWarnings.isPresent("doc_comment_missing_warning.zs:3:30: " +
                "Missing documentation comment for compatibility version."));
    }

    @Test
    public void packageNode()
    {
        assertTrue(zserioWarnings.isPresent("doc_comment_missing_warning.zs:5:9: " +
            "Missing documentation comment for package."));
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:1:9: " +
            "Missing documentation comment for package."));
    }

    @Test
    public void importNode()
    {
        assertTrue(zserioWarnings.isPresent("doc_comment_missing_warning.zs:7:8: " +
            "Missing documentation comment for import."));
    }

    @Test
    public void constant()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:13:14: " +
            "Missing documentation comment for constant 'CONSTANT'."));
    }

    @Test
    public void subtype()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:23:16: " +
            "Missing documentation comment for subtype 'Subtype'."));
    }

    @Test
    public void instantiateType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:20:35: " +
            "Missing documentation comment for instantiate type 'StructureTypeU32'."));
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:21:35: " +
            "Missing documentation comment for instantiate type 'StructureTypeSTR'."));
    }

    @Test
    public void bitmaskType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:3:15: " +
            "Missing documentation comment for bitmask 'BitmaskType'."));
    }

    @Test
    public void bitmaskValue()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:5:5: " +
            "Missing documentation comment for bitmask value 'BITMASK_VALUE'."));
    }

    @Test
    public void enumType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:8:12: " +
            "Missing documentation comment for enumeration 'EnumType'."));
    }

    @Test
    public void enumItem()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:10:5: " +
            "Missing documentation comment for enum item 'ENUM_ITEM'."));
    }

    @Test
    public void structureType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:15:8: " +
            "Missing documentation comment for structure 'StructureType'."));
    }

    @Test
    public void choiceType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:25:8: " +
            "Missing documentation comment for choice 'ChoiceType'."));
    }

    @Test
    public void unionType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:38:7: " +
            "Missing documentation comment for union 'UnionType'."));
    }

    @Test
    public void field()
    {
        // structure field
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:17:7: " +
            "Missing documentation comment for field 'field'."));

        // choice field
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:28:16: " +
            "Missing documentation comment for field 'field'."));

        // union field
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:40:12: " +
            "Missing documentation comment for field 'fieldU32'."));
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:41:12: " +
            "Missing documentation comment for field 'fieldSTR'."));

        // sql table field
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:46:12: " +
            "Missing documentation comment for field 'id'."));

        // sql database field
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:51:18: " +
            "Missing documentation comment for field 'sqlTable'."));
    }

    @Test
    public void function()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:32:21: " +
            "Missing documentation comment for function 'getField'."));
    }

    @Test
    public void choiceCaseExpression()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:27:5: " +
            "Missing documentation comment for choice case expression."));
    }

    @Test
    public void choiceDefault()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:29:5: " +
            "Missing documentation comment for choice default."));
    }

    @Test
    public void sqlTable()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:44:11: " +
            "Missing documentation comment for SQL table 'SqlTableType'."));
    }

    @Test
    public void sqlDatabase()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:49:14: " +
            "Missing documentation comment for SQL database 'SqlDatabaseType'."));
    }

    @Test
    public void ruleGroup()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:54:12: " +
            "Missing documentation comment for rule group 'Rules'."));
    }

    @Test
    public void rule()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:56:10: " +
            "Missing documentation comment for rule 'test-rule'."));
    }

    @Test
    public void serviceType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:59:9: " +
            "Missing documentation comment for service 'ServiceType'."));
    }

    @Test
    public void serviceMethod()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:61:22: " +
            "Missing documentation comment for method 'serviceMethod'."));
    }

    @Test
    public void pubsubType()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:64:8: " +
            "Missing documentation comment for pubsub 'PubsubType'."));
    }

    @Test
    public void pubsubMessage()
    {
        assertTrue(zserioWarnings.isPresent("all_nodes.zs:66:46: " +
            "Missing documentation comment for message 'pubsubMessage'."));
    }


    private static ZserioErrorOutput zserioWarnings;
};
