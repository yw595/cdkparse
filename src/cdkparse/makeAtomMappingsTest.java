/**
 * 
 */
package cdkparse;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;

import org.openscience.cdk.*;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.nonotify.*;

/**
 * @author Yiping Wang
 *
 */
public class makeAtomMappingsTest {

	public makeAtomMappings MAM;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MAM = new makeAtomMappings();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link cdkparse.makeAtomMappings#getMolecules(java.lang.String, org.openscience.cdk.smiles.SmilesParser)}.
	 */
	@Test
	public void testGetMolecules() {
		String readerLine = "GLYOX3||[CH3:1][CH:2]([OH:3])[C:4]([O-:5])=[O:6].[H+:7]>>"
		+ "[CH3:1][C:2](=[O:3])[CH:4]=[O:6].[OH2:5]||1||4";
		SmilesParser sp = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		ArrayList<Object> ret = null;
		try{
			ret = MAM.getMolecules(readerLine, sp);
		}
		catch(InvalidSmilesException e){
			System.out.println(e);
			fail();
		}
		
		String rxnName = (String) ret.get(0);
		ArrayList<IMolecule> reactantsMolecules = (ArrayList<IMolecule>) ret.get(1);
		ArrayList<IMolecule> productsMolecules = (ArrayList<IMolecule>) ret.get(2);
		assertEquals(rxnName,"GLYOX3");
		assertEquals(reactantsMolecules.get(0).getAtom(0).getSymbol(),"C");
		assertEquals(reactantsMolecules.get(0).getAtom(1).getSymbol(),"C");
		assertEquals(reactantsMolecules.get(0).getAtom(2).getSymbol(),"O");
		assertEquals(reactantsMolecules.get(0).getAtom(3).getSymbol(),"C");
		assertEquals(reactantsMolecules.get(0).getAtom(4).getSymbol(),"O");
		assertEquals(reactantsMolecules.get(0).getAtom(5).getSymbol(),"O");
		assertEquals(reactantsMolecules.get(1).getAtom(0).getSymbol(),"H");
		assertEquals(productsMolecules.get(0).getAtom(0).getSymbol(),"C");
		assertEquals(productsMolecules.get(0).getAtom(1).getSymbol(),"C");
		assertEquals(productsMolecules.get(0).getAtom(2).getSymbol(),"O");
		assertEquals(productsMolecules.get(0).getAtom(3).getSymbol(),"C");
		assertEquals(productsMolecules.get(0).getAtom(4).getSymbol(),"O");
		assertEquals(productsMolecules.get(1).getAtom(0).getSymbol(),"O");
	}

