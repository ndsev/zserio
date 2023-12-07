package test_utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public final class AssertionUtils
{
    public static void assertJsonEquals(String expectedJson, String providedJson)
    {
        assertEquals(expectedJson.replaceAll("\n", System.lineSeparator()), providedJson);
    }

    public static void assertMethodPresent(Class<?> userType, String methodPattern)
    {
        boolean present = false;
        for (String method : getMethods(userType))
        {
            if (method.indexOf(methodPattern) != -1)
            {
                present = true;
                break;
            }
        }
        assertTrue(present, "Method '" + methodPattern + "' is not present!");
    }

    public static void assertMethodNotPresent(Class<?> userType, String methodPattern)
    {
        boolean present = false;
        for (String method : getMethods(userType))
        {
            if (method.indexOf(methodPattern) != -1)
            {
                present = true;
                break;
            }
        }
        assertFalse(present, "Method '" + methodPattern + "' is present!");
    }

    public static void assertInnerClassPresent(Class<?> userType, String innerClassName)
    {
        boolean present = false;
        for (Class<?> declaredClass : userType.getDeclaredClasses())
        {
            if (declaredClass.getSimpleName().equals(innerClassName))
            {
                present = true;
                break;
            }
        }

        assertTrue(present, "Inner class '" + innerClassName + "' is not present!");
    }

    public static void assertInnerClassNotPresent(Class<?> userType, String innerClassName)
    {
        boolean present = false;
        for (Class<?> declaredClass : userType.getDeclaredClasses())
        {
            if (declaredClass.getSimpleName().equals(innerClassName))
            {
                present = true;
                break;
            }
        }

        assertFalse(present, "Inner class '" + innerClassName + "' is present!");
    }

    private static Set<String> getMethods(Class<?> userType)
    {
        final HashSet<String> methods = new HashSet<String>();

        for (Constructor<?> constructor : userType.getDeclaredConstructors())
            methods.add(constructor.toString());
        for (Method method : userType.getDeclaredMethods())
            methods.add(method.toString());

        return methods;
    }
}
