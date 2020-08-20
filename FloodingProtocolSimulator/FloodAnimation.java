import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


//TODO why is repainting happening too often ??

public class FloodAnimation extends JFrame
implements ActionListener {
	int frameNumber = -1;
	int scale = 3; //display:landscape = 2:1
	public int margin = 8; //picture border
	public int picturesize = FloodParameters.maxX*scale+margin*4;
	int infoborder = 20;
	Timer timer;


	//create and initialise an instance of the flooding protocol for this animation
	public FloodingProtocol fp;

//	 interface for running the simulation
	//private JFrame userinput = new JFrame("Flooding Parameters");
	private JButton startB = new JButton("Continue");
	private JButton pauseB = new JButton("Pause");
	private JButton endB = new JButton("Reset");
	private JButton setP = new JButton("Set Parameters");
	private JTextArea info = new JTextArea(5,50); //3 rows of 50 column text
	private JComponent picture = new MyComponent();

	void pauseaction() {
		stopAnimation();
	            startB.setEnabled(true);
	            pauseB.setEnabled(false);
	            endB.setEnabled(true);
	            setP.setEnabled(true);
	}

	void resetaction() {
		frameNumber = -1;
	        stopAnimation();
	        info.setText("");
	        setnewparams();
	        repaint();
	            startB.setEnabled(true);
	            pauseB.setEnabled(false);
	            endB.setEnabled(true);
	            setP.setEnabled(true);

	}

	//INTERFACE FOR CHANGING PARAMETERS
	private JFrame userinput = new JFrame("Flooding Parameters");

	private JLabel topollab = new JLabel("Topology type (0 or 1): : ");
	private JTextField topolinp = new JTextField();
	private JLabel footplab = new JLabel("Radio Footprint (0 to 3): ");
	private JTextField footpinp = new JTextField();
	private JLabel nodeslab = new JLabel("Number of Nodes: ");
	private JTextField nodesinp = new JTextField();
	private JLabel landsize = new JLabel("Landscape Size (square max 200): ");
	private JTextField landinp = new JTextField();
	private JLabel retranslab = new JLabel("Retrans Probab (0.0 to 1.0): ");
	private JTextField retransinp = new JTextField();
    private JLabel infolab = new JLabel("Error Messages : ");
	private JTextField paraminfo = new JTextField();
	private JButton defaultB = new JButton("Reset Defaults");
	private JButton changeB = new JButton("Update");

	void showcurrentparams() {
		topolinp.setText(String.valueOf(FloodParameters.topology));
		//TODO have a topoltoString
	   footpinp.setText(String.valueOf(FloodParameters.fptype));
	   nodesinp.setText(String.valueOf(FloodParameters.maxNodes));
	   landinp.setText(String.valueOf(FloodParameters.maxX));
	   retransinp.setText(String.valueOf(FloodParameters.RetransProb));
	   paraminfo.setText("");
	}

	void setnewparams() {
		int t, f, n, l;
		double rp;


		//TODO FIX RESET ACTION
		fp = null; //garbage collect
		try {
			//TODO use input MENU for topolg and fp types
			t = Integer.parseInt(topolinp.getText());
			f = Integer.parseInt(footpinp.getText());
			n = Integer.parseInt(nodesinp.getText());
			l = Integer.parseInt(landinp.getText());
			rp = Double.parseDouble(retransinp.getText());
			FloodParameters.setFloodParameters(t,f,n,l,rp);
			showcurrentparams();
		} catch (NumberFormatException e) {
			paraminfo.setText(""+e);
			//System.out.println("Format error when setting parameters "+e);
		}
		//TODO check all params in sensible range, and if not ask user to reset
			fp = new FloodingProtocol();
	}


	public FloodAnimation(int fps, String windowTitle) {
	        super(windowTitle);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        //create a new world to be animated
	        fp = new FloodingProtocol();

	        //use default Border layout - oversize for margins
	        setSize(picturesize+infoborder,picturesize+3*infoborder);

	        //Set up a timer that calls this object's action handler.
	        int delay = (fps > 0) ? (1000 / fps) : 100;
	        timer = new Timer(delay, this);
	        timer.setInitialDelay(0);
	        timer.setCoalesce(true);

	        //info display area
	        this.add(info,BorderLayout.NORTH);

	        //control area
	        JPanel rest = new JPanel();
	        rest.add(startB);
	        rest.add(pauseB);
	        rest.add(endB);
	        //rest.add(setP);
	        this.add(rest,BorderLayout.SOUTH);

	        //simulation picture area
	        this.add(picture,BorderLayout.CENTER);

	        //TODO add parameter window - see animation
	        userinput.setLayout(new GridLayout(7,2,2,2));

	        userinput.add(topollab);
	        userinput.add(topolinp);
	        userinput.add(footplab);
	        userinput.add(footpinp);
	        userinput.add(nodeslab);
	        userinput.add(nodesinp);
	        userinput.add(landsize);
	        userinput.add(landinp);
	        userinput.add(retranslab);
	        userinput.add(retransinp);
	        userinput.add(infolab);
	        userinput.add(paraminfo);
	        userinput.add(defaultB);
	        userinput.add(changeB);
	        showcurrentparams();
	        userinput.pack();
	        userinput.setVisible(true);

	        //now get ready for animation

	        startB.setEnabled(true);
	        pauseB.setEnabled(false);
	        endB.setEnabled(true);
	        setP.setEnabled(true);

	        this.setVisible(true);


	        addWindowListener(new WindowAdapter() {
	            public void windowIconified(WindowEvent e) {
	                stopAnimation();
	            }
	            public void windowDeiconified(WindowEvent e) {
	                startAnimation();
	            }
	            public void windowClosing(WindowEvent e) {
	                System.exit(0);
	            }
	        });

	        startB.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {
	            startAnimation();
	            startB.setEnabled(false);
	            pauseB.setEnabled(true);
	            endB.setEnabled(false);
	            setP.setEnabled(false);
	            }
	          });


	        pauseB.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {
	              pauseaction();
	            }
	          });

	          endB.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {
	               resetaction();
	            }
	          });

	          /*setP.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {
	               stopAnimation();
	               info.setText("");
	                //open a new window for setting params
	               //userinput.pack();
	               //userinput.setVisible(true);
	            startB.setEnabled(false);
	            pauseB.setEnabled(false);
	            endB.setEnabled(false);
	            setP.setEnabled(false);
	            }
	          });
	          */


	           defaultB.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {  //reset params to default vals
	               FloodParameters.setDefaultParameters();
	               showcurrentparams();
	               resetaction();
	            }
	            });

	          changeB.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {  //get all the inputs and reset simulation
	            	setnewparams();
	               	resetaction();
		    	}
	            });

	    }