	/**
	 * Test method for {@link cdkparse.makeAtomMappings#getMappings(java.lang.String)}.
	 */
	@Test
	public void testGetMappings1() {
		String rxnName = "FHL";
		ArrayList<Map<Integer, Integer>> mappings = null;
		try{
			mappings = MAM.getMappings(rxnName);
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		assertEquals((int) mappings.get(0).get(1),1);
		assertEquals((int) mappings.get(0).get(2),2);
		assertEquals((int) mappings.get(0).get(3),3);
		assertEquals((int) mappings.get(1).get(1),1);
		assertEquals((int) mappings.get(1).get(2),3);
		assertEquals((int) mappings.get(1).get(3),2);
	}
	
	@Test
	public void testGetMappings2() {
		String rxnName = "Htex";
		ArrayList<Map<Integer, Integer>> mappings = null;
		try{
			mappings = MAM.getMappings(rxnName);
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		assertEquals(mappings.size(),0);
	}
	
	@Test
	public void testGetMappings3() {
		String rxnName = "Zn2tex";
		ArrayList<Map<Integer, Integer>> mappings = null;
		try{
			mappings = MAM.getMappings(rxnName);
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		assertEquals(mappings.size(),1);
		assertEquals(mappings.get(0).size(),1);
		assertEquals((int) mappings.get(0).get(1),1);
	}
	
	@Test
	public void testGetMappings4() {
		String rxnName = "XYLI2";
		ArrayList<Map<Integer, Integer>> mappings = null;
		try{
			mappings = MAM.getMappings(rxnName);
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		assertEquals(mappings.size(),1);
	}

	/**
	 * Test method for {@link cdkparse.makeAtomMappings#sortMolecules(java.util.ArrayList)}.
	 */
	@Test
	public void testSortMolecules() {
		ArrayList<IMolecule> molecules = new ArrayList<IMolecule>();
		IMolecule molecule1 = new Molecule();
		molecule1.addAtom(new Atom("O"));
		molecule1.addAtom(new Atom("O"));
		molecule1.addAtom(new Atom("C"));
		molecules.add(molecule1);
		IMolecule molecule2 = new Molecule();
		molecule2.addAtom(new Atom("H"));
		molecule2.addAtom(new Atom("Br"));
		molecules.add(molecule2);
		ArrayList<ArrayList<IAtom>> sortedMolecules = MAM.sortMolecules(molecules);
		assertEquals(sortedMolecules.get(0).get(0).getSymbol(),"Br");
		assertEquals(sortedMolecules.get(0).get(1).getSymbol(),"H");
		assertEquals(sortedMolecules.get(1).get(0).getSymbol(),"C");
		assertEquals(sortedMolecules.get(1).get(1).getSymbol(),"O");
		assertEquals(sortedMolecules.get(1).get(2).getSymbol(),"O");
	}

	/**
	 * Test method for {@link cdkparse.makeAtomMappings#makeNumbersToLettersToAtoms(java.util.ArrayList)}.
	 */
	@Test
	public void testMakeNumbersToLettersToAtoms() {
		ArrayList<IAtom> CN = new ArrayList<IAtom>();
		CN.add(new Atom("C"));
		CN.add(new Atom("N"));
		ArrayList<IAtom> O = new ArrayList<IAtom>();
		O.add(new Atom("O"));
		ArrayList<IAtom> F = new ArrayList<IAtom>();
		F.add(new Atom("F"));
		ArrayList<ArrayList<IAtom>> sortedMolecules = new ArrayList<ArrayList<IAtom>>();
		sortedMolecules.add(CN);sortedMolecules.add(F);sortedMolecules.add(O);
		ArrayList<Object> ret = MAM.makeNumbersToLettersToAtoms(sortedMolecules);
		Map<Character, IAtom> lettersToAtoms = (Map<Character, IAtom>) ret.get(0);
		Map<Integer, Character> numbersToLetters = (Map<Integer, Character>) ret.get(1);
		assertEquals(lettersToAtoms.get('a').getSymbol(),"C");
		assertEquals(lettersToAtoms.get('b').getSymbol(),"N");
		assertEquals(lettersToAtoms.get('c').getSymbol(),"F");
		assertEquals(lettersToAtoms.get('d').getSymbol(),"O");
		assertEquals(numbersToLetters.get(1),(Character) 'a');
		assertEquals(numbersToLetters.get(2),(Character) 'b');
		assertEquals(numbersToLetters.get(3),(Character) 'c');
		assertEquals(numbersToLetters.get(4),(Character) 'd');
	}

	/**
	 * Test method for {@link 
	 * cdkparse.makeAtomMappings#makeVariantSide(java.util.ArrayList, boolean, java.util.Map, java.util.Map)}.
	 */
	@Test
	public void testMakeVariantSide() {
		ArrayList<IAtom> CN = new ArrayList<IAtom>();
		CN.add(new Atom("C"));
		CN.add(new Atom("N"));
		ArrayList<IAtom> O = new ArrayList<IAtom>();
		O.add(new Atom("O"));
		ArrayList<IAtom> F = new ArrayList<IAtom>();
		F.add(new Atom("F"));
		ArrayList<ArrayList<IAtom>> sortedMolecules = new ArrayList<ArrayList<IAtom>>();
		sortedMolecules.add(CN);sortedMolecules.add(F);sortedMolecules.add(O);
		boolean isReactantSide = true;
		Map<Integer, Character> numbersToLetters = new HashMap<Integer, Character>();
		numbersToLetters.put(1, 'a');numbersToLetters.put(2, 'b');
		numbersToLetters.put(3, 'c');numbersToLetters.put(4, 'd');
		Map<Character, IAtom> lettersToAtoms = new HashMap<Character, IAtom>();
		lettersToAtoms.put('a', new Atom("C"));lettersToAtoms.put('b', new Atom("N"));
		lettersToAtoms.put('c', new Atom("O"));lettersToAtoms.put('d', new Atom("F"));
		ArrayList<String> variantSide = null;
		try{
			variantSide = MAM.makeVariantSide(sortedMolecules,true,numbersToLetters,null,null);
		}
		catch(CDKException c){
			System.out.println(c);
			fail();
		}
		assertEquals(variantSide.get(0),"ab");assertEquals(variantSide.get(1),"c");
		assertEquals(variantSide.get(2),"d");
		
		ArrayList<IAtom> C = new ArrayList<IAtom>();
		C.add(new Atom("C"));
		ArrayList<IAtom> NO = new ArrayList<IAtom>();
		NO.add(new Atom("N"));
		NO.add(new Atom("O"));
		sortedMolecules = new ArrayList<ArrayList<IAtom>>();
		sortedMolecules.add(NO);sortedMolecules.add(C);sortedMolecules.add(F);
		isReactantSide = false;
		Map<Integer, Integer> mapping = new HashMap<Integer, Integer>();
		mapping.put(1,2);mapping.put(2,3);mapping.put(3,1);mapping.put(4,4);
		try{
			variantSide = MAM.makeVariantSide(sortedMolecules,false,numbersToLetters,mapping,lettersToAtoms);
		}
		catch(CDKException c){
			System.out.println(c);
			fail();
		}
		assertEquals(variantSide.get(0),"bc");assertEquals(variantSide.get(1),"a");
		assertEquals(variantSide.get(2),"d");
		
		mapping = new HashMap<Integer, Integer>();
		mapping.put(1,3);mapping.put(2,2);mapping.put(3,1);mapping.put(4,4);
		try{
			variantSide = MAM.makeVariantSide(sortedMolecules,false,numbersToLetters,mapping,lettersToAtoms);
			fail();
		}
		catch(CDKException c){
			System.out.println(c);
		}
		
		//FHL unmapped hydrogens test
		ArrayList<IAtom> COO = new ArrayList<IAtom>();
		COO.add(new Atom("C"));COO.add(new Atom("O"));COO.add(new Atom("O"));
		ArrayList<IAtom> H = new ArrayList<IAtom>();
		H.add(new Atom("H"));
		sortedMolecules = new ArrayList<ArrayList<IAtom>>();
		sortedMolecules.add(COO);sortedMolecules.add(H);
		numbersToLetters = new HashMap<Integer, Character>();
		numbersToLetters.put(1, 'a');numbersToLetters.put(2, 'b');
		numbersToLetters.put(3, 'c');numbersToLetters.put(4, 'd');
		numbersToLetters.put(5, 'e');
		lettersToAtoms = new HashMap<Character, IAtom>();
		lettersToAtoms.put('a', new Atom("C"));lettersToAtoms.put('b', new Atom("O"));
		lettersToAtoms.put('c', new Atom("O"));lettersToAtoms.put('d', new Atom("H"));
		lettersToAtoms.put('e', new Atom("H"));
		mapping = new HashMap<Integer, Integer>();
		mapping.put(1,1);mapping.put(2,2);mapping.put(3,3);
		try{
			variantSide = MAM.makeVariantSide(sortedMolecules,false,numbersToLetters,mapping,lettersToAtoms);
		}
		catch(CDKException c){
			System.out.println(c);
			fail();
		}
		assertEquals(variantSide.size(),1);
		
		try{
			variantSide = MAM.makeVariantSide(sortedMolecules,true,numbersToLetters,null,null);
		}
		catch(CDKException c){
			System.out.println(c);
			fail();
		}
		assertEquals(variantSide.size(),1);
		assertEquals(variantSide.get(0),"abc");
	}

	/**
	 * Test method for {@link cdkparse.makeAtomMappings#makeSide(java.util.ArrayList)}.
	 */
	@Test
	public void testMakeSide() {
		ArrayList<ArrayList<String>> variantSides = new ArrayList<ArrayList<String>>();
		ArrayList<String> variantSide = new ArrayList<String>();
		variantSide.add("ab");variantSide.add("cd");
		variantSides.add(variantSide);
		variantSide = new ArrayList<String>();
		variantSide.add("ab");variantSide.add("dc");
		variantSides.add(variantSide);
		variantSide = new ArrayList<String>();
		variantSide.add("ba");variantSide.add("cd");
		variantSides.add(variantSide);
		variantSide = new ArrayList<String>();
		variantSide.add("ba");variantSide.add("dc");
		variantSides.add(variantSide);
		assertEquals(MAM.makeSide(variantSides),"0.5 ba + 0.5 ab + 0.5 dc + 0.5 cd");
	}

	@Test
	public void testFateMapMethods1(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(
			"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\database_CarbonFateMapsRxns.txt"));
			String readerLine = reader.readLine();
			readerLine = reader.readLine();
			ArrayList<Object> ret = MAM.getFateMaps(readerLine,0);
			ArrayList<int[]> reactantsCarbonNumbering = (ArrayList<int[]>) ret.get(0);
			ArrayList<int[]> productsCarbonNumbering = (ArrayList<int[]>) ret.get(1);
			Map<Integer, Character> numbersToLetters = (HashMap<Integer, Character>) ret.get(2);
			ArrayList<Map<Integer, Integer>> fateMappings = (ArrayList<Map<Integer, Integer>>) ret.get(3);

			String writeString = MAM.makeWriteString(reactantsCarbonNumbering, productsCarbonNumbering,
			numbersToLetters,fateMappings);
			
			assertEquals(writeString, "abcdef = ebf + dac");
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testFateMapMethods2(){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(
			"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\database_CarbonFateMapsRxns.txt"));
			String readerLine = reader.readLine();
			readerLine = reader.readLine();
			readerLine = reader.readLine();
			readerLine = reader.readLine();
			readerLine = reader.readLine();
			readerLine = reader.readLine();
			ArrayList<Object> ret = MAM.getFateMaps(readerLine,0);
			ArrayList<int[]> reactantsCarbonNumbering = (ArrayList<int[]>) ret.get(0);
			ArrayList<int[]> productsCarbonNumbering = (ArrayList<int[]>) ret.get(1);
			Map<Integer, Character> numbersToLetters = (HashMap<Integer, Character>) ret.get(2);
			ArrayList<Map<Integer, Integer>> fateMappings = (ArrayList<Map<Integer, Integer>>) ret.get(3);

			String writeString = MAM.makeWriteString(reactantsCarbonNumbering, productsCarbonNumbering,
			numbersToLetters,fateMappings);
			
			assertEquals(writeString, "abcdef = abcfde");
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
	}
	
	@Test
	public void testMakeNewAllFateMappings(){
		ArrayList<ArrayList<Map<Integer, Integer>>> allFateMappings = 
		new ArrayList<ArrayList<Map<Integer, Integer>>>();
		ArrayList<Map<Integer, Integer>> fateMappings = new ArrayList<Map<Integer, Integer>>();
		Map<Integer, Integer> fateMapping = new HashMap<Integer, Integer>();
		fateMapping.put(1, 1);fateMapping.put(2, 2);
		fateMappings.add(fateMapping);allFateMappings.add(fateMappings);
		
		Map<String, ArrayList<ArrayList<String>>> rxnsToMets = 
		new HashMap<String, ArrayList<ArrayList<String>>>();
		ArrayList<String> reactants = new ArrayList<String>();reactants.add("A");
		ArrayList<String> products = new ArrayList<String>();products.add("B");
		ArrayList<ArrayList<String>> mets = new ArrayList<ArrayList<String>>();
		mets.add(reactants);mets.add(products);rxnsToMets.put("rxn", mets);
		reactants = rxnsToMets.get("rxn").get(0);
		products = rxnsToMets.get("rxn").get(1);
				
		Map<String, ArrayList<int[]>> metsToHomotopicSets = new HashMap<String, ArrayList<int[]>>();
		int[] homotopicSetA = new int[2]; homotopicSetA[0]=1; homotopicSetA[1]=2;
		ArrayList<int[]> tempList = new ArrayList<int[]>();tempList.add(homotopicSetA);metsToHomotopicSets.put("A",tempList);
		int[] homotopicSetB = new int[2]; homotopicSetB[0]=1; homotopicSetB[1]=2;
		tempList = new ArrayList<int[]>();tempList.add(homotopicSetB);metsToHomotopicSets.put("B",tempList);
		
		ArrayList<ArrayList<Map<Integer, Integer>>> newAllFateMappings = null;
		try{
		newAllFateMappings = MAM.makeNewAllFateMappings(
		allFateMappings,reactants,metsToHomotopicSets,true,1);
		}
		catch(CDKException c){
		System.out.println(c);
		fail();
		}
		allFateMappings = newAllFateMappings;
		//System.out.println(allFateMappings.size());
		assertEquals(allFateMappings.size(),2);
		assertEquals(allFateMappings.get(0).size(),1);
		assertEquals(allFateMappings.get(1).size(),1);
		assertEquals(allFateMappings.get(0).get(0).get(1),(Integer) 1);
		assertEquals(allFateMappings.get(0).get(0).get(2),(Integer) 2);
		assertEquals(allFateMappings.get(1).get(0).get(1),(Integer) 2);
		assertEquals(allFateMappings.get(1).get(0).get(2),(Integer) 1);
		
		try{
		newAllFateMappings = MAM.makeNewAllFateMappings(
		allFateMappings,products,metsToHomotopicSets,false,1);
		}
		catch(CDKException c){
		System.out.println(c);
		fail();
		}
		allFateMappings = newAllFateMappings;
		assertEquals(allFateMappings.size(),4);
		assertEquals(allFateMappings.get(0).size(),1);
		assertEquals(allFateMappings.get(1).size(),1);
		assertEquals(allFateMappings.get(0).get(0).get(1),(Integer) 1);
		assertEquals(allFateMappings.get(0).get(0).get(2),(Integer) 2);
		assertEquals(allFateMappings.get(1).get(0).get(1),(Integer) 2);
		assertEquals(allFateMappings.get(1).get(0).get(2),(Integer) 1);
		assertEquals(allFateMappings.get(2).get(0).get(1),(Integer) 2);
		assertEquals(allFateMappings.get(2).get(0).get(2),(Integer) 1);
		assertEquals(allFateMappings.get(3).get(0).get(1),(Integer) 1);
		assertEquals(allFateMappings.get(3).get(0).get(2),(Integer) 2);
	}
	
	@Test
	public void testMakeVariantSide2() {
		ArrayList<int[]> reactantsCarbonNumbering = new ArrayList<int[]>();
		int[] tempArray = new int[2];tempArray[0] = 1;tempArray[1] = 2;
		reactantsCarbonNumbering.add(tempArray);
		tempArray = new int[1];tempArray[0] = 1;
		reactantsCarbonNumbering.add(tempArray);
		tempArray = new int[1];tempArray[0] = 1;
		reactantsCarbonNumbering.add(tempArray);
		
		ArrayList<int[]> productsCarbonNumbering = new ArrayList<int[]>();
		tempArray = new int[2];tempArray[0] = 1;tempArray[1] = 2;
		productsCarbonNumbering.add(tempArray);
		tempArray = new int[1];tempArray[0] = 1;
		productsCarbonNumbering.add(tempArray);
		tempArray = new int[1];tempArray[0] = 1;
		productsCarbonNumbering.add(tempArray);
		
		Map<Integer, Character> numbersToLetters = new HashMap<Integer, Character>();		
		numbersToLetters.put(1, 'a');numbersToLetters.put(2, 'b');
		numbersToLetters.put(3, 'c');numbersToLetters.put(4, 'd');
		ArrayList<Map<Integer, Integer>> fateMappings = new ArrayList<Map<Integer, Integer>>();
		Map<Integer, Integer> tempMapping = new HashMap<Integer, Integer>();
		tempMapping.put(1,2);tempMapping.put(2,3);fateMappings.add(tempMapping);
		tempMapping = new HashMap<Integer, Integer>();
		tempMapping.put(1, 1);fateMappings.add(tempMapping);
		tempMapping = new HashMap<Integer, Integer>();
		tempMapping.put(1,4);fateMappings.add(tempMapping);
		
		ArrayList<String> variantReactantSide = null;
		try{
			variantReactantSide = MAM.makeVariantSide2(reactantsCarbonNumbering, productsCarbonNumbering,
			numbersToLetters,null,true);
		}
		catch(CDKException c){
			System.out.println(c);
			fail();
		}
		assertEquals(variantReactantSide.size(),3);
		assertEquals(variantReactantSide.get(0),"ab");
		assertEquals(variantReactantSide.get(1),"c");
		assertEquals(variantReactantSide.get(2),"d");
		
		ArrayList<String> variantProductSide = null;
		try{
			variantProductSide = MAM.makeVariantSide2(reactantsCarbonNumbering, productsCarbonNumbering,
			numbersToLetters,fateMappings,false);
		}
		catch(CDKException c){
			System.out.println(c);
			fail();
		}
		assertEquals(variantProductSide.size(),3);
		assertEquals(variantProductSide.get(0),"bc");
		assertEquals(variantProductSide.get(1),"a");
		assertEquals(variantProductSide.get(2),"d");
	}
	
	@Test
	public void testMakeRxnToMets()
	{
		Map<String, ArrayList<ArrayList<String>>> rxnToMets = null;
		try{
		rxnToMets = MAM.makeRxnToMets();
		}
		catch(IOException e)
		{
			System.out.println(e);
			fail();
		}
		
		Iterator<String> rxnToMetsKeysIt = rxnToMets.keySet().iterator();
		while(rxnToMetsKeysIt.hasNext())
		{
			String rxn = rxnToMetsKeysIt.next();
			assertEquals(rxn.contains("\""),false);
			assertEquals(rxn.contains("\'"),false);
			assertEquals(rxn.equals("0"),false);
			assertEquals(rxn.startsWith("R"),true);
			
			ArrayList<String> reactants = rxnToMets.get(rxn).get(0);
			ArrayList<String> products = rxnToMets.get(rxn).get(1);
			for(String reactant : reactants)
			{
				assertEquals(reactant.contains("\""),false);
				assertEquals(reactant.contains("\'"),false);
				assertEquals(reactant.equals("0"),false);
				assertEquals(reactant.startsWith("C"),true);
			}
			for(String product : products)
			{
				assertEquals(product.contains("\""),false);
				assertEquals(product.contains("\'"),false);
				assertEquals(product.equals("0"),false);
				assertEquals(product.startsWith("C"),true);
			}
		}
	}
	
	@Test
	public void testMakeMetsToHomotopicSets()
	{
		Map<String, ArrayList<int[]>> metsToHomotopicSets = null;
		try{
			metsToHomotopicSets = MAM.makeMetsToHomotopicSets();
		}
		catch(IOException e)
		{
			System.out.println(e);
			fail();
		}
		
		assertEquals(metsToHomotopicSets.keySet().size(),3653);
		assertEquals(metsToHomotopicSets.get("C00002").size(),0);
		assertEquals(metsToHomotopicSets.get("C00079").size(),2);
		assertEquals(metsToHomotopicSets.get("C00079").get(0).length,2);
		assertEquals(metsToHomotopicSets.get("C00079").get(1).length,2);
		assertEquals(metsToHomotopicSets.get("C14725").size(),0);
	}
	
	@Test
	public void testGlycolysis()
	{
		BufferedReader reader = null;
		Map<String, String> KEGGIDsToMappings = new HashMap<String, String>();
		try{
		reader = new BufferedReader(new FileReader(
		"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\MFA\\atomMappings3.csv"));
		String readerLine = reader.readLine();
		while(readerLine != null)
		{
			String[] fields = readerLine.split(",");
			if(fields.length==4)
				KEGGIDsToMappings.put(fields[1],fields[3]);
			readerLine = reader.readLine();
		}
		reader.close();
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		//2.7.1.1 hexokinase
		assertEquals(KEGGIDsToMappings.get("R02848"),
		"1.0 abcdefghij + 1.0 klmnop = 1.0 abcdefghij + 1.0 klmnop");
		//5.3.1.9 phosphoglucoisomerase
		assertEquals(KEGGIDsToMappings.get("R00771"),"1.0 abcdef = 1.0 afbcde");
		//2.7.1.11 phosphofructokinase
		assertEquals(KEGGIDsToMappings.get("R00756"),"1.0 abcdefghij + 1.0 klmnop = 1.0 abcdefghij + 1.0 klmnop");
		//4.1.2.13 fructose bisphosphate aldolase
		assertEquals(KEGGIDsToMappings.get("R01068"),"1.0 abcdef = 1.0 ebf + 1.0 dac");
		//5.3.1.1 triose phosphate isomerase
		assertEquals(KEGGIDsToMappings.get("R01015"),"1.0 abc = 1.0 abc");
		//1.2.1.12 glyceraldehyde phosphate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R01061"),
		"1.0 abc + 1.0 defghijklmnopqrstuvwx = 1.0 bca + 1.0 defghijklmnopqrstuvwx");
		//2.7.2.3 phosphoglycerate kinase
		assertEquals(KEGGIDsToMappings.get("R01512"),
		"1.0 abcdefghij + 1.0 klm = 1.0 abcdefghij + 1.0 klm");
		//5.4.2.1 phosphoglycerate mutase
		assertEquals(KEGGIDsToMappings.get("R01662"),"1.0 abc = 1.0 abc");
		//4.2.1.11 enolase
		assertEquals(KEGGIDsToMappings.get("R00658"),"1.0 abc = 1.0 abc");
		//2.7.1.40 pyruvate kinase
		assertEquals(KEGGIDsToMappings.get("R00200"),"1.0 abcdefghij + 1.0 klm = 1.0 abcdefghij + 1.0 klm");
		//1.2.4.1 pyruvate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R00209"),"1.0 abc + 1.0 defghijklmnopqrstuvwx + 1.0 yzABCDEFGHIJKLMNOPQRS = "
		+ "1.0 adefghijklmbnopqrstuvwx + 1.0 c + 1.0 yzABCDEFGHIJKLMNOPQRS");
	}
	
	public void testPentosePhosphate()
	{
		BufferedReader reader = null;
		Map<String, String> KEGGIDsToMappings = new HashMap<String, String>();
		try{
		reader = new BufferedReader(new FileReader(
				"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\Locasale Lab\\atomMappings3.csv"));
		String readerLine = reader.readLine();
		while(readerLine != null)
		{
			String[] fields = readerLine.split(",");
			if(fields.length==4)
				KEGGIDsToMappings.put(fields[1],fields[3]);
			readerLine = reader.readLine();
		}
		reader.close();
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		//1.1.1.44 Glucose-6-phosphate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R01528"),
		"1.0 abcdef + 1.0 ghijklmnopqrstuvwxyzA = 1.0 eadbc + 1.0 f + 1.0 ghijklmnopqrstuvwxyzA");
		//2.2.1.1 Transketolase 1
		assertEquals(KEGGIDsToMappings.get("R01641"),"1.0 abcdefg + 1.0 hij = 1.0 bdfge + 1.0 aicjh");
		//2.2.1.2 Transaldolase
		assertEquals(KEGGIDsToMappings.get("R01827"),"1.0 abcdefg + 1.0 hij = 1.0 gbfd + 1.0 iajhec");
		//2.2.1.1 Transketolase 2
		assertEquals(KEGGIDsToMappings.get("R01067"),"1.0 abcdef + 1.0 ghi = 1.0 eadc + 1.0 bhfig");
	}
	
	public void testTCA()
	{
		BufferedReader reader = null;
		Map<String, String> KEGGIDsToMappings = new HashMap<String, String>();
		try{
		reader = new BufferedReader(new FileReader(
				"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\Locasale Lab\\atomMappings3.csv"));
		String readerLine = reader.readLine();
		while(readerLine != null)
		{
			String[] fields = readerLine.split(",");
			if(fields.length==4)
				KEGGIDsToMappings.put(fields[1],fields[3]);
			readerLine = reader.readLine();
		}
		reader.close();
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		//1.3.99.1 succinate dehydrogenase, need to fix 1.3.5.1, which still scrambles FADH
		assertEquals(KEGGIDsToMappings.get("R00408"),
				"1.0 abcd + 1.0 efghijklmnopqrstuvwxyzABCDE = 1.0 efghijklmnopqrstuvwxyzABCDE + "
				+ "0.25 badc + 0.25 abdc + 0.25 abcd + 0.25 bacd");
		//4.2.1.2 fumarase
		assertEquals(KEGGIDsToMappings.get("R01082"),
				"1.0 abcd = 0.25 badc + 0.25 abcd + 0.25 abdc + 0.25 bacd");
		//1.1.1.37 malate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R00342"),
				"1.0 abcd + 1.0 efghijklmnopqrstuvwxy = 1.0 abcd + 1.0 efghijklmnopqrstuvwxy");
		//2.3.3.8, because 4.1.3.8 was transferred to that
		assertEquals(KEGGIDsToMappings.get("R00352"),
		"1.0 abcdefghij + 1.0 klmnop + 1.0 qrstuvwxyzABCDEFGHIJK = "
		+ "1.0 abcdefghij + 1.0 lqrstuvwxyznABCDEFGHIJK + 1.0 kpmo");
		//both 4.2.1.3, either citrate to isocitrate, or citrate to cis-aconitate
		assertEquals(KEGGIDsToMappings.get("R01324"),
				"1.0 abcdef = 1.0 bfdaec");
		assertEquals(KEGGIDsToMappings.get("R01325"),
				"1.0 abcdef = 1.0 bafdce");
		//1.1.1.41 isocitrate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R00709"),
				"1.0 abcdef + 1.0 ghijklmnopqrstuvwxyzA = 1.0 badcf + 1.0 e + 1.0 ghijklmnopqrstuvwxyzA");
		//1.2.4.2 alpha-ketoglutarate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R00621"),
				"1.0 abcde + 1.0 fghijklmnopq = 1.0 fgabhijkmnocpdql + 1.0 e");
		//6.2.1.4 Succinyl-CoA synthetase, appears wrong, we have hit GDP with rearrangement instead of succinyl-CoA
		assertEquals(KEGGIDsToMappings.get("R00432"),
		"1.0 abcdefghij + 1.0 klmn + 1.0 opqrstuvwxyzABCDEFGHI = 0.25 abcdefghij + "
		+ "0.25 badcefghij + 0.25 abdcefghij + 0.25 bacdefghij + 1.0 polkqrstuvwxyznmABCDEFGHI");
		//1.1.1.39 Malic enzyme
		assertEquals(KEGGIDsToMappings.get("R00214"),
		"1.0 abcd + 1.0 efghijklmnopqrstuvwxy = 1.0 abd + 1.0 c + 1.0 efghijklmnopqrstuvwxy");
	}
	
