package cdkparse;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.layout.*;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.renderer.font.*;
import org.openscience.cdk.renderer.generators.*;
import org.openscience.cdk.renderer.visitor.*;
import org.openscience.cdk.templates.*;

//import javax.imageio.*;

public class test9 {
	public static void main(String[] args){
	int WIDTH = 600;
	int HEIGHT = 600;

	// the draw area and the image should be the same size
	Rectangle drawArea = new Rectangle(WIDTH, HEIGHT);
	Image image = new BufferedImage(
	  WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB
	);

	IMolecule triazole = MoleculeFactory.make123Triazole();
	StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	sdg.setMolecule(triazole);
	try {
		sdg.generateCoordinates();
	} catch (CDKException e) {
		// TODO Auto-generated catch block
		System.out.println("HERE");
		e.printStackTrace();
	}
	triazole = sdg.getMolecule();

	// generators make the image elements
	List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
	generators.add(new BasicSceneGenerator());
	generators.add(new BasicBondGenerator());
	generators.add(new BasicAtomGenerator());

	// the renderer needs to have a toolkit-specific font manager
	AtomContainerRenderer renderer =
	  new AtomContainerRenderer(generators, new AWTFontManager());

	// the call to 'setup' only needs to be done on the first paint
	renderer.setup(triazole, drawArea);

	// paint the background
	Graphics2D g2 = (Graphics2D)image.getGraphics();
	g2.setColor(Color.WHITE);
	g2.fillRect(0, 0, WIDTH, HEIGHT);

	// the paint method also needs a toolkit-specific renderer
	renderer.paint(triazole, new AWTDrawVisitor(g2));
	
	try {
		ImageIO.write((RenderedImage)image, "PNG", new File("triazole.png"));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
