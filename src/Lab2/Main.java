package Lab2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	//public String filename;
	
	//
	//create menu with two options, train and test
	//train will train the network using the training set
	//test will prompt the user to enter in a text,
	//then load the current network and send it the text
	//which will then output its guess which language the text is
	//no need for providing filenames or cmd line args
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String select = " ";
		String text;
		Network n;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(!select.equals("Exit"))
		{
			System.out.println("Select Desired Operation:");
			System.out.println("Train: Train Network");
			System.out.println("Test: Test Nextwork");
			System.out.println("Exit: Exit");
			select = in.readLine();
			//System.out.println(select);
			if (select.equals("Train"))
			{
				n = new Network();
				n.init();
				n.train();
				n.saveWeights();
			}
			else if (select.equals("Test"))
			{
				System.out.println("Enter the text to test:");
				text = in.readLine();
				n = new Network();
				n.init();
				System.out.println("This text is in "+n.test(text));
				System.out.println(" ");
			}
		}
		in.close();
	}

}
