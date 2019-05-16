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
        current_temp=0;

        n.f0.accept(this,main_st);
        //n.f1.accept(this,null);
        pw.println("\n    ret i32 0");
        pw.println("}");
        pw.close();


        return null;
    }


    /**Statement
     * Grammar production:
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */

    public String visit(Statement n,ScopeType st) {

        n.f0.accept(this,st);
        return null;

    }

    public String visit(AssignmentStatement n,ScopeType st) {

        String type,id,llvm_id,llvm_type,tmp;


        id=n.f0.accept(this,st);
        type=st.GetType(id);
        llvm_type=ScopeType.GetLlvmType(type);

        tmp=n.f2.accept(this,st);

        pw.println("    "+"store "+llvm_type+" "+tmp+", "+llvm_type+"* "+"%"+id);
        return null;

    }

    public String visit(Expression n,ScopeType st) {

        String tmpvar;

        tmpvar=n.f0.accept(this,st);

        return tmpvar;

    }


    public String visit(PlusExpression n,ScopeType st) {
        String tmp1,tmp2;

        tmp1=n.f0.accept(this,st);
        tmp2=n.f2.accept(this,st);

        pw.println("    "+"%_"+current_temp+" = add i32 "+tmp1+", "+tmp2);

        return "%_"+current_temp;

    }


    private boolean IsInteger(String input)
    {
        try
        {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    public String visit(PrimaryExpression n,ScopeType st){

        String id,type,llvm_type;

        id=n.f0.accept(this,st);

        if(id.equals("true")){
            llvm_type="i1";
            return "1";
        }
        if(id.equals("false")){
            llvm_type="i1";
            return "0";
        }
        if(IsInteger(id)){
            return id;
        }


        type=st.GetType(id);

        llvm_type=ScopeType.GetLlvmType(type);

        pw.println("    "+"%_"+current_temp+" = load "+llvm_type+", "+llvm_type+"* "+"%"+id);
        current_temp++;
        return "%_"+(current_temp-1);


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

    public String visit(IntegerLiteral n,ScopeType st) { return n.f0.tokenImage; }

    public String visit(TrueLiteral n,ScopeType st){return n.f0.tokenImage;}

    public String visit(FalseLiteral n,ScopeType st){return n.f0.tokenImage;}































}
