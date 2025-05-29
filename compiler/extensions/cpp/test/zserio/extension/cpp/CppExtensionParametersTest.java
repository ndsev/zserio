package zserio.extension.cpp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import zserio.extension.common.ZserioExtensionException;
import zserio.tools.ExtensionParameters;

public class CppExtensionParametersTest
{
    @Test
    public void checkUnknownSetCppAllocator()
    {
        final String setCppAllocator = "unknown";
        final boolean withReflectionCode = false;
        final boolean withWriterCode = false;
        final boolean withSettersCode = false;
        final boolean withTypeInfoCode = false;
        final TestExtensionParameters extensionParameters = new TestExtensionParameters(
                setCppAllocator, withReflectionCode, withWriterCode, withSettersCode, withTypeInfoCode);
        assertThrows(ZserioExtensionException.class, () -> new CppExtensionParameters(extensionParameters));
    }

    @Test
    public void checkStdSetCppAllocator()
    {
        final String setCppAllocator = "std";
        final boolean withReflectionCode = false;
        final boolean withWriterCode = false;
        final boolean withSettersCode = false;
        final boolean withTypeInfoCode = false;
        final TestExtensionParameters extensionParameters = new TestExtensionParameters(
                setCppAllocator, withReflectionCode, withWriterCode, withSettersCode, withTypeInfoCode);
        assertDoesNotThrow(() -> new CppExtensionParameters(extensionParameters));
    }

    @Test
    public void checkPolymorphicSetCppAllocator()
    {
        final String setCppAllocator = "polymorphic";
        final boolean withReflectionCode = false;
        final boolean withWriterCode = false;
        final boolean withSettersCode = false;
        final boolean withTypeInfoCode = false;
        final TestExtensionParameters extensionParameters = new TestExtensionParameters(
                setCppAllocator, withReflectionCode, withWriterCode, withSettersCode, withTypeInfoCode);
        assertDoesNotThrow(() -> new CppExtensionParameters(extensionParameters));
    }

    @Test
    public void checkWithReflectionCode()
    {
        final String setCppAllocator = "std";
        final boolean withReflectionCode = true;
        final boolean withWriterCode = true;
        final boolean withSettersCode = false;
        final boolean withTypeInfoCode = true;
        final TestExtensionParameters extensionParameters = new TestExtensionParameters(
                setCppAllocator, withReflectionCode, withWriterCode, withSettersCode, withTypeInfoCode);
        assertDoesNotThrow(() -> new CppExtensionParameters(extensionParameters));
    }

    @Test
    public void checkWithReflectionCodeWithoutTypeInfoCode()
    {
        final String setCppAllocator = "std";
        final boolean withReflectionCode = true;
        final boolean withWriterCode = true;
        final boolean withSettersCode = true;
        final boolean withTypeInfoCode = false;
        final TestExtensionParameters extensionParameters = new TestExtensionParameters(
                setCppAllocator, withReflectionCode, withWriterCode, withSettersCode, withTypeInfoCode);
        assertThrows(ZserioExtensionException.class, () -> new CppExtensionParameters(extensionParameters));
    }

    @Test
    public void checkWithSettersCode()
    {
        final String setCppAllocator = "std";
        final boolean withReflectionCode = false;
        final boolean withWriterCode = true;
        final boolean withSettersCode = false;
        final boolean withTypeInfoCode = false;
        final TestExtensionParameters extensionParameters = new TestExtensionParameters(
                setCppAllocator, withReflectionCode, withWriterCode, withSettersCode, withTypeInfoCode);
        assertDoesNotThrow(() -> {
            CppExtensionParameters params = new CppExtensionParameters(extensionParameters);
            assertEquals(
                    params.getWithSettersCode(), true); // withSettersCode should be overriden by withWriterCode
        });
    }

    private static class TestExtensionParameters implements ExtensionParameters
    {
        public TestExtensionParameters(String setCppAllocator, boolean withReflectionCode,
                boolean withWriterCode, boolean withSettersCode, boolean withTypeInfoCode)
        {
            this.setCppAllocator = setCppAllocator;
            this.withReflectionCode = withReflectionCode;
            this.withWriterCode = withWriterCode;
            this.withTypeInfoCode = withTypeInfoCode;
            this.withSettersCode = withSettersCode;
        }

        @Override
        public boolean argumentExists(String argumentName)
        {
            if (argumentName.equals("withReflectionCode"))
                return withReflectionCode;
            if (argumentName.equals("withWriterCode"))
                return withWriterCode;
            if (argumentName.equals("withTypeInfoCode"))
                return withTypeInfoCode;
            if (argumentName.equals("withoutSourcesAmalgamation"))
                return false;
            if (argumentName.equals("withParsingInfoCode"))
                return false;
            if (argumentName.equals("withSettersCode"))
                return withSettersCode;

            fail("TestExtensionParameters: argumentExists failure for '" + argumentName + "'!");

            return false;
        }

        @Override
        public String getCommandLineArg(String argumentName)
        {
            if (argumentName.equals("setCppAllocator"))
                return setCppAllocator;
            if (argumentName.equals("cpp"))
                return null;

            fail("TestExtensionParameters: getCommandLineArg failure for '" + argumentName + "'!");

            return null;
        }

        @Override
        public String getFileName()
        {
            return null;
        }

        @Override
        public String getPathName()
        {
            return null;
        }

        @Override
        public boolean getWithRangeCheckCode()
        {
            return false;
        }

        @Override
        public boolean getWithPubsubCode()
        {
            return false;
        }

        @Override
        public boolean getWithServiceCode()
        {
            return false;
        }

        @Override
        public boolean getWithSqlCode()
        {
            return false;
        }

        @Override
        public boolean getWithTypeInfoCode()
        {
            return withTypeInfoCode;
        }

        @Override
        public boolean getWithValidationCode()
        {
            return false;
        }

        @Override
        public boolean getWithWriterCode()
        {
            return withWriterCode;
        }

        @Override
        public boolean getWithCodeComments()
        {
            return false;
        }

        @Override
        public List<String> getTopLevelPackageNameIds()
        {
            return new ArrayList<String>();
        }

        @Override
        public boolean getIgnoreTimestamps()
        {
            return false;
        }

        @Override
        public long getLastModifiedTime()
        {
            return 0;
        }

        @Override
        public String getZserioVersion()
        {
            return null;
        }

        private final String setCppAllocator;
        private final boolean withReflectionCode;
        private final boolean withTypeInfoCode;
        private final boolean withWriterCode;
        private final boolean withSettersCode;
    }
}
