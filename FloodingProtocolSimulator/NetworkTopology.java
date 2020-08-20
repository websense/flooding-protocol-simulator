// rco 23/i/04
// Topology creation places nodes in the landscape class
// NodePos[0] gives the position of the source node

import java.awt.*;

public class NetworkTopology {

	public static String thistopology;

    public static Point [] NodePos;
    // position of each mote (0 to maxNodes-1) in the landscape

    //select either random or regular node placement
	public static void placenodes() {
		NodePos = new Point [FloodParameters.maxNodes];
		if (FloodParameters.topology==FloodParameters.RANDOM) {
			randomtopology();
		} else if (FloodParameters.topology==FloodParameters.REGULAR) {
			regulartopology(); //or try a regular topology
		} else {
			throw new IllegalArgumentException ("FloorParamters.topology must be REGULAR or RANDOM (0 or 1)");
		}
	}


    public static void randomtopology () {
    	thistopology = "random";
    	for (int pos=0; pos<FloodParameters.maxNodes; pos++) {
    		NodePos[pos] = new Point ((int) (Math.random()*(double)FloodParameters.maxX),
    								  (int)(Math.random()*(double)FloodParameters.maxY));
    	}
    }

    public static void regulartopology () {
    	// position motes in a biscuit grid and identify top left as Source

    	//fit maxXnodes into maxX and maxYnodes into maxY
    	int xspace = FloodParameters.maxX /(FloodParameters.maxXnodes+1);
    	int yspace = xspace;
    	int offset = xspace/2;
        thistopology = "medium-hexagonal";
    	int pos=0;
    	int i=0;
    	for (int nx=0; nx<FloodParameters.maxXnodes; nx++) {
    		for (int ny=0; ny<FloodParameters.maxYnodes; ny++) {
    			if (ny%2==0) i=0; else i=xspace/2; // offset odd rows
    			NodePos[pos] = new Point ( nx*xspace+i+offset, ny*yspace+ offset );
    			pos++;
    		}
    	}
    }

}
