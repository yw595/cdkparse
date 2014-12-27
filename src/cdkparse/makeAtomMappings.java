package cdkparse;

import java.io.*;
import java.util.*;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.nonotify.*;

public class makeAtomMappings {
	
	public static char[] atomLettersArray;
	
	public makeAtomMappings()
	{
		makeAtomLettersArray();
	}
	
	public static void makeAtomLettersArray()
	{
		atomLettersArray = new char[62];
		for(int i = 0;i < 26;i++)
			atomLettersArray[i] = (char) (97+i);
		for(int i = 0;i < 26;i++)
			atomLettersArray[26+i] = (char) (65+i);
		for(int i = 0;i < 10;i++)
			atomLettersArray[52+i] = (char) (48+i);
	}
	
	public static void main(String[] args)
	{
		try{
			makeAtomLettersArray();
			
			ArrayList<Object> ret;
			Map<Integer, Character> numbersToLetters;
			PrintWriter writer;
			
			Map<String, ArrayList<ArrayList<String>>> rxnsToMets = makeRxnToMets();
			
			Map<String, ArrayList<int[]>> metsToHomotopicSets = makeMetsToHomotopicSets();
			
			BufferedReader reader = new BufferedReader(new FileReader(
			"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\database_CarbonFateMapsRxns.txt"));
			writer = new PrintWriter(new FileOutputStream(
			"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\atomMappings3.csv"));
			
			String readerLine = reader.readLine();
			readerLine = reader.readLine();
			int count = 0;
			while(readerLine != null)
			{
				count++;
				System.out.println(count);
				ret = getFateMaps(readerLine,count);
				ArrayList<int[]> reactantsCarbonNumbering = (ArrayList<int[]>) ret.get(0);
				ArrayList<int[]> productsCarbonNumbering = (ArrayList<int[]>) ret.get(1);
				numbersToLetters = (HashMap<Integer, Character>) ret.get(2);
				ArrayList<Map<Integer, Integer>> fateMappings = (ArrayList<Map<Integer, Integer>>) ret.get(3);
				String rxnName = (String) ret.get(4);
				String ECNumber = (String) ret.get(5);
				String KEGGID = (String) ret.get(6);
				System.out.println(KEGGID);
				
				ArrayList<ArrayList<Map<Integer, Integer>>> allFateMappings = 
				new ArrayList<ArrayList<Map<Integer, Integer>>>();
				allFateMappings.add(fateMappings);
				ArrayList<String> reactants;
				ArrayList<String> products;
				if(rxnsToMets.containsKey(KEGGID))
				{
					reactants = rxnsToMets.get(KEGGID).get(0);
					products = rxnsToMets.get(KEGGID).get(1);
				}
				else
				{
					System.out.println(KEGGID);
					readerLine = reader.readLine();
					continue;
				}
				
				ArrayList<ArrayList<Map<Integer, Integer>>> newAllFateMappings = null;
				try{
				newAllFateMappings = makeNewAllFateMappings(
				allFateMappings,reactants,metsToHomotopicSets,true,count);
				}
				catch(CDKException c){
					System.out.println(c);
					writer.print(rxnName + ",,Reactant " + c.getMessage());
					String homotopicSetsString = "";
					for(int i = 0;i < reactants.size();i++)
						if(metsToHomotopicSets.containsKey(reactants.get(i)))
						{
						for(int j = 0;j < metsToHomotopicSets.get(reactants.get(i)).size();j++)
						{
							int[] tempArray = metsToHomotopicSets.get(reactants.get(i)).get(j);
							homotopicSetsString += "(";
							for(int k = 0;k < tempArray.length;k++)
								homotopicSetsString += tempArray[k] + ":";
							homotopicSetsString += ")";
						}
						}
					writer.println(" " + homotopicSetsString);
					readerLine = reader.readLine();
					continue;
				}
				allFateMappings = newAllFateMappings;
				newAllFateMappings = null;
				
				try{
				newAllFateMappings = makeNewAllFateMappings(
				allFateMappings,products,metsToHomotopicSets,false,count);
				}
				catch(CDKException c){
					System.out.println(c);
					writer.print(rxnName + ",,Product " + c.getMessage());
					String homotopicSetsString = "";
					for(int i = 0;i < products.size();i++)
						if(metsToHomotopicSets.containsKey(products.get(i)))
						{
							for(int j = 0;j < metsToHomotopicSets.get(products.get(i)).size();j++)
							{
								int[] tempArray = metsToHomotopicSets.get(products.get(i)).get(j);
								homotopicSetsString += "(";
								for(int k = 0;k < tempArray.length;k++)
									homotopicSetsString += tempArray[k] + ":";
								homotopicSetsString += ")";
							}
						}
					writer.println(" " + homotopicSetsString);
					readerLine = reader.readLine();
					continue;
				}
				allFateMappings = newAllFateMappings;
				newAllFateMappings = null;
				
				ArrayList<ArrayList<String>> variantReactantSides = new ArrayList<ArrayList<String>>();
				try{
				variantReactantSides.add(makeVariantSide2(reactantsCarbonNumbering, productsCarbonNumbering,
				numbersToLetters,null,true));
				}
				catch(CDKException c){
					System.out.println(c);
					writer.println(rxnName + ",Reactant " + c.getMessage());
					readerLine = reader.readLine();
					continue;
				}
				
				ArrayList<ArrayList<String>> variantProductSides = new ArrayList<ArrayList<String>>();
				try{
					for(int i = 0;i < allFateMappings.size();i ++)
					{
						fateMappings = allFateMappings.get(i);
						variantProductSides.add(makeVariantSide2(reactantsCarbonNumbering, productsCarbonNumbering,
						numbersToLetters,fateMappings,false));
					}
				}
				catch(CDKException c){
					System.out.println(c);
					writer.print(rxnName + ",Product " + c.getMessage());
					if(readerLine.contains("("))
						writer.println(",has parentheses");
					else
						writer.println(","+readerLine);
					readerLine = reader.readLine();
					continue;
				}
				
				String reactantSide = makeSide(variantReactantSides);
				
				String productSide = makeSide(variantProductSides);
				
				//remove all commas for csv file
				rxnName = rxnName.replaceAll(",","");
				ECNumber = ECNumber.replaceAll(","," and ");
				
				System.out.println(rxnName + "," + KEGGID + "," + ECNumber + "," + reactantSide + " = " + productSide);
				writer.println(rxnName + "," + KEGGID + "," + ECNumber + "," + reactantSide + " = " + productSide);
				readerLine = reader.readLine();
			}
			
			writer.close();
			reader.close();
			
			while("a".equals("b"))
			{
				//System.out.println(readerLine);
				String[] fields = readerLine.split("\\t");
				for(int i = 0;i < fields.length; i++)
					if(i==2)
						System.out.println(fields[i]);
				readerLine = reader.readLine();
			}
			if(false)
			{
			reader = new BufferedReader(new FileReader(
			"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\Locasale Lab\\CLCA_output.out"));
			readerLine = reader.readLine();
			writer = new PrintWriter(new FileOutputStream(
			"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\Locasale Lab\\atomMappings.csv"));
			SmilesParser sp = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
			while(readerLine != null)
			{	
				System.out.println(readerLine);
				try{
					ret = getMolecules(readerLine,sp);
				}
				catch(InvalidSmilesException e){
					System.out.println(e);
					String[] fields = readerLine.split("\\|\\|");
					String rxnName = fields[0];
					writer.println(rxnName + "," + e.getMessage());
					readerLine = reader.readLine();
					continue;
				}
				String rxnName = (String) ret.get(0);
				ArrayList<IMolecule> reactantsMolecules = (ArrayList<IMolecule>) ret.get(1);
				ArrayList<IMolecule> productsMolecules = (ArrayList<IMolecule>) ret.get(2);
				
				ArrayList<Map<Integer, Integer>> mappings;
				//System.out.println(rxnName);
				try{
					mappings = getMappings(rxnName);
				}
				catch(IOException e){
					System.out.println(e);
					writer.println(rxnName + "," + e.getMessage());
					readerLine = reader.readLine();
					continue;
				}
				if(mappings.size()==0)
				{
					System.out.println("Empty mapping file");
					writer.println(rxnName + ",Empty mapping file");
					readerLine = reader.readLine();
					continue;
				}
				
				ArrayList<ArrayList<IAtom>> sortedReactantsMolecules = sortMolecules(reactantsMolecules);
				
				ArrayList<ArrayList<IAtom>> sortedProductsMolecules = sortMolecules(productsMolecules);
				
				ret = makeNumbersToLettersToAtoms(sortedReactantsMolecules);
				Map<Character, IAtom> lettersToAtoms = (Map<Character, IAtom>) ret.get(0);
				numbersToLetters = (Map<Integer, Character>) ret.get(1);
				
				ArrayList<ArrayList<String>> variantReactantSides = new ArrayList<ArrayList<String>>();
				try{
				variantReactantSides.add(makeVariantSide(sortedReactantsMolecules, 
				true, numbersToLetters, null,null));
				}
				catch(CDKException c){
					System.out.println(c);
					writer.println(rxnName + ",Reactant " + c.getMessage());
					continue;
				}
				
				ArrayList<ArrayList<String>> variantProductSides = new ArrayList<ArrayList<String>>();
				try{
					for(int i = 0;i < mappings.size();i ++)
					{
						variantProductSides.add(makeVariantSide(sortedProductsMolecules, 
						false, numbersToLetters, mappings.get(i),lettersToAtoms));
					}
				}
				catch(CDKException c){
					//System.out.println(c);
					writer.print(rxnName + ",Product " + c.getMessage());
					if(readerLine.contains("("))
						writer.println(",has parentheses");
					else
						writer.println(","+readerLine);
					readerLine = reader.readLine();
					continue;
				}
				
				String reactantSide = makeSide(variantReactantSides);
				
				String productSide = makeSide(variantProductSides);
				
				writer.println(rxnName + "," + reactantSide + " = " + productSide);
				readerLine = reader.readLine();
			}
			reader.close();
			writer.close();
		}
		}
		catch(IOException e){
			System.out.println("IOException on CLCA file");
		}
		}

