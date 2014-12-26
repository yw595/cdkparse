package cdkparse;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.*;

import javax.imageio.*;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.renderer.font.*;
import org.openscience.cdk.renderer.generators.*;
import org.openscience.cdk.renderer.visitor.*;
import org.openscience.cdk.templates.*;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.nonotify.*;

public class test {
	public static void main(String[] args)
	{
		SmilesParser sp = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
		ArrayList<String> smilesStrings = new ArrayList<String>();
		//String smiles = "[H]c2c([H])c(c1c(nc(n1([H]))C(F)(F)F)c2Cl)Cl";
		//String smiles = "[OH2:1].[OH2:2].[O:3]=[O:4]";
		//String smiles = "[OH2].[OH2].[O]=[O].[O]=[O].[O]=[O]";
		//String smiles = "[O]=[C]=[O].[H][H]";
		//String smiles = "[O]=[C]=[O].[H][H].[H+].[O-][CH]=[O]";
		//String smiles = "[OH][CH]1[CH2][O][CH]([OH])[CH]([OH])[CH]1[OH]";
		//String smiles = "C";
		smilesStrings.add("[O]=[C]=[O].[H][H].[H+].[O-][CH]=[O]");
		smilesStrings.add("[H]c2c([H])c(c1c(nc(n1([H]))C(F)(F)F)c2Cl)Cl");
		smilesStrings.add("[OH][CH]1[CH2][O][CH]([OH])[CH]([OH])[CH]1[OH]");
	    try {
	    	for(int k=0;k<smilesStrings.size();k++)
	    	{
	    		String smiles = smilesStrings.get(k);
	    		System.out.println(smiles);
			IMolecule mol = sp.parseSmiles(smiles);
			StructureDiagramGenerator sdg = new StructureDiagramGenerator();
			System.out.println(mol.getAtomCount());
			for(int i=0;i<mol.getAtomCount();i++)
			{
				System.out.println(mol.getAtom(i));
			}
			//System.out.println(mol.getAtom(5).getID());
			//System.out.println(mol.getAtom(6).getID());
			//System.out.println(mol.getAtom(7).getID());
			//System.out.println(mol.getAtom(8).getID());
	    	}
		} catch (InvalidSmilesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
