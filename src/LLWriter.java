import visitor.GJDepthFirst;
import syntaxtree.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

class LLWriter extends GJDepthFirst<String,ScopeType> {


    private final STDataStructure STD;
    private final PrintWriter pw;

    private int current_temp;
    private int current_label;

    private String NewTemp()
    {
        String temp;

        temp="%_"+current_temp;
        current_temp++;

        return temp;
    }

    private String NewLabel(){

        String l;

        l="Label"+current_label;
        current_label++;


        return l;
    }


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
        current_label=0;

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
        String tmp1,tmp2,res;

        tmp1=n.f0.accept(this,st);
        tmp2=n.f2.accept(this,st);


        res=NewTemp();
        pw.println("    "+res+" = add i32 "+tmp1+", "+tmp2);

        return res;

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

        String id,type,llvm_type,res;

        id=n.f0.accept(this,st);

        if(id.startsWith("%"))
        {
            return id;
        }
        if(id.equals("true")){
            return "1";
        }
        if(id.equals("false")){
            return "0";
        }
        if(IsInteger(id)){
            return id;
        }


        type=st.GetType(id);

        llvm_type=ScopeType.GetLlvmType(type);

        res=NewTemp();

        pw.println("    "+res+" = load "+llvm_type+", "+llvm_type+"* "+"%"+id);

        return res;


    }

    public String visit(MinusExpression n,ScopeType st){
        String tmp1,tmp2,res;

        tmp1=n.f0.accept(this,st);
        tmp2=n.f2.accept(this,st);

        res=NewTemp();
        pw.println("    "+res+" = sub i32 "+tmp1+", "+tmp2);


        return res;


    }

    public String visit(ArrayAssignmentStatement n,ScopeType st){
        String id;

        id=n.f0.accept(this,st);

        return null;


    }

    public String visit(ArrayLength n,ScopeType st){
        String exp,res;

        exp=n.f0.accept(this,st);

        res=NewTemp();

        pw.println("    "+res+" = load i32, i32* "+exp);

        return res;

    }

    public String visit(ArrayLookup n,ScopeType st){

        String arr_var,offset_var,resp,res,arr_sz,element,bool,r_offset,label1,label2;

        arr_var=n.f0.accept(this,st);
        offset_var=n.f2.accept(this,st);



        arr_sz=NewTemp();
        pw.println("    "+arr_sz+" = "+"load i32, i32* "+arr_var);


        r_offset=NewTemp();
        pw.println("    "+r_offset+" = "+"add i32 "+offset_var+", 1");

        bool=NewTemp();
        pw.println("    "+bool+" = icmp ult i32 "+r_offset+", "+arr_sz);



        label1=NewLabel();

        label2=NewLabel();

        pw.println("    "+"br i1 "+bool+", label "+"%"+label2+", label "+"%"+label1);
        pw.println();
        pw.println(label1+":");
        pw.println("    "+"call void @throw_oob()");
        pw.println("    "+"br label "+"%"+label2);
        pw.println();
        pw.println(label2+":");

        resp=NewTemp();
        pw.println("    "+resp+" = getelementptr i32, i32* "+arr_var+", i32 "+r_offset);

        res=NewTemp();
        pw.println("    "+res+" = load i32, i32* "+resp);

        return res;






    }

    public String visit(ArrayAllocationExpression n,ScopeType st){

        String exp,allocsize,label1,label2,allocvoid,allocint,intpointer,bool;

        exp=n.f3.accept(this,st);

        bool=NewTemp();
        pw.println("    "+bool+" = icmp slt i32 "+exp+", 0");

        label1=NewLabel();

        label2=NewLabel();


        pw.println("    "+"br i1 "+bool+", label "+"%"+label1+", label "+"%"+label2);
        pw.println();
        pw.println(label1+":");
        pw.println("    "+"call void @throw_oob()");
        pw.println("    "+"br label "+"%"+label2);
        pw.println();
        pw.println(label2+":");



        allocsize=NewTemp();
        pw.println("    "+allocsize+" = add i32 "+exp+", "+1);


        allocvoid=NewTemp();
        pw.println("    "+allocvoid+" = call i8* @calloc(i32 4, i32 "+allocsize+")");

        allocint=NewTemp();
        pw.println("    "+allocint+" = bitcast i8* "+allocvoid+" to i32*");

        pw.println("    "+"store i32 "+allocsize+", i32* "+allocint);
        return allocint;


    }

    public String visit(Clause n,ScopeType st){

        return n.f0.accept(this,st);

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