	public static ArrayList<Object> getMolecules(String readerLine, SmilesParser sp) throws InvalidSmilesException
	{
		String[] fields = readerLine.split("\\|\\|");
		String rxnName = fields[0];
		String noNumbersField = "";
		for(int i = 0;i < fields[1].length();i++)
		{
			if(!Character.isDigit(fields[1].charAt(i)) && (fields[1].charAt(i)!=':'))
			{
				noNumbersField += fields[1].charAt(i);
			}
		}
		String[] reactantsAndProducts = noNumbersField.split(">>");
		String[] reactantsStrings = reactantsAndProducts[0].split("\\.");
		String[] productsStrings = reactantsAndProducts[1].split("\\.");
		
		ArrayList<IMolecule> reactantsMolecules = new ArrayList<IMolecule>();
		for(int i = 0;i < reactantsStrings.length;i++)
		{
			reactantsMolecules.add(sp.parseSmiles(reactantsStrings[i]));
		}
		
		ArrayList<IMolecule> productsMolecules = new ArrayList<IMolecule>();
		for(int i = 0;i < productsStrings.length;i++)
		{
			productsMolecules.add(sp.parseSmiles(productsStrings[i]));
		}
		
		ArrayList<Object> ret = new ArrayList<Object>();
		ret.add(rxnName);
		ret.add(reactantsMolecules);
		ret.add(productsMolecules);
		return ret;
	}
	
