import visitor.GJDepthFirst;
import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

class LLWriter extends GJDepthFirst<String,ScopeType> {


    private final STDataStructure STD;
    private final PrintWriter pw;

    private int current_temp;


    public STDataStructure GetSTD() {
            return STD;
         }


    public LLWriter(STDataStructure newSTD, PrintWriter fo)
    {
        STD=newSTD;
        pw=fo;
    }



    /** Goal
     * Grammar production:
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */

    public String visit(Goal n, ScopeType st){

        STD.WriteV_TablesToFile(pw);


        ScopeType main_st=STD.GetMainVariables();
        pw.println("define i32 @main(){");

        n.f0.accept(this,main_st);
        //n.f1.accept(this,null);

        pw.println("}");
        pw.close();


        return null;
    }


    /**VarDecl
     * Grammar production:
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */

    public String visit(VarDeclaration n,ScopeType st){
        String type,llvm_type,id,llvm_id;

        type =n.f0.accept(this,null);
        id=n.f1.accept(this,null);
        llvm_id="%"+id;

        llvm_type= ScopeType.GetLlvmType(type);
        st.InsertVariable(id,type);

        pw.println("    "+llvm_id+" = alloca "+llvm_type);
        pw.println();
        return null;



    }


    public String visit(IntegerType n, ScopeType st)
    {

        return n.f0.tokenImage;
    }

    public String visit(BooleanType n, ScopeType st)
    {

        return n.f0.tokenImage;
    }

    public String visit(ArrayType n, ScopeType st)
    {
        return (n.f0.tokenImage+n.f1.tokenImage+n.f2.tokenImage);
    }





    public String visit(Identifier n,ScopeType st) { return n.f0.tokenImage;}

    public String visit(IntegerLiteral n,ScopeType st) { return "int"; }

    public String visit(TrueLiteral n,ScopeType st){return "boolean";}

    public String visit(FalseLiteral n,ScopeType st){return "boolean";}































}
