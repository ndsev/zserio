package apollo;

struct Map
{
    Header header;

    Crosswalk crosswalk[];
    Junction junction[];
    Lane lane[];
    StopSign stopSign[];
    Signal signal[];
    YieldSign yieldSign[];
    Overlap overlap[];
    ClearArea clearArea[];
    optional SpeedBump speedBump[];
    Road road[];
    optional ParkingSpace parkingSpace[];
    optional PNCJunction pncJunction[];
    optional RSU rsu[];
};

// This message defines how we project the ellipsoidal Earth surface to a plane.
struct Projection
{
    // PROJ.4 setting:
    // "+proj=tmerc +lat_0={origin.lat} +lon_0={origin.lon} +k={scale_factor}
    // +ellps=WGS84 +no_defs"
    string proj;
};

struct Header
{
    string version;
    string date;
    Projection projection;
    string district;
    optional string generation;
    string revMajor;
    string revMinor;
    float64 left;
    float64 top;
    float64 right;
    float64 bottom;
    string vendor;
};

// A clear area means in which stopping car is prohibited

struct ClearArea
{
    Id id;
    Id overlapId[];
    optional Polygon polygon;
};

// Crosswalk is a place designated for pedestrians to cross a road.
struct Crosswalk
{
    Id id;
    optional Polygon polygon;
    Id overlapId[];
};

// Polygon, not necessary convex.
struct Polygon
{
    PointENU point[];
};

// Straight line segment.
struct LineSegment
{
    PointENU point[];
};

// Generalization of a line.
struct CurveSegment
{
    LineSegment lineSegment;
    float64 s;  // start position (s-coordinate)
    optional PointENU startPosition;
    float64 heading;  // start orientation
    float64 length;
};

// An object similar to a line but that need not be straight.
struct Curve
{
    CurveSegment segment[];
};

// A point in the map reference frame. The map defines an origin, whose
// coordinate is (0, 0, 0).
// Most modules, including localization, perception, and prediction, generate
// results based on the map reference frame.
// Currently, the map uses Universal Transverse Mercator (UTM) projection. See
// the link below for the definition of map origin.
//   https://en.wikipedia.org/wiki/Universal_Transverse_Mercator_coordinate_system
// The z field of PointENU can be omitted. If so, it is a 2D location and we do
// not care its height.
struct PointENU
{
    float64 x;  // East from the origin, in meters.
    float64 y;  // North from the origin, in meters.
    optional float64 z;  // Up from the WGS-84 ellipsoid, in meters.
};

// A point in the global reference frame. Similar to PointENU, PointLLH allows
// omitting the height field for representing a 2D location.
struct PointLLH
{
    // Longitude in degrees, ranging from -180 to 180.
    float64 lon;
    // Latitude in degrees, ranging from -90 to 90.
    float64 lat;
    // WGS-84 ellipsoid height in meters.
    optional float64 height;
};

// A general 2D point. Its meaning and units depend on context, and must be
// explained in comments.
struct Point2D
{
    float64 x;
    float64 y;
};

// A general 3D point. Its meaning and units depend on context, and must be
// explained in comments.
struct Point3D
{
    float64 x;
    float64 y;
    float64 z;
};

// A unit quaternion that represents a spatial rotation. See the link below for
// details.
//   https://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation
// The scalar part qw can be omitted. In this case, qw should be calculated by
//   qw = sqrt(1 - qx * qx - qy * qy - qz * qz).
struct Quaternion
{
    float64 qx;
    float64 qy;
    float64 qz;
    float64 qw;
};

// Global unique ids for all objects (include lanes, junctions, overlaps, etc).
struct Id
{
    string id;
};

// A junction is the junction at-grade of two or more roads crossing.
struct Junction
{
    Id id;
    optional Polygon polygon;
    Id overlapId[];
    optional JunctionType type;
};

enum uint8 JunctionType
{
    UNKNOWN = 0,
    IN_ROAD = 1,
    CROSS_ROAD = 2,
    FORK_ROAD = 3,
    MAIN_SIDE = 4,
    DEAD_END = 5,
};

struct LaneBoundaryType
{
    // Offset relative to the starting point of boundary
    float64 s;
    // support multiple types
    BoundaryType types[];
};

