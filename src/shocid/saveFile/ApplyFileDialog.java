package shocid.saveFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import org.encog.neural.networks.BasicNetwork;
import org.encog.util.obj.SerializeObject;

public class ApplyFileDialog {



	public void saveAgent
	(BasicNetwork agent) {
		FileDialog fd = new FileDialog(new Frame(), "Save...", FileDialog.SAVE);
		
		fd.setFile("geneticAgent.net");
		fd.setDirectory(".\\");
		fd.setLocation(50, 50);
		try {
			File saveFile = new File(fd.getDirectory() + System.getProperty("file.separator")+fd.getFile());
			SerializeObject.save(saveFile,agent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		return fd.getDirectory() + 
//		System.getProperty("file.separator") + fd.getFile();
	}


//	public String loadFile
//	(Frame f, String title, String defDir, String filePath) {
//		FileDialog fd = new FileDialog(f, title, FileDialog.LOAD);
//		fd.setFile(filePath);
//		fd.setDirectory(defDir);
//		fd.setLocation(50, 50);
//		return fd.getDirectory() + 
//		System.getProperty("file.separator") + fd.getFile();
//	}

	//  public static void loadAndSave() {
	//	  ApplyFileDialog ufd = new ApplyFileDialog();
	//    System.out.println
	//      ("Loading : " 
	//          + ufd.loadFile(new Frame(), "Open...", ".\\", "*.txt"));
	//    System.out.println
	//      ("Saving : " 
	//          + ufd.saveFile(new Frame(), "Save...", ".\\", "*.txt"));
	//    System.exit(0);
	//    }
}
