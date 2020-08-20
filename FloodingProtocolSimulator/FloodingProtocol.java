//26 Feb version
//modified to match Tinyos SMAC backoff
//large interference (and CS) radius as in Xu et al

import java.awt.*;
import javax.swing.*;


/* Recompiled 23/i/03
 // Simulator constants, variables and actions: init and updatestate
 */

public class FloodingProtocol {
boolean debug = false; //turns on (or off) debug printing to System.out
	// SIMULATOR CONSTANTS

	// STATE VARIABLES FOR EACH NODE
	public int [] nstate; // 0=WAIT TO RECEIVE, 1=TRANSMITTING, 2=DONE
	public int [] nmessage; // data>0, 0=empty or first
	public int [] nbackoff; // number of steps to wait (in state 1)
	public int [] nmaxbackoff; // sum of all n's backoffs while waiting to send

	//METRICS FOR THE SIMULATOR
	//TODO make these vars private ??
	public int maxCycles=400;  // will calculate cycles from 0 to maxCycles-2
	// need >200 cycles for big MAC of tinyos
	public int cycles;		//simulation cycles done
	public int thisround;	//for drawing simulation in steps

	public int[] received = new int [maxCycles];  // number of nodes who have received
	public int[] settled  = new int [maxCycles];  // number of nodes who have finished
	public int receivedcycles; // number of cycles all nodes who can have recv the flood
									  // need to calc this later once total nodes settled is known
	public int settledcycles;  // number of cycles until no nodes are waiting

	public int senderswaiting; // number of nodes who are waiting to transmit the flood
	public int swaiting;

	static RandomCellChooser1d rcc;  // used to process transmission nodes in random order


	public FloodingProtocol() { //constructs an instance of the flooding protocol
		initstate();
	}

//	simulator FSM initial state

	public void initstate() { //throws Exception {
		// assumes simulator topology constants maxX, maxY, maxNodes, NodePos, Source
		// have already been initialised

		NetworkTopology.placenodes(); //random or regular

		// initialise clear landscape
		for (int x=0; x < FloodParameters.maxX; x++) {
			for (int y=0; y < FloodParameters.maxY; y++) {
				FloodParameters.landscape[x][y]=0;  // empty landscape
			} //for y
		}// for x


		//initialise node state variables
		nstate = new int [FloodParameters.maxNodes]; // 0=WAIT TO RECEIVE, 1=TRANSMITTING, 2=DONE
		nmessage = new int [FloodParameters.maxNodes]; // data>0, 0=empty or first
		nbackoff = new int [FloodParameters.maxNodes]; // number of steps to wait (in state 1)
		nmaxbackoff = new int [FloodParameters.maxNodes]; // sum of all n's backoffs while waiting to send
		for (int n=0; n<FloodParameters.maxNodes; n++) {
			nstate[n] = 0;
			nmessage[n] = 0;
			nbackoff[n] = 1 + (int) Math.round( Math.random()*(FloodParameters.initmaxBackoff));
			nmaxbackoff[n] = 1;
		} // for n

		rcc = new RandomCellChooser1d( FloodParameters.maxNodes );
		// initialise source node for broadcast flood to middle node ID
		nstate[FloodParameters.Source] = 1;
		nmessage[FloodParameters.Source] = 1;

		// and initialise simulator metric variables
		cycles = 0;
		thisround = 0;
		received[0] = 1; // so far, only Source node has got its packet
		settled[0] = 0;
		for (int i=1; i<maxCycles; i++) { received[i]=0; settled[i]=0; }
		senderswaiting = 1; // one node has received the flood
		swaiting = 1;
		settledcycles = 0;  // 0 until set to cylces taken until all who can have settled
		receivedcycles = 0; // 0 until set to cycles taken until all who can have received
	} // initstate




//	OPERATIONS WHICH CHANGE GLOBAL STATE: transmit, receive and clear

	// throws an exception as a result of transmitphase having
	// to throw an exception

	public void updatestate() { //throws Exception {
	int maxsett, c;

		if ((cycles < maxCycles-1)&&(settledcycles==0)) {
			// while still flooding to do
			// calculate current state
			switch (thisround) {
			case 0:  transmitphase(); thisround++; break;
			case 1:  receivephase(); thisround++; break;
			case 2:  clearlandscapephase();
						senderswaiting = swaiting;
						if (debug) { System.out.println("cycles = "+cycles); }
						if ((received[cycles]==settled[cycles])&&(settledcycles==0)) {
							// flood finished, so:
							settledcycles=cycles;
							// now count backward to see when received settled
							maxsett=settled[cycles];
							c=settledcycles-1;
							while (c>0) { // found where settled was finished
								if (received[c] < maxsett) {
									receivedcycles = c+1;
									c=0;
								} else { // keep looking
									c--;
								}
							}
						} // if
						else { // continue simulation
							received[cycles+1]=received[cycles]; // initalise next round's counters
							settled[cycles+1]=settled[cycles];
							cycles++; thisround=0;
						}
			break;
			}//switch
		}//cycles
	}//updatestate


	// throws an exception now,
	// as rcc.next( ) will throw an exception if all the cells have already
	// been returned

