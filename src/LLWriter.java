import visitor.GJDepthFirst;
import syntaxtree.*;

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

        pw.println("\n    ret i32 0");
        pw.println("}");
        pw.println();
        n.f1.accept(this,null);
        pw.close();


        return null;
    }

    public String visit(ClassDeclaration n,ScopeType st){
        String id;
        ClassType ct;

        id=n.f1.accept(this,null);
        ct=STD.GetClass(id);


        n.f4.accept(this,ct);
        return null;


    }

    public String visit(ClassExtendsDeclaration n,ScopeType st){
        String c_name;
        ClassType c;

        c_name=n.f1.accept(this,st);
        c=STD.GetClass(c_name);

        n.f6.accept(this,c);
        return null;


    }

    private  void AllocParameters(String types_params){
        String[] t_p,temp;
        String type,param;
        int i;

        t_p=types_params.split(",");

        for(i=0;i<t_p.length;i++){

            temp=t_p[i].split(" ");
            type=temp[0];
            param=temp[1];

            pw.println("    "+"%"+param+" = alloca "+type);
            pw.println("    "+"store "+type+"%."+param+", "+type+"* "+"%"+param);

        }

    }


    public String visit(MethodDeclaration n,ScopeType st){
        String id,type,classname,llvm_type,exp,types_params;
        ClassType ct;
        MethodType mt;

        current_label=0;
        current_temp=0;

        type=n.f1.accept(this,st);
        llvm_type=ScopeType.GetLlvmType(type);

        id=n.f2.accept(this,st);

        ct=(ClassType)st;

        classname=ct.GetName();
        mt=ct.GetMethod(id);

        pw.print("define "+llvm_type+" @"+classname+"."+id);
        pw.print("(i8* %this");
        types_params=n.f4.accept(this,mt);

        pw.println(") {");
        if(types_params!=null){
            AllocParameters(types_params);
        }


        n.f7.accept(this,mt);
        n.f8.accept(this,mt);
        exp=n.f10.accept(this,mt);
        pw.println("\n    ret "+llvm_type+" "+exp);

        pw.println("}");


        return null;



    }

    public String visit(FormalParameterList n,ScopeType st){

        String types_params;

        types_params=n.f0.accept(this,st);
        types_params=types_params+n.f1.accept(this,st);


        return types_params;

    }

    public String visit(FormalParameterTail n,ScopeType st){

        int i;
        String params_types="";
        for(i=0;i<n.f0.size();i++){

            params_types=params_types+","+n.f0.elementAt(i).accept(this,st);
        }
        return params_types;

    }

    public String visit(FormalParameter n,ScopeType st){
        String id,type,llvm_type,type_param;

        type=n.f0.accept(this,st);
        llvm_type=ScopeType.GetLlvmType(type);

        id=n.f1.accept(this,st);

        type_param=llvm_type+" "+id;
        pw.print(", "+llvm_type+" %."+id);


        return type_param;

    }

    public String visit(FormalParameterTerm n,ScopeType st){


        return n.f1.accept(this,st);

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
    private String AccessThisField(String clas,String type,String id){
        ClassType ct;
        int offset;
        String temp1,temp2;


        ct=STD.GetClass(clas);
        offset=ct.getVariableOffset(id);

        temp1=NewTemp();
        temp2=NewTemp();

        pw.println("    "+temp1+" = getelementptr i8, i8* %this, i32 "+offset);
        pw.println("    "+temp2+" = bitcast i8* "+temp1+" to "+type+"*");

        return temp2;

    }

    public String visit(ThisExpression n,ScopeType st){

        return "%"+n.f0.tokenImage;

    }

    public String visit(AssignmentStatement n,ScopeType st) {

        String type,id,llvm_type,tmp1,tmp2;


        id=n.f0.accept(this,st);

        type=st.GetType(id);



        if(type.indexOf('.')>0){

            String[] c_t=type.split("\\.");

            llvm_type=ScopeType.GetLlvmType(c_t[1]);

            tmp1=n.f2.accept(this,st);

            tmp2=AccessThisField(c_t[0],llvm_type,id);

            pw.println("    "+"store "+llvm_type+" "+tmp1+", "+llvm_type+"* "+tmp2);

        }
        else{

            llvm_type=ScopeType.GetLlvmType(type);
            tmp1=n.f2.accept(this,st);
            pw.println("    "+"store "+llvm_type+" "+tmp1+", "+llvm_type+"* "+"%"+id);

        }



        return null;

    }

    public String visit(Expression n,ScopeType st) {

        String tmpvar;

        tmpvar=n.f0.accept(this,st);

        return tmpvar;

    }

    private static String MergeParameters(String types,String var){
        String[] types_arr,var_arr;
        int i;
        String params="";

        types_arr=types.split(",");
        var_arr=var.split(" ");

        for(i=1;i<types_arr.length;){
                params=params+", "+types_arr[i]+" "+var_arr[i-1];
                i++;

        }

        return params;

    }

    public String visit(MessageSend n,ScopeType st){

        String id,exp,meth_param_types,meth_param_exp,params;

        String  temp1,temp2,temp3,temp4,temp5,temp6,llvm_type;
        MethodType meth;

        int offset;

        exp=n.f0.accept(this,st);
        id=n.f2.accept(this,st);

        temp1=NewTemp();
        pw.println("    "+temp1+" = bitcast i8* "+exp+" to i8***");

        temp2=NewTemp();
        pw.println("    "+temp2+" = load i8**, i8*** "+temp1);

        temp3=NewTemp();
        meth=STD.getMethod(id);

        offset=meth.getOffset();
        pw.println("    "+temp3+" = getelementptr i8*, i8** "+temp2+", i32 "+offset);

        temp4=NewTemp();
        pw.println("    "+temp4+" = load i8*, i8** "+temp3);

        temp5=NewTemp();
        meth_param_types=meth.Get_parametersIR();
        llvm_type=ScopeType.GetLlvmType(meth.GetType());

        pw.println("    "+temp5+" = bitcast i8* "+temp4+" to "+llvm_type+" (i8*"+meth_param_types+")"+"*");

        meth_param_exp= n.f4.accept(this,st);

        if(meth_param_exp==null || meth_param_types==null){
            params="";
        }else{
            params=LLWriter.MergeParameters(meth_param_types,meth_param_exp);
        }


        temp6=NewTemp();

        pw.println("    "+temp6+" = call "+llvm_type+" "+temp5+"(i8* "+exp+params+")");

        return temp6;


    }

    public String visit(ExpressionList n,ScopeType st){

        String exp;

        exp=n.f0.accept(this,st);

        exp=exp+n.f1.accept(this,st);

        return exp;


    }

    public String visit(ExpressionTail n,ScopeType st){

        String all_exp="";
        int i;

        for(i=0;i<n.f0.size();i++){
            all_exp=all_exp+" "+n.f0.elementAt(i).accept(this,st);
        }

        return all_exp;

    }

    public String visit(ExpressionTerm n,ScopeType st){

        return n.f1.accept(this,st);
    }



    public String visit(WhileStatement n,ScopeType st){

        String bool,label1,label2,label3;

        label1=NewLabel();
        label2=NewLabel();
        label3=NewLabel();


        pw.println("    br label "+"%"+label1);
        pw.println();
        pw.println(label1+":");


        bool=n.f2.accept(this,st);



        pw.println("    "+"br i1 "+bool+", label "+"%"+label2+", label "+"%"+label3);
        pw.println();
        pw.println(label2+":");
        n.f4.accept(this,st);
        pw.println("    br label "+"%"+label1);
        pw.println(label3+":");

        return null;


    }

    public String visit(IfStatement n,ScopeType st){
        String bool,label1,label2,label3;

        bool=n.f2.accept(this,st);

        label1=NewLabel();
        label2=NewLabel();
        label3=NewLabel();

        pw.println("    "+"br i1 "+bool+", label "+"%"+label1+", label "+"%"+label2);
        pw.println();

        pw.println(label1+":");
        n.f4.accept(this,st);
        pw.println("    "+"br label"+"%"+label3);

        pw.println();

        pw.println(label2+":");
        n.f6.accept(this,st);
        pw.println("    "+"br label"+"%"+label3);

        pw.println();
        pw.println(label3+":");


        return null;



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

    public String visit(AllocationExpression n,ScopeType st){

        String id,temp1,temp2,temp3;
        ClassType ct;

        int var_offset,method_num;
        int alloc;

        id=n.f1.accept(this,st);
        ct=STD.GetClass(id);


        var_offset=ct.GetVariablesOffset();
        alloc=var_offset;

        temp1=NewTemp();
        pw.println("    "+temp1+" = call i8* @calloc(i32 1, i32 "+alloc+")");

        temp2=NewTemp();
        pw.println("    "+temp2+" = bitcast i8* "+temp1+" to i8***");

        temp3=NewTemp();

        method_num=ct.getMethods().size();
        pw.println("    "+temp3+" = getelementptr ["+method_num+" x i8*], ["+method_num+" x i8*]* "+"@."+ct.GetName()+"_vtable, i32 0, i32 0");
        pw.println("    "+"store i8** "+temp3+", i8*** "+temp2);

        return temp1;



    }

    public String visit(BracketExpression n,ScopeType st){
        return n.f1.accept(this,st);

    }

    public String visit(PrimaryExpression n,ScopeType st){

        String id,type,llvm_type,res,tmp1;

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

        res=NewTemp();

        if(type.indexOf('.')>0){

            String[] c_t=type.split("\\.");

            llvm_type=ScopeType.GetLlvmType(c_t[1]);

            tmp1=AccessThisField(c_t[0],llvm_type,id);

            pw.println("    "+res+" = load "+llvm_type+", "+llvm_type+"* "+tmp1);

        }
        else{

            llvm_type=ScopeType.GetLlvmType(type);
            pw.println("    "+res+" = load "+llvm_type+", "+llvm_type+"* "+"%"+id);

        }

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

    public String visit(TimesExpression n,ScopeType st){
        String tmp1,tmp2,res;

        tmp1=n.f0.accept(this,st);
        tmp2=n.f2.accept(this,st);

        res=NewTemp();
        pw.println("    "+res+" = mul i32 "+tmp1+", "+tmp2);


        return res;


    }




    private void ArrayOObCheck(String offset,String limit){

        String bool1,label1,label2;

        bool1=NewTemp();
        if(offset.equals("0")){
            pw.println("    "+bool1+" = icmp sle i32 "+offset+", "+limit);
        }
        else{
            pw.println("    "+bool1+" = icmp slt i32 "+offset+", "+limit);
        }



        label1=NewLabel();
        label2=NewLabel();

        pw.println("    "+"br i1 "+bool1+", label "+"%"+label2+", label "+"%"+label1);
        pw.println();
        pw.println(label1+":");
        pw.println("    "+"call void @throw_oob()");
        pw.println("    "+"br label "+"%"+label2);
        pw.println();
        pw.println(label2+":");

    }

    public String visit(ArrayAssignmentStatement n,ScopeType st){

        String id,size,offset,ptr,res,arr,new_offset,type,tmp1;

        id=n.f0.accept(this,st);
        type=st.GetType(id);

        arr=NewTemp();

        if(type.indexOf('.')>0){

            String[] c_t=type.split("\\.");

            tmp1=AccessThisField(c_t[0],"i32*",id);

            pw.println("    "+arr+" = load i32*, i32** "+tmp1);

        }
        else{

            pw.println("    "+arr+" = load i32*, i32** "+"%"+id);
        }


        size=NewTemp();
        pw.println("    "+size+" = load i32, i32* "+arr);

        offset=n.f2.accept(this,st);

        ArrayOObCheck(offset,size);
        ArrayOObCheck("0",offset);

        new_offset=NewTemp();
        pw.println("    "+new_offset+" = add i32 "+offset+", 1");

        ptr=NewTemp();
        pw.println("    "+ptr+" = getelementptr i32, i32* "+arr+", i32 "+new_offset);

        res=n.f5.accept(this,st);
        pw.println("    "+"store i32 "+res+", i32* "+ptr);

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

        String arr_var,offset_var,resp,res,arr_sz,r_offset;

        arr_var=n.f0.accept(this,st);
        offset_var=n.f2.accept(this,st);



        arr_sz=NewTemp();
        pw.println("    "+arr_sz+" = "+"load i32, i32* "+arr_var);


        ArrayOObCheck(offset_var,arr_sz);
        ArrayOObCheck("0",offset_var);



        r_offset=NewTemp();
        pw.println("    "+r_offset+" = "+"add i32 "+offset_var+", 1");

        resp=NewTemp();
        pw.println("    "+resp+" = getelementptr i32, i32* "+arr_var+", i32 "+r_offset);

        res=NewTemp();
        pw.println("    "+res+" = load i32, i32* "+resp);

        return res;



    }

    public String visit(ArrayAllocationExpression n,ScopeType st){

        String exp,allocsize,allocvoid,allocint;

        exp=n.f3.accept(this,st);




        ArrayOObCheck("0",exp);



        allocsize=NewTemp();
        pw.println("    "+allocsize+" = add i32 "+exp+", "+1);


        allocvoid=NewTemp();
        pw.println("    "+allocvoid+" = call i8* @calloc(i32 4, i32 "+allocsize+")");

        allocint=NewTemp();
        pw.println("    "+allocint+" = bitcast i8* "+allocvoid+" to i32*");

        pw.println("    "+"store i32 "+allocsize+", i32* "+allocint);
        return allocint;


    }

    public String visit(PrintStatement n,ScopeType st){

        String exp;

        exp=n.f2.accept(this,st);

        pw.println("    "+"call void (i32) @print_int(i32 "+exp+")");

        return null;



    }

    public String visit(CompareExpression n,ScopeType st){
        String exp1,exp2,res;

        exp1=n.f0.accept(this,st);
        exp2=n.f2.accept(this,st);

        res=NewTemp();
        pw.println("    "+res+" = icmp slt i32 "+exp1+", "+exp2);
        return res;

    }

    public String visit(NotExpression n,ScopeType st){

        String c,bool;
        c= n.f1.accept(this,st);

        bool=NewTemp();
        pw.println("    "+bool+" = icmp eq i1 "+c+", 0");

        return bool;

    }

    public String visit(AndExpression n,ScopeType st){

        String c1,c2,res;

        c1=n.f0.accept(this,st);
        c2=n.f2.accept(this,st);

        res=NewTemp();

        pw.println("    "+res+" = and i1 "+c1+", "+c2);
        return res;


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