	public static ArrayList<Map<Integer, Integer>> getMappings(String rxnName) throws IOException
	{
			BufferedReader mappingReader = new BufferedReader(new FileReader(
			"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\imPR90068_updatedmappings_20120320\\" 
			+ rxnName + ".txt"));
			String mappingLine = mappingReader.readLine();
			ArrayList<Map<Integer, Integer>> mappings = new ArrayList<Map<Integer, Integer>>();
			while(mappingLine != null)
			{
				if(mappingLine.substring(0,1).equals("#"))
				{
					mappingLine = mappingReader.readLine();
					continue;
				}
				else
					mappingLine = mappingLine.substring(1,mappingLine.length()-1);
				if(mappingLine.equals(""))
					break;
				mappings.add(new HashMap<Integer, Integer>());
				if(!mappingLine.contains("], ["))
				{
					mappings.get(mappings.size()-1).put(
					Integer.parseInt(mappingLine.substring(1,mappingLine.indexOf(", "))),
					Integer.parseInt(mappingLine.substring(mappingLine.indexOf(",")+2, mappingLine.length()-1)));
				}
				
				String[] mapping = mappingLine.split("\\], \\[");
				mapping[0] = mapping[0].substring(1);
				mapping[mapping.length-1] = mapping[mapping.length-1].substring(
				0,mapping[mapping.length-1].length()-1);
				for(int i = 0;i < mapping.length;i++)
				{
					String[] atomMap = mapping[i].split(", ");
					mappings.get(mappings.size()-1).put(
					Integer.parseInt(atomMap[0]),Integer.parseInt(atomMap[1]));
				}
				mappingLine = mappingReader.readLine();
			}
			mappingReader.close();
			return mappings;
	}
	