	public void transmitphase() { //throws Exception{
		int n;
		// transmission phase, send or accept ready messages
		// need to traverse the nodes at random
		// reset the random cell chooser
		// so it starts again
		rcc.reset( );

		// while there are still nodes that haven't been
		// chosen
		while( rcc.hasNext()) {
			// get the next node index
			try {
				n = rcc.next(); // choose next node ID
			} catch (Exception e) {
				n=-1; //should never happen
				System.err.println("RCC generated node n="+n+" not in "+FloodParameters.maxNodes);
			}
			Point p1 = NetworkTopology.NodePos[n];

			if (nstate[n]==1) {
				if (nbackoff[n]==0) {
					if (carrierfree(p1.x,p1.y)) {
						broadcast(n,
								FloodParameters.receptionpercent[FloodParameters.fptype],
								FloodParameters.interferenceradius[FloodParameters.fptype]);
						nstate[n] = 2; // broadcast done
						swaiting--; // this node is no longer waiting to send
						settled[cycles]++;
					} // if carrier free
					else { // reset nbackoff automatically gives 1 delay, then wait maxBackoff more cycles
						nbackoff[n] = (int) Math.round( Math.random()*(FloodParameters.maxBackoff));
						nmaxbackoff[n] = nmaxbackoff[n]+nbackoff[n];
					} // else
				} // if nbackoff==0 then reduce nbackoff
				else nbackoff[n]--;
			} // if nstate==1
		}//while
	}//end transmitphase



	public boolean islandscapenode(int x, int y) {
		return (x>=0 && x<FloodParameters.landscape.length &&
				y>=0 && y<FloodParameters.landscape[0].length);
	}


	/*
	 * true if landscape at x,y contains data signal
	 */
	public boolean datasense(int x, int y) {
		return (FloodParameters.landscape[x][y] > 0);
	}

	/*
	 * true if landscape at x,y has no nodes transmitting
	 */
	public boolean carrierfree(int x, int y) {
		// does x,y detect any other nodes sending ?
		// only if a signal already at x,y
		return (FloodParameters.landscape[x][y] == 0);
	}// carrierfree


	/*
	 * paint node n's footprint over the landscape
	 */
	public void broadcast(int n, int[] footprint, int intrad) {

		int thiscell;
		int thisrad;
		Point p1 = NetworkTopology.NodePos[n];
		int xs = p1.x;
		int ys = p1.y;

		for (int x = xs-FloodParameters.maxFPradius; x < xs+FloodParameters.maxFPradius; x++) {
			for (int y = ys-FloodParameters.maxFPradius; y < ys+FloodParameters.maxFPradius; y++) {
				// 1. check this part of the footprint is in the landscape
				if (islandscapenode(x,y)) {
					// 2. calculate radius
					thisrad = (int) Math.round (
							Math.sqrt((xs-x)*(xs-x) + (ys-y)*(ys-y)) );
					// 3. check the radius is in the scope of probability data we have
					if (thisrad < FloodParameters.maxFPradius && thisrad > 0) {
						// 4. toss to decide whether reception is good or not
						if ( (footprint[thisrad]>0) &&
								( ((int) Math.floor(100*Math.random())) <= (footprint[thisrad]) ))
							thiscell = 1; // good reception - can't include prob 0, but may need 100
						else {
							// 5. if bad then noise within interference radius or clear without
							if (thisrad <= intrad)
								thiscell = -1; // noisy reception
							else
								thiscell = 0;  // no reception
						} // if else calc type of this cell

						// 6. now merge this cell with the landscape
						if ((thiscell == -1) || (FloodParameters.landscape[x][y] == -1))
							FloodParameters.landscape[x][y] = -1; // collision
						else
							if ((thiscell == 1) && (FloodParameters.landscape[x][y] == 0))
								FloodParameters.landscape[x][y]=nmessage[n];
							else
								if ((thiscell == 1) && (FloodParameters.landscape[x][y]>0))
									FloodParameters.landscape[x][y] = -1; // collision
						// else (thiscell == 0) and no change in landscape
					}//is valid reception range
				}//is landscape node
			}//for x
		}//for y

	} //broadcast


	/*
	 * forwarding rule used by each node to decide whether to forward the flood packet or not
	 * @param n node deciding whether to retransmit or not
	 * @returns boolean true if node will retransmit and false otherwise
	 */
	public boolean forwardingcondition(int n) {
		return (Math.random() < FloodParameters.RetransProb );
	}

	public void receivephase() {
		// called after network phase is finished,
		for (int n=0; n < FloodParameters.maxNodes; n++) {
			Point p1 = NetworkTopology.NodePos[n];
			if ((nstate[n]==0) &&
					(datasense(p1.x,p1.y))) {
				nmessage[n]=FloodParameters.landscape[p1.x][p1.y]+1; // message ID just broadcast there
				if (forwardingcondition(n))  {
					nstate[n] = 1; // choose to retransmit
					swaiting++;
				}
				else {
					nstate[n] = 2; // choose not to retransmit
					settled[cycles]++; // and register this node as settled
				}
				received[cycles]++;
			} // if
		} //for n
	} // receivephase

	public void clearlandscapephase() {
		// called after send and recv phases finished,
		// ignore maxhops for now: maxhops = calcmaxhops();

		for (int x=0; x < FloodParameters.maxX; x++) {
			for (int y=0; y < FloodParameters.maxY; y++) {
				FloodParameters.landscape[x][y]=0; // clear last transmission signal
			} //for y
		}// for x
	} // clearlandscapephase


	public int calcmaxhops() { // not used for reliability exps
		int maxh = -1;
		for (int n=0; n < FloodParameters.maxNodes; n++) {
			if ((nmessage[n]-1) > maxh)
				maxh = nmessage[n]-1;
		}
		return maxh;
	} // calcmaxhops
}

