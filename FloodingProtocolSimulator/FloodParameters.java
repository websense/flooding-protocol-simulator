
public class FloodParameters {
    // SIMULATOR TOPOLOGY CONSTANTS

    //number of nodes in the network
	public static int maxNodes = 25*25; //400;
	//for regular topology, maxNodes is expected to be a perfect square
    public static int maxXnodes = (int)Math.sqrt(maxNodes);
    public static int maxYnodes = maxXnodes;
    //public static int maxNodes = maxXnodes * maxYnodes;

    public static int Source=0;  // identifier of flood source node, first in topology

    //LANDSCAPE DIMENSIONS - always square for now
    public static int maxX = 200;
    public static int maxY = maxX;

    public static final int RANDOM = 0; //uniform random node placement in landscape
    public static final int REGULAR = 1; //hex grid topology

    public static int topology = RANDOM; //default
 	// STATE OF THE LANDSCAPE
 	public static int [][] landscape = new int [maxX][maxY];
 		// landscape value -1=collision 0=no signal 1=message present

    // PROTOCOL RETRANSMISSION RULE - probability of passing on a message  default=1
    public static double RetransProb = 1.0d;

    // MAC constants used in tos/platform/mica2/CC1000RadioIntM
    // backoffs calculated assuming one simulator cycle = 56 byte times
    //								  = 1 pkt + preamble
    public static double initmaxBackoff = 10.286; // before trying to send wait for 1 up to 1+15 packet times
    public static double maxBackoff = 2.268; //if carrier busy wait msgsize + [0,127] radio bytes

//  SET DEFAULT FP TYPE FOR EXPERIMENT HERE
 public static int fptype = 2; // 1 default is average noise

 // RADIO TRANSMISSION FOOTPRINT INFO
    static int maxFPradius = 30;

    int nFPtypes = 4; // number of different footprint profiles

// CALCULATE TRANSMISSION FOOTPRINTS

// probability of reception at distance from sender in nodes
// for 4 FPs for experiments
// 28 Aug 03 example FPs for motes from
// MoteConnectivity data woo.allnodedata.28aug.txt
// but stretched to give 30 landscape steps radius

static int [][] receptionpercent = {
 // high noise, node 15 (id 111) at power 0, avg 114 nodes in FP
  { 0, 70, 70, 74, 75, 61, 50, 41, 40, 31, 20, 31, 30, 30, 25, 26, 10, 32, 20, 35, 12, 5, 0, 4, 0, 3, 0, 0, 0, 0 },
// avg noise, from woo at power MED, avg 113 nodes
  { 0, 82, 80, 77, 75, 73, 70, 67, 65, 58, 50, 54, 48, 40, 35, 28, 28, 20, 5, 5, 0, 4, 0, 3, 3,  2, 2, 0, 0, 0 },
// low noise, node 86 (id 255) at power 3, avg 110 nodes in FP
  { 0, 91, 90, 89, 85, 80, 75, 70, 75, 66, 65, 65, 50, 50, 50, 50, 45, 37, 35, 25, 24, 0, 23, 23, 0, 0, 0, 0, 0, 0 },
// dense FP radius 25, 113 nodes in FP
  { 0, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 0, 0, 0, 0, 0 }
 };

// Xu et al show interference radius larger than effective receive distance
// and carrier sense distance is tunable
// here assume CS radius = interference radius
// = greatest dist with recv prob > 5%

static int[] interferenceradius = { 11, 9, 9, 6  };

public static void setFloodParameters(int t, int f, int n, int l, double rp) {
	topology = t;
	if ((topology<0)||(topology>1)) {
		throw new NumberFormatException ("Topology must be 0 or 1");
	}
	fptype = f;
	if ((fptype<0)||(fptype>3)) {
		throw new NumberFormatException ("Footprint must be in 0 to 3");
	}

	maxXnodes = (int)Math.sqrt(n);
	maxYnodes = maxXnodes;
	if (topology==REGULAR) { //then maxNodes must be perfect sq
		maxNodes = maxXnodes*maxXnodes;
	} else {
		maxNodes = n;
	}
	maxX = l;
	maxY = maxX;
	if ((l<10)||(l>200)) {
		throw new NumberFormatException ("Landscape must be a square of 10 to 200");
	}
	RetransProb = rp;
	if ((rp<0.0)||(rp>1.0)) {
		throw new NumberFormatException ("Retrans must be in 0.0 to 1.0");
	}
}

/*
 * reset default parameters if necessary
 */
public static void setDefaultParameters() {
	setFloodParameters(RANDOM,3,400,200,1.0);
}
}
