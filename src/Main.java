import syntaxtree.*;
import minijava.*;

import java.io.*;

import static java.lang.System.exit;

class Main {



	static private PrintWriter GetLLFile(String javaFileName){
		PrintWriter writer=null;

		String[] fn = javaFileName.split("\\.");


		try {
			writer = new PrintWriter("./"+fn[fn.length-2]+".ll");

		}
		catch(FileNotFoundException ex) {
			System.err.println(ex.getMessage());
			System.exit(0);
		}

		return writer;

	}



    public static void main (String [] args){

    FileInputStream fis = null;




    if(args.length==0)							//check parameters
	{
		System.out.println("Usage: java Main [file1] [file1] [file2] [file3]..");
		exit(0);
	}
	try{
		for (String arg : args) {
			fis = new FileInputStream(arg);


			MiniJavaParser parser = new MiniJavaParser(fis);

			STClassesVisitor STCV = new STClassesVisitor();
			Goal root = parser.Goal();                        //check syntax

			root.accept(STCV, null);                        //check class declaration

			PrintWriter llfile = GetLLFile(arg);

			LLWriter TCV = new LLWriter(STCV.GetSTD(), llfile);        //check statements and assignments
			root.accept(TCV, null);
		}

	}
	catch(ParseException ex){
	    System.out.println(ex.getMessage());
	}
	catch(FileNotFoundException ex){
	    System.err.println(ex.getMessage());
	}
	finally{
	    try{
		if(fis != null) fis.close();
	    }
	    catch(IOException ex){
		System.err.println(ex.getMessage());
	    }
	}
    }
}
