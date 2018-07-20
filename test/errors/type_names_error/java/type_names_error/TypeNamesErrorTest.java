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
        final String error =
                "choice_choice_name_conflict_error.zs:11:8: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void choiceStructureNameConflict()
    {
        final String error =
                "choice_structure_name_conflict_error.zs:11:8: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void choiceUnionNameConflict()
    {
        final String error =
                "choice_union_name_conflict_error.zs:11:7: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constConstNameConflict()
    {
        final String error =
                "const_const_name_conflict_error.zs:4:14: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constStructureNameConflict()
    {
        final String error =
                "const_structure_name_conflict_error.zs:5:8: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void constSubtypeNameConflict()
    {
        final String error =
                "const_subtype_name_conflict_error.zs:5:16: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void structureParamStructureNameConflict()
    {
        final String error =
                "structure_param_structure_name_conflict_error.zs:8:8: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void structureStructureNameConflict()
    {
        final String error =
                "structure_structure_name_conflict_error.zs:8:8: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void structureUnionNameConflict()
    {
        final String error =
                "structure_union_name_conflict_error.zs:8:7: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypeStructureNameConflict()
    {
        final String error =
                "subtype_structure_name_conflict_error.zs:5:8: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void subtypeSubtypeNameConflict()
    {
        final String error =
                "subtype_subtype_name_conflict_error.zs:5:16: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    @Test
    public void unionUnionNameConflict()
    {
        final String error =
                "union_union_name_conflict_error.zs:9:7: 'Test' is already defined in this package!";
        assertTrue(zserioErrors.isPresent(error));
    }

    private static ZserioErrors zserioErrors;
}
