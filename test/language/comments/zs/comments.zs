package comments;

// This direct import 'DirectionStructure' is intentional. For older Zserio, this caused the following error:
// '[ERROR] zs/comments/documentation_comments.zs:25:4: The type Direction not found!'
import comments.documentation_comments.DirectionStructure;

import comments.standard_comments.*;

// caused an error with older Zserio versions
import comments.unknown_tag_matching_prefix.*;
// caused an error with older Zserio versions
import comments.unknown_tag.*;