enum uint8 BoundaryType
{
    UNKNOWN = 0,
    DOTTED_YELLOW = 1,
    DOTTED_WHITE = 2,
    SOLID_YELLOW = 3,
    SOLID_WHITE = 4,
    DOUBLE_YELLOW = 5,
    CURB = 6,
};

struct LaneBoundary
{
    Curve curve;

    float64 length;
    // indicate whether the lane boundary exists in real world
    optional bool virtualBoundary;
    // in ascending order of s
    LaneBoundaryType boundaryType[];
};

// Association between central point to closest boundary.
struct LaneSampleAssociation
{
    float64 s;
    float64 width;
};

// A lane is part of a roadway, that is designated for use by a single line of
// vehicles.
// Most public roads (include highways) have more than two lanes.
struct Lane
{
    Id id;

    // Central lane as reference trajectory, not necessary to be the geometry
    // central.
    Curve centralCurve;

    // Lane boundary curve.
    optional LaneBoundary leftBoundary;
    optional LaneBoundary rightBoundary;

    // in meters.
    float64 length;

    // Speed limit of the lane, in meters per second.
    optional float64 speedLimit;

    Id overlapId[];

    // All lanes can be driving into (or from).
    optional Id predecessorId[];
    optional Id successorId[];

    // Neighbor lanes on the same direction.
    optional Id leftNeighborForwardLaneId[];
    optional Id rightNeighborForwardLaneId[];

    LaneType type;

    LaneTurn turn;

    optional Id leftNeighborReverseLaneId[];
    optional Id rightNeighborReverseLaneId[];

    optional Id junctionId;

    // Association between central point to closest boundary.
    LaneSampleAssociation leftSample[];
    LaneSampleAssociation rightSample[];

    optional LaneDirection direction;

    // Association between central point to closest road boundary.
    optional LaneSampleAssociation leftRoadSample[];
    optional LaneSampleAssociation rightRoadSample[];

    optional Id selfReverseLaneId[];
};

enum uint8 LaneDirection
{
    FORWARD = 1,
    BACKWARD = 2,
    BIDIRECTION = 3,
};

enum uint8 LaneType
{
    NONE = 1,
    CITY_DRIVING = 2,
    BIKING = 3,
    SIDEWALK = 4,
    PARKING = 5,
    SHOULDER = 6,
};

enum uint8 LaneTurn
{
    NO_TURN = 1,
    LEFT_TURN = 2,
    RIGHT_TURN = 3,
    U_TURN = 4,
};

struct LaneOverlapInfo
{
    float64 startS;  // position (s-coordinate)
    float64 endS;    // position (s-coordinate)
    bool isMerge;

    optional Id regionOverlapId;
};

struct SignalOverlapInfo {};

struct StopSignOverlapInfo {};

struct CrosswalkOverlapInfo {};

struct JunctionOverlapInfo {};

struct YieldOverlapInfo {};

struct ClearAreaOverlapInfo {};

struct SpeedBumpOverlapInfo {};

struct ParkingSpaceOverlapInfo {};

struct PNCJunctionOverlapInfo {};

struct RSUOverlapInfo {};

struct RegionOverlapInfo
{
    Id id;
    Polygon polygon[];
};

// Information about one object in the overlap.
struct ObjectOverlapInfo
{
    Id id;

    OverlapInfo overlapInfo;
};

union OverlapInfo
{
    LaneOverlapInfo laneOverlapInfo;
    SignalOverlapInfo signalOverlapInfo;
    StopSignOverlapInfo stopSignOverlapInfo;
    CrosswalkOverlapInfo crosswalkOverlapInfo;
    JunctionOverlapInfo junctionOverlapInfo;
    YieldOverlapInfo yieldSignOverlapInfo;
    ClearAreaOverlapInfo clearAreaOverlapInfo;
    SpeedBumpOverlapInfo speedBumpOverlapInfo;
    ParkingSpaceOverlapInfo parkingSpaceOverlapInfo;
    PNCJunctionOverlapInfo pncJunctionOverlapInfo;
    RSUOverlapInfo rsuOverlapInfo;
};

// Here, the "overlap" includes any pair of objects on the map
// (e.g. lanes, junctions, and crosswalks).
struct Overlap
{
    Id id;

    // Information about one overlap, include all overlapped objects.
    ObjectOverlapInfo object[];

    optional RegionOverlapInfo regionOverlap[];
};

