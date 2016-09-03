package shocid.utilities;
import java.io.File;
 
public class WorkspaceExample {
 
    public static void main(String[] args) {
        File f = new File("");
        String p = f.getAbsolutePath();
        System.out.println(p);
        p = p.substring(0, p.lastIndexOf(System.getProperty("file.separator")));
        System.out.println(p);
    }
}