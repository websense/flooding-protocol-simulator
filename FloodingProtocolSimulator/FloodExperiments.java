// Revised 23/i/04
// last changes 9/xi/03 rco
// reliabilty  experiments

// run with
// cd XXXTopology
// java WSNexperiments > simdata.date.txt
// then analyse the output file in simdata.date.xml
// 90% reliability = success

import tio.*;
import java.awt.*;

public class FloodExperiments {

	public static void main(String[] args) {

//		create and initialise an instance of the flooding protocol for this animation
		FloodingProtocol fp = new FloodingProtocol();

		int maxRuns = 25; // number of experiments for each setting of the constants
//		100 is better, and 500 used for paper, but  that's pretty slow

//		metrics for reliability experiments
		float reliability;
		float cost;
		float efficiency;
		float succ90runs;
		float succ50runs;
		float succ00runs;

//		local vars for calculating metrics
		float avgrecvpercent;
		float avgreceivedcycles;
		float avgsettledcycles;


		System.out.println("Reliability-Experiments 2008"); System.out.println();
		System.out.println("Sim-runs-per-experiment=" + maxRuns);


		System.out.println("All results float type converted to percentages or as cycles");
		System.out.println();

		System.out.println("Topology, Footprint, RetransProb, Reliability(%), Cost, Effiency(%), "+
		"succ90runs(%), succ50runs(%), succ00runs(%)");
		System.out.println();

		for (int top = 0; top<=1; top++) { //test regular and random topology
			for (int footp=0; footp<4; footp++) { // test all fps
				for (int p=4; p>0; p--) { // test up to 4 retrans probabilities
					FloodParameters.topology = top;
					FloodParameters.fptype = footp;
					FloodParameters.RetransProb=p*0.25d;

					reliability=0f;
					cost=0f;
					efficiency=0f;
					succ90runs=0f;
					succ50runs=0f;
					succ00runs=0f;

					avgrecvpercent = 0f;
					avgreceivedcycles = 0f;
					avgsettledcycles = 0f;

					for (int run=0; run<maxRuns; run++) {

						fp.initstate(); //re-initialise the protocol
						//then run the flood

						int c=0;
						while ((fp.settled[c] < fp.received[c])&&(c<fp.maxCycles-1)) {
							fp.updatestate(); //phase 1
							fp.updatestate(); //phase 2
							fp.updatestate(); //phase 3
							c++;
						}

						// at end of flood run, add current run results to averages
						if (c==fp.maxCycles-1) {
							System.out.println("WARNING: Flood could not finish. Increase maxCycles above "+
									fp.maxCycles);
							System.out.println("settled = "+fp.settled[c]+" received="+fp.received[c]);
						} else {

							int recvcount = fp.settled[c-1];

							avgrecvpercent = avgrecvpercent + (float) recvcount;
							if (recvcount>=(int)(0.90f*(float)FloodParameters.maxNodes))
								succ90runs=succ90runs+1.0f;  //track number of runs where over 90% recv the flood
							else if (recvcount>=(int)(0.50f*(float)FloodParameters.maxNodes))
								succ50runs=succ50runs+1.0f;
							else 	succ00runs=succ00runs+1.0f;  //track number of runs where over 90-50-0% recv the flood
							avgreceivedcycles = avgreceivedcycles + (float) fp.receivedcycles;
							avgsettledcycles = avgsettledcycles + (float) fp.settledcycles;
						} // if run OK

					} // end for all runs

//					calculate averages over all runs
					avgrecvpercent = (avgrecvpercent / (float) maxRuns) / (float) FloodParameters.maxNodes;
					avgreceivedcycles = (avgreceivedcycles / (float) maxRuns) ;
					avgsettledcycles = (avgsettledcycles / (float) maxRuns) ;

//					all results
					reliability = avgrecvpercent;
					cost = avgsettledcycles;
					if (avgsettledcycles>0)
						efficiency = avgreceivedcycles / avgsettledcycles;
					else efficiency = 0;
					succ90runs = succ90runs / (float) maxRuns; // float % of very reliable runs
					succ50runs = succ50runs / (float) maxRuns; // float % of very reliable runs
					succ00runs = succ00runs / (float) maxRuns; // float % of very reliable runs

//					print results as percentages or cycles - ready to paste into spreadsheet

					System.out.println(NetworkTopology.thistopology + ", " +
							FloodParameters.fptype + ", " +
							FloodParameters.RetransProb + ", " +
							reliability*100 + ", " +
							cost + ", " +
							efficiency*100 + ", " +
							succ90runs*100 + ", " +
							succ50runs*100 + ", " +
							succ00runs*100 );
//					System.out.println();

					/*
					if (succ90runs<0.90f) {
						p=0; // don't collect anymore prob retrans data
						System.out.println("No more good runs for RetransProb < "+FloodParameters.RetransProb);
					}
					*/
				}// for retrans prob
			} // for fp type
		} //for topology type

		System.out.println();
		System.out.println("Simulations completed");
	}// end main


} // end WSNexperiments