	public void testSerineLactate()
	{
		BufferedReader reader = null;
		Map<String, String> KEGGIDsToMappings = new HashMap<String, String>();
		try{
		reader = new BufferedReader(new FileReader(
				"C:\\Users\\Yiping Wang\\Documents\\Lab Work\\Locasale Lab\\atomMappings3.csv"));
		String readerLine = reader.readLine();
		while(readerLine != null)
		{
			String[] fields = readerLine.split(",");
			if(fields.length==4)
				KEGGIDsToMappings.put(fields[1],fields[3]);
			readerLine = reader.readLine();
		}
		reader.close();
		}
		catch(IOException e){
			System.out.println(e);
			fail();
		}
		//1.1.1.27 lactate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R00703"),
		"1.0 abc + 1.0 defghijklmnopqrstuvwx = 1.0 abc + 1.0 defghijklmnopqrstuvwx");
		//1.1.1.95 phosphoglycerate dehydrogenase
		assertEquals(KEGGIDsToMappings.get("R01513"),
		"1.0 abc + 1.0 defghijklmnopqrstuvwx = 1.0 abc + 1.0 defghijklmnopqrstuvwx");
		//2.1.2.1 serine hydroxymethyltransferase, methylenetetrahydrofolate has symmetric 1,2 and 3,4 carbons
		assertEquals(KEGGIDsToMappings.get("R00945"),
		"1.0 abcdefghijklmnopqrst + 1.0 uv = 0.25 badcefhgjklmnopqrst + 0.25 abcdefhgjklmnopqrst + "
		+ "0.25 abdcefhgjklmnopqrst + 0.25 bacdefhgjklmnopqrst + 1.0 iuv");
	}
}
