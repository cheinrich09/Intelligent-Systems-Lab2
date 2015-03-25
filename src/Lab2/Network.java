package Lab2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Network {

	public class WeightSet
	{
		int[][] connections;
		double[][] weights;
		
		public WeightSet()
		{
			connections = new int[nodeIndex+1][nodeIndex+1];
			weights = new double[nodeIndex+1][nodeIndex+1];
			for (int i = 0; i <= nodeIndex; i++)
			{
				for(int j = 0; j <= nodeIndex; j++)
				{
					connections[i][j] = 0;
					weights[i][j] = 0;
				}
			}
		}
		//set the weight from i to j
		public void Set(int i, int j, double w)
		{
			if(connections[i][j]==1)
			{
				weights[i][j] = w;
			}
		}
		//add connection between i and j
		public void Add(int i, int j)
		{
			connections[i][j] = 1;
		}
		public boolean Validate(int i, int j)
		{
			if (connections[i][j] == 1)
			{
				return true;
			}
			return false;
		}
		public double Get(int i, int j)
		{
			return weights[i][j];
		}
	}

	public class Layer
	{
		//the nodes in this layer
		public List<Node> nodes;
		public Layer()
		{
			nodes = new ArrayList<Node>();
		}
	}
	public class Node
	{
		//each node should have an index (for calculating weights) and a pointer to its layer
		//1 input, 2 hidden, 3 output
		int layer;
		double input;
		int index;
		double a;
		double delta;
		
		public Node(int i, int l)
		{
			index = i;
			layer = l;
			input = 0;
			a = 0;
			delta = 0;
		}
	}
	public class Example
	{
		
		double input[];
		String out;
		double output[];
		
		public Example(String text)
		{
			input = new double[numInputs];
			getInput(text);
			output = new double[3];
			output[0] = 0.0;
			output[1] = 0.0;
			output[2] = 0.0;
		}
		public void getInput(String text)
		{
			//points of comparison
			double repeatLetters = 0; //repeated letters common in dutch
			//double wordLength = 0; //dutch tends to use compound words
			double j =0,k =0,w=0,x=0,y = 0; //Italian alphabet doesn't contain these 3 letters, meaning they are only from imported words
			//double th = 0;//the most common diagraph (two letter pair) in English is TH
			double ij = 0;//diagraph from dutch
			//double accent = 0;
			char current = ' ';
			char previous;
			//int numLetters = 0;
			for(int i = 0; i < text.length(); i++)
			{
				previous = current;
				current = text.charAt(i);
				if (Character.compare(previous, current) == 0)
				{
					repeatLetters++;
				}
				if((Character.compare(previous, 'i') == 0 || Character.compare(previous, 'I') == 0)
						&& Character.compare(current, 'j')==0)
				{
					ij++;
				}
				if	(Character.compare(current, 'j') == 0 || Character.compare(current, 'J') == 0)
				{
					j++;
				}
					
				if (Character.compare(current, 'k') == 0 || Character.compare(current, 'K') == 0)
				{
					k++;
				}
				if (Character.compare(current, 'w') == 0 || Character.compare(current, 'W') == 0)
				{
					w++;
				}
				if (Character.compare(current, 'x') == 0 || Character.compare(current, 'X') == 0)
				{
					x++;
				}
				if (Character.compare(current, 'y') == 0 || Character.compare(current, 'Y') == 0)
				{
					y++;
				}
				/*if (Character.compare(current, ' ') == 0 
						|| Character.compare(current, '.') == 0 
						|| Character.compare(current, ':') == 0 
						|| Character.compare(current, ';') == 0 
						|| Character.compare(current, ',') == 0
						|| Character.compare(current, '!') == 0
						|| Character.compare(current, '?') == 0)
				{
					if(numLetters > 10)
					{
						//wordLength++;
					}
					numLetters = 0;
				}
				else
				{
					numLetters++;
				}*/
			}
			input[0] = repeatLetters;
			//input[1] = wordLength;
			input[1] = j;
			input[2] = k;
			input[3] = w;
			input[4] = x;
			input[5] = y;
			//input[3] = th;
			input[6] = ij;
		}
		public void setOutput(char l)
		{
			if (Character.compare(l, 'd')==0)
			{
				output[0] = 1.0;
			}
			else if (Character.compare(l, 'i')==0)
			{
				output[2] = 1.0;
			}
			else output[1] = 1.0;
		}
	}
	
	//example list
	List<Example> examples;
	//array of layers, with a total of three layers, each with a list of nodes
	//Layer[0] is input, Layer[1] is hidden, Layer[2] is output
	Layer[] layers;
	WeightSet weights;
	int numInputs;
	double alpha;
	int numHidden;
	int numOutput;
	int nodeIndex;
	
	public Network()
	{
		numInputs = 7;
		numHidden = 5;
		numOutput = 3;
		nodeIndex = 0;
		examples = new ArrayList<Example>();
		alpha = 0.25;
		layers = new Layer[3];
	}
	
	public void init()
	{
		layers[0] = new Layer();
		layers[1] = new Layer();
		layers[2] = new Layer();
		//set up the nodes
		for (int a = 0; a < numInputs; a++)
		{
			nodeIndex++;
			layers[0].nodes.add(new Node(nodeIndex, 0));		
		}
		for (int b = 0; b<numHidden; b++)
		{
			nodeIndex++;
			layers[1].nodes.add(new Node(nodeIndex, 1));		
		}
		for (int c = 0; c<numOutput; c++)
		{
			nodeIndex++;
			layers[2].nodes.add(new Node(nodeIndex, 2));	
		}
		weights = new WeightSet();
		//add connections, setting them to 0
		//if training, will set to random values, train, then save vals
		//if testing, will load weights from saved vals
		for (int l = 2; l > 0; l--)
		{
			for(int j = 0; j < layers[l].nodes.size(); j++)
			{
				for (int i = 0; i < layers[l-1].nodes.size(); i++)
				{
					int start = layers[l-1].nodes.get(i).index;
					int end = layers[l].nodes.get(j).index;
					weights.Add(start, end);
				}
			}
		}
	}

	//read in and set weights from file
	public void loadWeights() throws FileNotFoundException
	{
		BufferedReader weightIn = new BufferedReader(new FileReader("./resources/weights.txt"));
		String line;
		try {
			while((line = weightIn.readLine())!= null)
			{
				String[] elements = line.split(",");
				weights.Set(Integer.parseInt(elements[0]), Integer.parseInt(elements[1]), Double.parseDouble(elements[2]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveWeights() throws FileNotFoundException
	{
		PrintWriter saveWeights = new PrintWriter("./resources/weights.txt");
		for (int i = 1; i <= nodeIndex; i++)
		{
			for(int j = 1; j <= nodeIndex; j++)
			{
				if(weights.Validate(i, j))
				{
					//System.out.println(i+","+j);
					saveWeights.println(i+","+j+","+weights.Get(i, j));
				}
			}
		}
		saveWeights.close();
	}
	//reads in each training text from the resources folder, for each language, creating an example for each
	//each text is named as either E#, I#, or D#, where E is English, I is Italian, and d is Dutch
	//and # is the number of that text for that language
	//the example takes the text and calculates the input from it during construction
	//then the language is set based on the language from the file name.
	//either evenly spread the example read in (so read in one of each language before reading in the second of a language)
	//or randomly spread so that as the network iterates through each example, it won't become overly biased towards a single language
	public void trainingIn() throws IOException
	{
		String training_dir = "./training";
		File dir = new File(training_dir);
		File[] files = 	dir.listFiles();
		
		for (File f: files)
		{
			if(f.isFile())
			{
				BufferedReader inStream = null;
				char l = ' ';
				if (f.getName().charAt(0) == 'E')
				{
					l = 'e';
				}
				else if (f.getName().charAt(0) == 'D')
				{
					l = 'd';
				}
				else if (f.getName().charAt(0) == 'I')
				{
					l = 'i';
				}
				try{
					inStream = new BufferedReader(new FileReader(f));
					String text = inStream.readLine();
					
					examples.add(new Example(text));
					examples.get(examples.size()-1).setOutput(l);
				}
				finally
				{
					if (inStream != null)
					{
						inStream.close();
					}
				}
			}
		}
	}
	
	public double G(double in)
	{
		return 1/(1+Math.exp(-in));
	}
	
	public void bpLearning() throws FileNotFoundException
	{
		
		Random rand = new Random(System.currentTimeMillis());
		int k = 0;
		for (int i = 1; i <= nodeIndex; i++)
		{
			for(int j = 1; j <= nodeIndex; j++)
			{
				weights.Set(i, j, rand.nextDouble());
			}
		}
		while(true)
		{
			for(Example e : examples)
			{
				//propogate forward
				for(Node i :layers[0].nodes)
				{
					i.a = e.input[i.index-1];
				}
				for(int l =1; l < layers.length; l++)
				{
					for(Node j : layers[l].nodes)
					{
						double inj = 0;
						///
						for (Node i :layers[l-1].nodes)
						{
							inj += (i.a * weights.Get(i.index, j.index));
						}
						j.input=inj;
						j.a = G(inj);
					}
				}
				//propagate error back
				int s = 0;
				for(Node j : layers[2].nodes)
				{
					j.delta = (G(j.input)*(1-G(j.input)))*(e.output[s]-j.a);
					s++;
				}
				for(int l = layers.length-2; l >= 0; l--)
				{
					for (Node i : layers[l].nodes)
					{
						double dJ = 0;
						for (Node j : layers[l+1].nodes)
						{
							dJ += weights.Get(i.index, j.index) * j.delta;
						}
						i.delta = (G(i.input)*(1-G(i.input)))*dJ;
					}
				}
				//update weights
				for(int l =0; l < layers.length-1; l++)
				{
					for (Node i:layers[l].nodes)
					{
						for (Node j: layers[l+1].nodes)
						{
							weights.Set(i.index, j.index, (weights.Get(i.index, j.index)+(alpha*i.a*j.delta)));
						}
					}
				}
			}
			if(k >100)
			{
				break;
			}
			else
			{
				k++;
			}
		}
		saveWeights();
	}
	//uses back propagation for training
	//first reads in each example from training folder
	//then builds base network
	//then sets random small weights
	//then back propagation training
	//then save weights to resources folder
	public void train() throws IOException
	{
		trainingIn();
		bpLearning();
		
	}
	
	
	//takes in a text
	//creates an example(without answer)
	//load network using weights from resource folder
	//run example through network
	//return result to be output
	public String test(String text) throws FileNotFoundException
	{
		loadWeights();
		Example e = new Example(text);
		//return "English";
		//propogate forward
		for(Node i :layers[0].nodes)
		{
			i.a = e.input[i.index-1];
		}
		for(int l =1; l < layers.length; l++)
		{
			for(Node j : layers[l].nodes)
			{
				double inj = 0;
				///
				for (Node i :layers[l-1].nodes)
				{
					inj += (i.a * weights.Get(i.index, j.index));
				}
				j.input=inj;
				j.a = G(inj);
			}
		}
		double dOut = layers[2].nodes.get(0).a;
		double eOut = layers[2].nodes.get(1).a;
		double iOut = layers[2].nodes.get(2).a;
		System.out.println("Dutch: "+dOut);
		System.out.println("Italian: "+iOut);
		System.out.println("English: "+eOut);
		if (dOut > eOut && dOut > iOut)
		{
			return "Dutch";
		}
		else if (iOut > eOut && iOut > dOut)
		{
			return "Italian";
		}
		else
		{
			return "English";
		}
	}

}