// ParkingSpace is a place designated to park a car.
struct ParkingSpace
{
    Id id;
    optional Polygon polygon;
    Id overlapId[];
    optional float64 heading;
};

// ParkingLot is a place for parking cars.
struct ParkingLot
{
    Id id;
    optional Polygon polygon;
    Id overlapId[];
};

struct Passage
{
    Id id;

    Id signalId[];
    Id yieldId[];
    Id stopSignId[];
    Id laneId[];

    optional PassageType type;
};

enum uint8 PassageType
{
    UNKNOWN = 0,
    ENTRANCE = 1,
    EXIT = 2,
};

struct PassageGroup
{
    Id id;
    Passage passage[];
};

struct PNCJunction
{
    Id id;
    optional Polygon polygon;
    Id overlapId[];
    PassageGroup passageGroup[];
};

struct BoundaryEdge
{
    Curve curve;
    EdgeType type;
};

enum uint8 EdgeType
{
    UNKNOWN = 0,
    NORMAL = 1,
    LEFT_BOUNDARY = 2,
    RIGHT_BOUNDARY = 3,
};

struct BoundaryPolygon
{
    BoundaryEdge edge[];
};

// boundary with holes
struct RoadBoundary
{
    BoundaryPolygon outerPolygon;
    // if boundary without hole, hole is null
    optional BoundaryPolygon hole[];
};

struct RoadROIBoundary
{
    Id id;
    RoadBoundary roadBoundaries[];
};

// road section defines a road cross-section, At least one section must be
// defined in order to
// use a road, If multiple road sections are defined, they must be listed in
// order along the road
struct RoadSection
{
    Id id;
    // lanes contained in this section
    Id laneId[];
    // boundary of section
    optional RoadBoundary boundary;
};

// The road is a collection of traffic elements, such as lanes, road boundary
// etc.
// It provides general information about the road.
struct Road
{
    Id id;
    RoadSection section[];

    // if lane road not in the junction, junction id is null.
    optional Id junctionId;

    optional RoadType type;
};

enum uint8 RoadType
{
    UNKNOWN = 0,
    HIGHWAY = 1,
    CITY_ROAD = 2,
    PARK = 3,
};

struct RSU
{
    Id id;
    optional Id junctionId;
    Id overlapId[];
};

enum uint8 SubsignalType
{
    UNKNOWN = 1,
    CIRCLE = 2,
    ARROW_LEFT = 3,
    ARROW_FORWARD = 4,
    ARROW_RIGHT = 5,
    ARROW_LEFT_AND_FORWARD = 6,
    ARROW_RIGHT_AND_FORWARD = 7,
    ARROW_U_TURN = 8,
};

struct Subsignal
{
    Id id;
    SubsignalType type;

    // Location of the center of the bulb. now no data support.
    optional PointENU location;
};

struct SignInfo
{
    SignInfoType type;
};

enum uint8 SignInfoType
{
    None = 0,
    NO_RIGHT_TURN_ON_RED = 1,
};

struct Signal
{
    Id id;
    optional Polygon boundary;
    Subsignal subsignal[];
    // TODO: add orientation. now no data support.
    Id overlapId[];
    optional SignalType type;
    // stop line
    Curve stopLine[];

    optional SignInfo signInfo[];
};

enum uint8 SignalType
{
    UNKNOWN = 1,
    MIX_2_HORIZONTAL = 2,
    MIX_2_VERTICAL = 3,
    MIX_3_HORIZONTAL = 4,
    MIX_3_VERTICAL = 5,
    SINGLE = 6,
};

struct SpeedBump
{
    Id id;
    Id overlapId[];
    Curve position[];
};

// A stop sign is a traffic sign to notify drivers that they must stop before
// proceeding.
struct StopSign
{
    Id id;
    Curve stopLine[];
    Id overlapId[];
    optional StopType type;
};

enum uint8 StopType
{
    UNKNOWN = 0,
    ONE_WAY = 1,
    TWO_WAY = 2,
    THREE_WAY = 3,
    FOUR_WAY = 4,
    ALL_WAY = 5,
};

// A yield indicates that each driver must prepare to stop if necessary to let a
// driver on another approach proceed.
// A driver who stops or slows down to let another vehicle through has yielded
// the right of way to that vehicle.
struct YieldSign
{
    Id id;

    Curve stopLine[];

    Id overlapId[];
};
