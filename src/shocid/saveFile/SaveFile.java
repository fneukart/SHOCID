package shocid.saveFile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class SaveFile {

  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.open();
    FileDialog dialog = new FileDialog(shell, SWT.SAVE);
    dialog.setFilterNames(new String[] { "Neural Network Files", "All Files (*.*)" });
    dialog.setFilterExtensions(new String[] { "*.net", "*.*" }); // Windows

    dialog.setFilterPath("c:\\"); // Windows path
    dialog.setFileName("ANN.net");
    System.out.println("Save to: " + dialog.open());
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}