	public static ArrayList<ArrayList<IAtom>> sortMolecules(ArrayList<IMolecule> aMolecules)
	{
		ArrayList<IMolecule> molecules = new ArrayList<IMolecule>();
		for(int i = 0;i < aMolecules.size();i++)
			molecules.add(aMolecules.get(i));
		Collections.sort(molecules,new MoleculeComparator());
		ArrayList<ArrayList<IAtom>> sortedMolecules= new ArrayList<ArrayList<IAtom>>();
		for(int i = 0;i < molecules.size();i++)
		{
			ArrayList<IAtom> atoms = new ArrayList<IAtom>();
			for(int j = 0;j < molecules.get(i).getAtomCount();j++)
				atoms.add(molecules.get(i).getAtom(j));
			Collections.sort(atoms,new AtomComparator());
			sortedMolecules.add(atoms);
		}
		return sortedMolecules;
	}
	
	public static ArrayList<Object> makeNumbersToLettersToAtoms(ArrayList<ArrayList<IAtom>> sortedMolecules)
	{
		Map<Character, IAtom> lettersToAtoms = new HashMap<Character, IAtom>();
		Map<Integer, Character> numbersToLetters = new HashMap<Integer, Character>();
		Integer currentNumber = 1;
		char currentLetter = atomLettersArray[currentNumber-1];
		for(int i = 0;i < sortedMolecules.size();i++)
			for(int j = 0;j < sortedMolecules.get(i).size();j++)
			{
				numbersToLetters.put(currentNumber, currentLetter);
				lettersToAtoms.put(currentLetter, sortedMolecules.get(i).get(j));
				currentNumber += 1;
				if(currentNumber >= 63)
					currentLetter = '?';//(char)(currentNumber+65);
				else
					currentLetter = atomLettersArray[currentNumber-1];
			}
		ArrayList<Object> ret = new ArrayList<Object>();
		ret.add(lettersToAtoms);
		ret.add(numbersToLetters);
		return ret;
	}
	
	public static ArrayList<String> makeVariantSide(ArrayList<ArrayList<IAtom>> sortedMolecules, 
	boolean isReactantSide, Map<Integer, Character> numbersToLetters, Map<Integer, Integer> mapping,
	Map<Character, IAtom> lettersToAtoms) throws CDKException
	{

		int currentNumber = 1;
		ArrayList<String> variantSide = new ArrayList<String>();
		for(int i = 0;i < sortedMolecules.size();i++)
		{
			String moleculeString = "";
			for(int j = 0;j < sortedMolecules.get(i).size();j++)
			{
				if(isReactantSide)
				{
					if(!sortedMolecules.get(i).get(j).getSymbol().equals("H"))
						moleculeString += numbersToLetters.get(currentNumber);
				}
				else
				{
					if(!sortedMolecules.get(i).get(j).getSymbol().equals("H"))
						if(sortedMolecules.get(i).get(j).getSymbol().equals(
							lettersToAtoms.get(numbersToLetters.get(mapping.get(currentNumber))).getSymbol()))
							moleculeString += numbersToLetters.get(mapping.get(currentNumber));
						else
							if(mapping.containsKey(currentNumber))
								throw new CDKException("Atom mismatch");
							else
								throw new CDKException("Atom mapping not contained");
				}
				currentNumber += 1;
			}
			if(!moleculeString.equals(""))
				variantSide.add(moleculeString);
		}
		return variantSide;
	}
	
