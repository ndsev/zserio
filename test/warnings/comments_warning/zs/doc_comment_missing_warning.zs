// contains all documentable AST nodes, but no documentation - each one should fire doc-comment-missing warning

zserio_compatibility_version("2.7.0");

package doc_comment_missing_warning;

import doc_comment_missing_warning.all_nodes.*;

/** Check that documented nodes don't fire warnings. */
import doc_comment_missing_warning.documented.*;
