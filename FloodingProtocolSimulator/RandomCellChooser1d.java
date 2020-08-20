public class RandomCellChooser1d
{
	protected boolean[] done = null; // the array used to keep track of which cells have been chosen
	protected int currCount = 0; // keeps track of how many cells we've already chosen on the current run
	private int li = 0; //RENAME holds a number of the amount of pairs that have currently been generated.

	private int cellCount = 0; 

	public RandomCellChooser1d( )
	{
	}

	// initialises the done array 
	// and initialises all values to their defaults
	public void initialise( int Max ) //throws Exception
	{
 		// check the bounds.
		//if( Max < 1 )
		//	throw new Exception( "Negative bounds aren't allowed." );
		
		// initialise state array
		done = new boolean[Max];

		// get the total number of cells
		cellCount = Max;

		// reset the total number of pairs that have been generated
		// more for stats analysis than anything
		li = 0;

		// reset the amount of cells that have been generated
		// for the current run
		currCount = 0;

		// make sure all cells are false
		setAllCells( false );
	}

	public RandomCellChooser1d( int Max ) //throws Exception
	{
		initialise( Max );
	} 

	// a private method that sets the state of all the cells
	// to the state specified as the argument
	private void setAllCells( boolean state )
	{
		for (int x = 0; x < done.length; x++ )
				done[x] = state;
	}

	// resets the cell chooser
	public void reset( )
	{
		currCount = 0;
		li = 0;
		setAllCells( false );
	}

	// returns true if there are cells left
	// that haven't been returned
	public boolean hasNext( )
	{
		return (currCount < cellCount);
	}

	// returns the next randomly chosen value
	// throws an exception if there are no more cells left to choose
	public int next( ) throws Exception
	{
		int cellIndex = 0;

		if( currCount < cellCount )
		{
			boolean found = false;

 			while( ! found ) // currCount < cellCount ) 
			{
     		// get a random values
   	      int n = ((int)(Math.random( ) * Integer.MAX_VALUE)) % done.length;
		li++; // increment the number of times we've had to generate a val               
	
		// check to see that the co-ordinate pair 
		// hasn't already been returned
     		if( done[n] == false ) {
        	   cellIndex = n;
	         done[n] = true;
		   currCount++;	
		   found = true;
       	}
	      }	
		}
		else
			throw new Exception( "All cells have been generated." );

		return cellIndex;
	}
}