	public static String makeSide(ArrayList<ArrayList<String>> variantSides)
	{
		String side = "";
		for(int i = 0;i < variantSides.size();i++)
		{
			for(int j = 0;j < variantSides.get(0).size();j++)
			{
				//System.out.print(variantSides.get(i).get(j) + " ");
			}
			//System.out.println();
		}
		for(int i = 0;i < variantSides.get(0).size();i++)
		{
			Map<String, Integer> variantsToOccurrences = new HashMap<String, Integer>();
			for(int j = 0;j < variantSides.size();j++)
			{
				System.out.println(j);
				if(!variantsToOccurrences.containsKey(variantSides.get(j).get(i)))
					variantsToOccurrences.put(variantSides.get(j).get(i), 1);
				else
					variantsToOccurrences.put(variantSides.get(j).get(i), 
					variantsToOccurrences.get(variantSides.get(j).get(i))+1);
			}
			Set<String> variants = variantsToOccurrences.keySet();
			Iterator<String> variantsIter = variants.iterator();
			while(variantsIter.hasNext())
			{
				String variant = variantsIter.next();
				side += ((double) variantsToOccurrences.get(variant))/variantSides.size() 
				+ " " + variant;
				if(i!=variantSides.get(0).size()-1 || variantsIter.hasNext())
					side += " + ";
			}
		}
		return side;
	}
	
	public static class MoleculeComparator implements Comparator<IMolecule>{
		public int compare(IMolecule a,IMolecule b)
		{
			int weightA = 0;
			for(int i = 0;i < a.getAtomCount();i++)
				weightA += a.getAtom(i).getAtomicNumber();
			int weightB = 0;
			for(int i = 0;i < b.getAtomCount();i++)
				weightB += b.getAtom(i).getAtomicNumber();
			return weightB - weightA;
		}
	}
	
	public static class AtomComparator implements Comparator<IAtom>{
		public int compare(IAtom a,IAtom b)
		{
			return a.getSymbol().compareTo(b.getSymbol());
		}
	}
	
	public static ArrayList<Object> getFateMaps(String readerLine,int count)
	{
		String[] fields = readerLine.split("\\t");
		String InChI = fields[2];
		String[] reactantsAndProducts = InChI.split(" <-> ");
		String[] reactantsStrings = reactantsAndProducts[0].split("\\)\\+\"\"");
		String[] productsStrings = reactantsAndProducts[1].split("\\)\\+\"\"");
		
		Integer currentNumber = 1;
		char currentLetter = atomLettersArray[currentNumber-1];
		Map<Integer, Character> numbersToLetters = new HashMap<Integer, Character>();
		ArrayList<int[]> reactantsCarbonNumbering = new ArrayList<int[]>();
		for(int i = 0;i < reactantsStrings.length;i++)
		{
			String[] carbonNumberingStrings = reactantsStrings[i].substring(
			reactantsStrings[i].indexOf("\"\"(")+3,reactantsStrings[i].length()-1).split(",");
			int[] carbonNumbering = new int[carbonNumberingStrings.length];
			for(int j = 0;j < carbonNumberingStrings.length;j++)
			{
				carbonNumbering[j] = Integer.parseInt(carbonNumberingStrings[j].
				substring(1,carbonNumberingStrings[j].indexOf("%")));
				numbersToLetters.put(currentNumber, currentLetter);
				currentNumber += 1;
				if(currentNumber >= 63)
					currentLetter = '?';//(char)(currentNumber+65);
				else
					currentLetter = atomLettersArray[currentNumber-1];
			}
			reactantsCarbonNumbering.add(carbonNumbering);
		}
		
		ArrayList<Map<Integer, Integer>> mappings = new ArrayList<Map<Integer, Integer>>();
		ArrayList<int[]> productsCarbonNumbering = new ArrayList<int[]>();
		for(int i = 0;i < productsStrings.length;i++)
		{
			Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
			String[] carbonNumberingStrings = productsStrings[i].substring(
			productsStrings[i].indexOf("\"\"(")+3,productsStrings[i].length()).split(",");
			int[] carbonNumbering = new int[carbonNumberingStrings.length];
			for(int j = 0;j < carbonNumberingStrings.length;j++)
			{
				if(i==productsStrings.length-1 && j==carbonNumberingStrings.length-1)
					carbonNumberingStrings[j] = carbonNumberingStrings[j].substring(0,carbonNumberingStrings[j].length()-2);
				carbonNumbering[j] = Integer.parseInt(carbonNumberingStrings[j].
				substring(1,carbonNumberingStrings[j].indexOf("%")));
				mapping.put(carbonNumbering[j], Integer.parseInt(carbonNumberingStrings[j].substring(
				carbonNumberingStrings[j].indexOf("%")+1)));
			}
			productsCarbonNumbering.add(carbonNumbering);
			mappings.add(mapping);
		}
		
		ArrayList<Object> ret = new ArrayList<Object>();
		ret.add(reactantsCarbonNumbering);
		ret.add(productsCarbonNumbering);
		ret.add(numbersToLetters);
		ret.add(mappings);
		ret.add(fields[0]);
		ret.add(fields[1]);
		ret.add(fields[4]);
		return ret;
	}
	