//	Can be invoked by any thread (since timer is thread-safe).
    public void startAnimation() {
            if (!timer.isRunning()) {
                timer.start();
            }
    }

    //Can be invoked by any thread (since timer is thread-safe).
    public void stopAnimation() {
        //Stop the animating thread.
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public void actionPerformed(ActionEvent e) {
        //Advance the animation frame.
        frameNumber++;
        fp.updatestate();
        repaint();
        info.setText("PARAMETERS: topology = "+NetworkTopology.thistopology +
        		" footprint ID = "+Integer.toString(FloodParameters.fptype)+
                " RetransProb = "+Integer.toString((int) (FloodParameters.RetransProb * 100))+"%"+
            	"\n number of nodes = "+FloodParameters.maxNodes+
        		" landscape size = "+FloodParameters.maxX + " by "+ FloodParameters.maxY+
                "\n RESULTS: simulation cycles = "+
                Integer.toString(fp.cycles)+" ("+fp.thisround+")"+
                "\n received = "+Integer.toString(fp.received[fp.cycles])+
                " waitingsenders = "+Integer.toString(fp.senderswaiting)+
                " settled = "+Integer.toString(fp.settled[fp.cycles])+

                "\n cycles until receive = "+Integer.toString(fp.receivedcycles)+
	   	        " cycles until settled = "+Integer.toString(fp.settledcycles)
                );



		//TODO stop when flood done pauseaction();

        }


public class MyComponent extends JComponent{

	public void paint(Graphics g ) {
		super.paint(g);

		// display landscape size
		//g.setColor(Color.black);
		//g.drawRect(0,0,FloodParameters.maxX*scale,FloodParameters.maxX*scale);

		// draw landscape cells
		for (int y=0; y < FloodParameters.maxY; y++) {
			for (int x=0; x < FloodParameters.maxX; x++) {
				displaysignal(g,x,y,scale,margin/2); // green, grey, blue
			}//for y
		}//for x

		// draw network nodes
		for (int n=0; n < FloodParameters.maxNodes; n++) {
			displaynstate(g,n,scale,margin/2);
			//TODO OPTIONS //displaymaxbackoff(g,n);
			//displayhops(g,n);
			//displayenergy(g,n);
		}

	}//paint



    	    void displaynstate(Graphics g, int n, int scale, int margin) {
    	    	// orange=WAIT, red=TRANSMIT, darkgray=DONE
    	    	// use big rectangle so nodes standout
    	    	switch (fp.nstate[n]) {
    	    	case 2: g.setColor(Color.darkGray); break;
    	    	case 1: g.setColor(Color.red); break;
    	    	case 0: g.setColor(Color.green); break;
    	    	}
    	    	Point p1 = NetworkTopology.NodePos[n];
    	    	g.fillRect(scale*p1.x+margin, scale*p1.y+margin, 4, 4);
    	    } // displaynstate

    	    void displaysignal(Graphics g, int x, int y, int scale, int margin) {
    	    	// -1=collision 1=data signal 0=silence
    	    	if (FloodParameters.landscape[x][y] == -1)
    	    		g.setColor(Color.yellow); // collision
    	    	else if (FloodParameters.landscape[x][y] == 0)
    	    		g.setColor(Color.white); // no signal
    	    	else
    	    		g.setColor(Color.blue); // a signal
    	    	g.fillRect(scale*x+margin, scale*y+margin, 2,2);
    	    } //display signal

    }

    public static void main (String args[]) {
        FloodAnimation animator = null;
        int fps = 10; //default get frames per second rate

        //or get frames per second from the command line argument.
        if (args.length > 0) {
            try {
                fps = Integer.parseInt(args[0]);
            } catch (Exception e) {}
        }

        animator = new FloodAnimation(fps, "Flood Animation");


    }
}

