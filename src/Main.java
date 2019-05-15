import syntaxtree.*;
import minijava.*;

import java.io.*;

import static java.lang.System.exit;

class Main {



	static private PrintWriter GetLLFile(String javaFileName){
		PrintWriter writer=null;

		String[] fn = javaFileName.split("\\.");


		try {
			writer = new PrintWriter("../outfiles/"+fn[0]+".ll");
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
		for(int i=0;i<args.length;i++)						//for every file
		{
			fis = new FileInputStream(args[i]);


			MiniJavaParser parser = new   MiniJavaParser(fis);

			STClassesVisitor STCV = new STClassesVisitor();
			Goal root = parser.Goal();						//check syntax

			root.accept(STCV, null);						//check class declaration

			PrintWriter llfile=GetLLFile(args[i]);

			LLWriter TCV=new LLWriter(STCV.GetSTD(),llfile);		//check statements and assignments
			root.accept(TCV, null);


//			STPVariablesDeclVisitor STPCTV=new STPVariablesDeclVisitor(STCV.GetSTD());
//			root.accept(STPCTV, null);				//check variables declartion
//			if(STPCTV.GetSTD().getErrorFlag())
//			{
//				System.out.println("////////////////////////////////////////////"+"\n");
//				continue;
//			}
//
//
//			LLWriter TCV=new LLWriter(STCV.GetSTD());		//check statements and assignments
//			root.accept(TCV, null);
//
//			if(TCV.GetSTD().getErrorFlag())
//			{
//				System.out.println("////////////////////////////////////////////"+"\n");
//				continue;
//			}
//			else{
//				System.err.println("Program evaluated successfully.");
//				TCV.GetSTD().PrintOffsets();								//Print classes offsets
//			}
//
//			System.out.println("////////////////////////////////////////////"+"\n");
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