	public static String makeWriteString(ArrayList<int[]> reactantsCarbonNumbering, ArrayList<int[]> productsCarbonNumbering,
	Map<Integer, Character> numbersToLetters, ArrayList<Map<Integer, Integer>> fateMappings) 
	{
		String writeString = "";
		int reactantCarbonNumber = 0;
		for(int i = 0;i < reactantsCarbonNumbering.size();i++)
		{
			for(int j = 0;j < reactantsCarbonNumbering.get(i).length;j++)
			{
				reactantCarbonNumber += 1;
				writeString += numbersToLetters.get(reactantCarbonNumber);
				if(j==reactantsCarbonNumbering.get(i).length-1)
					if(i==reactantsCarbonNumbering.size()-1)
						writeString += " = ";
					else
						writeString += " + ";
			}
		}
		for(int i = 0;i < productsCarbonNumbering.size();i++)
		{
			for(int j = 0;j < productsCarbonNumbering.get(i).length;j++)
			{
				writeString += numbersToLetters.get(fateMappings.get(i).get(productsCarbonNumbering.get(i)[j]));
				if(j==productsCarbonNumbering.get(i).length-1)
					if(i==productsCarbonNumbering.size()-1)
						;
					else
						writeString += " + ";
			}
		}
		return writeString;
	}
	
	public static ArrayList<String> makeVariantSide2(ArrayList<int[]> reactantsCarbonNumbering, ArrayList<int[]> productsCarbonNumbering,
	Map<Integer, Character> numbersToLetters, ArrayList<Map<Integer, Integer>> fateMappings, boolean isReactant)
	throws CDKException
	{
		ArrayList<String> variantSide = new ArrayList<String>();

		if(isReactant)
		{
			int reactantCarbonNumber = 0;
			for(int i = 0;i < reactantsCarbonNumbering.size();i++)
			{
				String reactantString = "";
				for(int j = 0;j < reactantsCarbonNumbering.get(i).length;j++)
				{
					reactantCarbonNumber += 1;
					reactantString += numbersToLetters.get(reactantCarbonNumber);
				}
				variantSide.add(reactantString);
			}
		}
		else
		{
			for(int i = 0;i < productsCarbonNumbering.size();i++)
			{
				String productString = "";
				for(int j = 0;j < productsCarbonNumbering.get(i).length;j++)
				{
					//System.out.println(fateMappings.get(i).get(productsCarbonNumbering.get(i)[j]));
					productString += numbersToLetters.get(fateMappings.get(i).get(productsCarbonNumbering.get(i)[j]));
				}
				variantSide.add(productString);
			}
		}
		return variantSide;
	}
	
