package shocid.utilities;

import java.util.*;
import java.lang.*;
import java.net.*;

public class GetClassDirectory
{
  public void main(String args[]) {
  URL classesRootDir = getClass().getResource(".");


  System.out.println(classesRootDir);
  }
}
