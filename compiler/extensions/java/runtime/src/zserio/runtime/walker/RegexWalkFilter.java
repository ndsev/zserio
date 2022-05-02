package zserio.runtime.walker;

import java.lang.reflect.Array;
import java.util.Stack;
import java.util.regex.Pattern;

import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.typeinfo.TypeInfoUtil;

/**
 * Walk filter which allows to walk only paths matching the given regex.
 *
 *  The path is constructed from field names within the root object, thus the root object
 *  itself is not part of the path.
 *
 *  Array elements have the index appended to the path so that e.g. "compound.arrayField[0]" will match
 *  only the first element in the array "arrayField".
 */
public class RegexWalkFilter implements WalkFilter
{
    /**
     * Constructor.
     *
     * @param pathRegex Path regex to use for filtering.
     */
    public RegexWalkFilter(String pathRegex)
    {
        this.pathRegex = pathRegex;
        this.currentPath = new Stack<String>();
    }

    @Override
    public boolean beforeArray(Object array, FieldInfo fieldInfo)
    {
        currentPath.push(fieldInfo.getSchemaName());
        if (!Pattern.matches(pathRegex, getCurrentPath()))
            return true; // the array itself matches

        // try to find match in each element and continue into the array only if some match is found
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i)
        {
            currentPath.set(currentPath.size() - 1, fieldInfo.getSchemaName() + "[" + i + "]");
            final Object element = Array.get(array, i);
            if (matchSubtree(element, fieldInfo.getTypeInfo()))
                return true;
        }
        currentPath.set(currentPath.size() - 1, fieldInfo.getSchemaName());

        return false;
    }

    @Override
    public boolean afterArray(Object array, FieldInfo fieldInfo)
    {
        currentPath.pop();
        return true;
    }

    @Override
    public boolean beforeCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
        appendPath(typeInfo, elementIndex);
        if (Pattern.matches(pathRegex, getCurrentPath()))
            return true; // the compound itself matches

        return matchSubtree(compound, typeInfo);
    }

    @Override
    public boolean afterCompound(Object compound, TypeInfo typeInfo, int elementIndex)
    {
        popPath(typeInfo, elementIndex);
        return true;
    }

    @Override
    public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        final TypeInfo typeInfo = fieldInfo.getTypeInfo();
        appendPath(typeInfo, elementIndex);
        return matchSubtree(value, typeInfo);
    }

    @Override
    public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex)
    {
        popPath(fieldInfo.getTypeInfo(), elementIndex);
        return true;
    }

    boolean matchSubtree(Object member, TypeInfo typeInfo)
    {
        if (member != null && TypeInfoUtil.isCompound(typeInfo.getSchemaType()))
        {
            // is a not null compound, try to find match within its subtree
            final DefaultWalkObserver defaultObserver = new DefaultWalkObserver();
            final SubtreeRegexWalkFilter subtreeFilter = new SubtreeRegexWalkFilter(currentPath, pathRegex);
            final Walker walker = new Walker(defaultObserver, subtreeFilter);
            walker.walk(member);
            return subtreeFilter.matches();
        }
        else
        {
            // try to match a simple value or null compound
            return Pattern.matches(pathRegex, getCurrentPath());
        }
    }

    private String getCurrentPath()
    {
        return getCurrentPathImpl(currentPath);
    }

    private void appendPath(TypeInfo typeInfo, int elementIndex)
    {
        appendPathImpl(currentPath, typeInfo, elementIndex);
    }

    private void popPath(TypeInfo typeInfo, int elementIndex)
    {
        popPathImpl(currentPath, typeInfo, elementIndex);
    }

    private static String getCurrentPathImpl(Stack<String> currentPath)
    {
        return String.join(".", currentPath);
    }

    private static void appendPathImpl(Stack<String> currentPath, TypeInfo typeInfo, int elementIndex)
    {
        if (elementIndex == WalkerConst.NOT_ELEMENT)
            currentPath.push(typeInfo.getSchemaName());
        else
            currentPath.set(currentPath.size() - 1, typeInfo.getSchemaName() + "[" + elementIndex + "]");
    }

    private static void popPathImpl(Stack<String> currentPath, TypeInfo typeInfo, int elementIndex)
    {
        if (elementIndex == WalkerConst.NOT_ELEMENT)
            currentPath.pop();
        else
            currentPath.set(currentPath.size() -1, typeInfo.getSchemaName());
    }

    /**
     * Walks whole subtree and in case of match stops walking. Used to check whether any path
     * within the subtree matches given regex.
     */
    private static class SubtreeRegexWalkFilter implements WalkFilter
    {
        public SubtreeRegexWalkFilter(Stack<String> currentPath, String pathRegex)
        {
            this.currentPath = currentPath;
            this.pathRegex = pathRegex;

            matches = false;
        }

        public boolean matches()
        {
            return matches;
        }

        @Override
        public boolean beforeArray(Object array, FieldInfo fieldInfo)
        {
            currentPath.push(fieldInfo.getSchemaName());
            matches = Pattern.matches(pathRegex, getCurrentPath());

            // terminate when the match is already found (note that array is never None here)
            return !matches;
        }

        @Override
        public boolean afterArray(Object array, FieldInfo fieldInfo)
        {
            currentPath.pop();

            // terminate when the match is already found
            return !matches;
        }

        @Override
        public boolean beforeCompound(Object compound, TypeInfo typeInfo, int elementIndex)
        {
            appendPath(typeInfo, elementIndex);
            matches = Pattern.matches(pathRegex, getCurrentPath());

            //  terminate when the match is already found (note that compound is never None here)
            return !matches;
        }

        @Override
        public boolean afterCompound(Object compound, TypeInfo typeInfo, int elementIndex)
        {
            popPath(typeInfo, elementIndex);

            // terminate when the match is already found
            return !matches;
        }

        @Override
        public boolean beforeValue(Object value, FieldInfo fieldInfo, int elementIndex)
        {
            appendPath(fieldInfo.getTypeInfo(), elementIndex);
            matches = Pattern.matches(pathRegex, getCurrentPath());

            // terminate when the match is already found
            return !matches;
        }

        @Override
        public boolean afterValue(Object value, FieldInfo fieldInfo, int elementIndex)
        {
            popPath(fieldInfo.getTypeInfo(), elementIndex);

            // terminate when the match is already found
            return !matches;
        }

        private String getCurrentPath()
        {
            return RegexWalkFilter.getCurrentPathImpl(currentPath);
        }

        private void appendPath(TypeInfo typeInfo, int elementIndex)
        {
            RegexWalkFilter.appendPathImpl(currentPath, typeInfo, elementIndex);
        }

        private void popPath(TypeInfo typeInfo, int elementIndex)
        {
            RegexWalkFilter.popPathImpl(currentPath, typeInfo, elementIndex);
        }

        private final Stack<String> currentPath;
        private final String pathRegex;

        private boolean matches;
    };

    private final String pathRegex;
    private final Stack<String> currentPath;
};