	public static ArrayList<ArrayList<Map<Integer, Integer>>> makeNewAllFateMappings(
	ArrayList<ArrayList<Map<Integer, Integer>>> allFateMappings, ArrayList<String> mets, 
	Map<String, ArrayList<int[]>> metsToHomotopicSets, boolean isReactant, int count)
	throws CDKException
	{	
		ArrayList<ArrayList<Map<Integer, Integer>>> newAllFateMappings = 
		new ArrayList<ArrayList<Map<Integer, Integer>>>();
		if(count == 1507)
			;//System.out.println("HERE");
		for(int i = 0;i < mets.size();i++)
		{
			ArrayList<int[]> homotopicSets = metsToHomotopicSets.get(mets.get(i));
			if(count == 1507 && mets.get(i).equals("C00042"))
				System.out.println("HERE");
			if(homotopicSets==null || homotopicSets.size()==0)
				newAllFateMappings = allFateMappings;
			else
			{
				//newAllFateMappings = new ArrayList<ArrayList<Map<Integer, Integer>>>();
				for(int j = 0;j < homotopicSets.size();j++)
				{
					//System.out.println(allFateMappings.size());
					if(allFateMappings.size()>10000)
						throw new CDKException("Too many mappings");
					newAllFateMappings = new ArrayList<ArrayList<Map<Integer, Integer>>>();
					for(int k = 0;k < allFateMappings.size();k++)
					{
						if(count == 898)// && !isReactant)// && i==0 && j==12 && allFateMappings.size()>10000)
						{
							//System.out.println("HERE");
						System.out.println(isReactant);
						System.out.println(i);
						System.out.println(j);
						System.out.println(k);
						//System.out.println(count);
						}
						ArrayList<Map<Integer, Integer>> fateMappings = allFateMappings.get(k);
						ArrayList<Map<Integer, Integer>> reverseFateMappings =
								new ArrayList<Map<Integer, Integer>>();
						for (int l = 0;l < fateMappings.size();l++)
						{
							Map<Integer, Integer> tempFateMapping = new HashMap<Integer, Integer>();
							for(Integer int1 : fateMappings.get(l).keySet())
								tempFateMapping.put(fateMappings.get(l).get(int1),int1);
							reverseFateMappings.add(tempFateMapping);
						}
						ArrayList<ArrayList<Map<Integer, Integer>>> newFateMappings = 
								new ArrayList<ArrayList<Map<Integer, Integer>>>();

						ArrayList<int[]> permutations = permute(homotopicSets.get(j));
						for(int l = 0;l < permutations.size() ;l++)
						{
							int[] permutation = permutations.get(l);
							ArrayList<Map<Integer, Integer>> tempFateMappings = 
									new ArrayList<Map<Integer, Integer>>();
							for (int m = 0;m < fateMappings.size();m++)
							{
								Map<Integer, Integer> tempFateMapping = new HashMap<Integer, Integer>();
								for(Integer int1 : fateMappings.get(m).keySet())
									tempFateMapping.put(int1,fateMappings.get(m).get(int1));
								for(int n = 0;n < homotopicSets.get(j).length;n++)
									if(isReactant)
									{
										//if(reactantCarbonNumsToMoleculeNums.get(homotopicSets.get(j)[n])==i)
										tempFateMapping.put(
												reverseFateMappings.get(m).get(homotopicSets.get(j)[n]),
												permutation[n]);
									}
									else
									{
										if(i==m)
											tempFateMapping.put(
											homotopicSets.get(j)[n],
											fateMappings.get(m).get(permutation[n]));
											//tempFateMapping.put(
												//reverseFateMappings.get(m).get(permutation[n]),
												//homotopicSets.get(j)[n]);
									}
								tempFateMappings.add(tempFateMapping);
							}
							newFateMappings.add(tempFateMappings);
						}
						newAllFateMappings.addAll(newFateMappings);
					}
					allFateMappings = newAllFateMappings;
					//newAllFateMappings = new ArrayList<ArrayList<Map<Integer, Integer>>>();
				}
			}
		}
		return newAllFateMappings;
	}
	
	public static Map<String, ArrayList<ArrayList<String>>> makeRxnToMets() throws IOException
	{
		Map<String, ArrayList<ArrayList<String>>> rxnsToMets = 
		new HashMap<String, ArrayList<ArrayList<String>>>();
		BufferedReader KEGGReader = new BufferedReader(new FileReader(
		"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\KEGGParse.csv"));
		String KEGGLine = KEGGReader.readLine();
		while(KEGGLine != null)
		{
			String[] fields = KEGGLine.split(",");
			String KEGGID = fields[0].substring(1,fields[0].length()-1);
			if(!KEGGID.equals("0"))
			{
				String[] reactants = fields[1].substring(1,fields[1].length()-1).split(" ");
				String[] products = fields[2].substring(1,fields[2].length()-1).split(" ");
				ArrayList<String> reactantsList = new ArrayList<String>();
				for(int i = 0;i < reactants.length;i++)
					reactantsList.add(reactants[i]);
				ArrayList<String> productsList = new ArrayList<String>();
				for(int i = 0;i < products.length;i++)
					productsList.add(products[i]);
				ArrayList<ArrayList<String>> mets = new ArrayList<ArrayList<String>>();
				mets.add(reactantsList);
				mets.add(productsList);
				rxnsToMets.put(KEGGID, mets);
			}
			KEGGLine = KEGGReader.readLine();
		}
		KEGGReader.close();
		return rxnsToMets;
	}
	
