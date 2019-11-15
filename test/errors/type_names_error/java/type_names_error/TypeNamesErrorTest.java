package type_names_error;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import test_utils.ZserioErrors;

public class TypeNamesErrorTest
{
    @BeforeClass
    public static void readZserioErrors() throws IOException
    {
        zserioErrors = new ZserioErrors();
    }

    @Test
    public void choiceChoiceNameConflict()
    {
        final String errors[] =
        {
            "choice_choice_name_conflict_error.zs:3:8:     First defined here",
            "choice_choice_name_conflict_error.zs:11:8: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void choiceStructureNameConflict()
    {
        final String errors[] =
        {
            "choice_structure_name_conflict_error.zs:3:8:     First defined here",
            "choice_structure_name_conflict_error.zs:11:8: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void choiceUnionNameConflict()
    {
        final String errors[] =
        {
            "choice_union_name_conflict_error.zs:3:8:     First defined here",
            "choice_union_name_conflict_error.zs:11:7: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constConstNameConflict()
    {
        final String errors[] =
        {
            "const_const_name_conflict_error.zs:3:13:     First defined here",
            "const_const_name_conflict_error.zs:4:14: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constServiceNameConflict()
    {
        final String errors[] =
        {
            "const_service_name_conflict_error.zs:15:9:     First defined here",
            "const_service_name_conflict_error.zs:3:14: 'Math' is a defined type in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constStructureNameConflict()
    {
        final String errors[] =
        {
            "const_structure_name_conflict_error.zs:5:8:     First defined here",
            "const_structure_name_conflict_error.zs:3:13: 'Test' is a defined type in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void constSubtypeNameConflict()
    {
        final String errors[] =
        {
            "const_subtype_name_conflict_error.zs:5:16:     First defined here",
            "const_subtype_name_conflict_error.zs:3:13: 'Test' is a defined type in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void serviceServiceNameConflict()
    {
        final String errors[] =
        {
            "service_service_name_conflict_error.zs:13:9:     First defined here",
            "service_service_name_conflict_error.zs:18:9: 'Math' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureEnumNameConflict()
    {
        final String errors[] =
        {
            "structure_enum_name_conflict_error.zs:3:8:     First defined here",
            "structure_enum_name_conflict_error.zs:8:12: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureParamStructureNameConflict()
    {
        final String errors[] =
        {
            "structure_param_structure_name_conflict_error.zs:3:8:     First defined here",
            "structure_param_structure_name_conflict_error.zs:8:8: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureServiceNameConflict()
    {
        final String errors[] =
        {
            "structure_service_name_conflict_error.zs:3:8:     First defined here",
            "structure_service_name_conflict_error.zs:19:9: 'Math' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureStructureNameConflict()
    {
        final String errors[] =
        {
            "structure_structure_name_conflict_error.zs:3:8:     First defined here",
            "structure_structure_name_conflict_error.zs:8:8: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void structureUnionNameConflict()
    {
        final String errors[] =
        {
            "structure_union_name_conflict_error.zs:3:8:     First defined here",
            "structure_union_name_conflict_error.zs:8:7: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void subtypeStructureNameConflict()
    {
        final String errors[] =
        {
            "subtype_structure_name_conflict_error.zs:3:15:     First defined here",
            "subtype_structure_name_conflict_error.zs:5:8: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void subtypeSubtypeNameConflict()
    {
        final String errors[] =
        {
            "subtype_subtype_name_conflict_error.zs:3:15:     First defined here",
            "subtype_subtype_name_conflict_error.zs:5:16: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    @Test
    public void unionUnionNameConflict()
    {
        final String errors[] =
        {
            "union_union_name_conflict_error.zs:3:7:     First defined here",
            "union_union_name_conflict_error.zs:9:7: 'Test' is already defined in this package!"
        };
        assertTrue(zserioErrors.isPresent(errors));
    }

    private static ZserioErrors zserioErrors;
}