	public static Map<String, ArrayList<int[]>> makeMetsToHomotopicSets() throws IOException
	{
		Map<String, ArrayList<int[]>> metsToHomotopicSets = new HashMap<String, ArrayList<int[]>>();
		BufferedReader readerMets = new BufferedReader(new FileReader(
				"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\database_CarbonFateMapsMets.txt"));
		String metsLine = readerMets.readLine();
		metsLine = readerMets.readLine();
		int count = 0;
		int auxCount = 0;
		while(metsLine != null)
		{
			count++;
			if(metsLine.startsWith("Aux"))
			{
				auxCount++;
				//System.out.println(auxCount);
				metsLine = readerMets.readLine();continue;
			}
			String[] fields = metsLine.split("\\t");
			ArrayList<int[]> homotopicSets = new ArrayList<int[]>();
			if(!fields[3].equals(""))
			{
				String[] CESets = fields[3].substring(1,fields[3].length()-1).split(";");
				String[] prochiralSets = new String[0];
				if(!fields[4].equals(""))
					prochiralSets = fields[4].substring(1,fields[4].length()-1).split(";");
				for(int i = 0;i < CESets.length;i++)
				{
					boolean matchesProchiral = false;
					for(int j = 0;j < prochiralSets.length;j++)
						if(CESets[i].equals(prochiralSets[j]))
							matchesProchiral = true;
					if(!matchesProchiral)
					{
						String[] homotopicSetString = CESets[i].substring
								(1,CESets[i].length()-1).split(",");
						int[] homotopicSet = new int[homotopicSetString.length];
						for(int j = 0;j < homotopicSetString.length;j++)
							homotopicSet[j] = Integer.parseInt(homotopicSetString[j]);
						homotopicSets.add(homotopicSet);
					}
				}
			}
			//strip off all quote signs at beginning of fields[2], seems variable number across metabolites
			while(fields[2].startsWith("\""))
				fields[2] = fields[2].substring(1);
			metsToHomotopicSets.put(fields[2].substring(5,11),homotopicSets);
			metsLine = readerMets.readLine();
		}
		readerMets.close();
		
		return metsToHomotopicSets;
	}

	
	public static int factorial(int n)
	{
		if(n>=1)
			return n*factorial(n-1);
		else
			return 1;
	}
	
	public static ArrayList<int[]> permute(int[] nums)
	{
		ArrayList<int[]> permutations = new ArrayList<int[]>();
		//permutations.add(nums);
		for(int i = 0;i < nums.length;i++)
		{
			ArrayList<int[]> newPermutations = new ArrayList<int[]>();
			if(permutations.size()==0)
			{
				for(int j = 0;j < nums.length;j++)
				{
					int[] tempArray = new int[1]; tempArray[0] = nums[j];
					newPermutations.add(tempArray);
				}
			}
			else
			{
				for(int j = 0;j < permutations.size();j++)
				{
					Set<Integer> availableNums = new HashSet();
					for(int k = 0;k < nums.length;k++)
						availableNums.add(nums[k]);
					for(int k = 0;k < permutations.get(j).length;k++)
					{
						availableNums.remove(permutations.get(j)[k]);
					}
					for(Integer availableNum : availableNums)
					{
						int[] newPermutation = new int[permutations.get(j).length+1];
						for(int k = 0;k < permutations.get(j).length;k++)
							newPermutation[k] = permutations.get(j)[k];
						newPermutation[newPermutation.length-1] = availableNum;
						newPermutations.add(newPermutation);
					}
				}
			}
			permutations = newPermutations;
		}
		return permutations;
	}
}
