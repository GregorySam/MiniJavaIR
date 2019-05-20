import syntaxtree.*;
import visitor.GJDepthFirst;

class STPVariablesDeclVisitor extends GJDepthFirst<String,ScopeType> {

    private final STDataStructure STD;




    public STPVariablesDeclVisitor(STDataStructure newSTD)
    {
        STD=newSTD;
    }

    public STDataStructure GetSTD()
    {
        return STD;
    }

    /** Goal
     * Grammar production:
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */

    public String visit(Goal n, ScopeType st){


        n.f0.accept(this,null);

        n.f1.accept(this,null);


        return null;

    }

    /**MainClass
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */

    public String visit(MainClass n, ScopeType st)
    {
        n.f14.accept(this,STD.GetMainVariables());
        return null;

    }
    //////////////////////////////////////////////////////////////////////////////

    /** VarDeclaration
     * Grammar production:
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */

    public String visit(VarDeclaration n, ScopeType ST)
    {
        String type;
        String id;


        type=n.f0.accept(this,null);
        id=n.f1.accept(this,null);

        if(!type.equals("int") && !type.equals("int[]") && !type.equals("boolean"))
        {
            if(!STD.FindClass(type))
            {
                System.out.println("Unkown type: "+type+" in "+ST.GetScopeName());
               STD.SetErrorFlag(true);
            }
        }


//        if(!ST.InsertVariable(id,type))
//        {
//            System.out.println("Identifier "+id+" already declared in "+ST.GetScopeName());
//            STD.SetErrorFlag(true);
//
//        }
        return null;
    }
    ///////////////////////////////////////////////////////////////////



    /** Type
     * Grammar production:
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */


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


    ////////////////////////////////////////////////////////////////////////////
    /**TypeDeclaration
     * Grammar production:
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */

    public String visit(TypeDeclaration n,ScopeType st)
    {

        n.f0.accept(this,null);
        return null;
    }
    ////////////////////////////////////////////////////////////////////////////



    /**ClassDeclaration
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */

    public String visit(ClassDeclaration n,ScopeType st)
    {
        String id;
        id=n.f1.accept(this,null);

        ClassType ct;
        ct=STD.GetClass(id);


        n.f3.accept(this,ct);
        n.f4.accept(this,ct);

        return null;
    }

    public String visit(MethodDeclaration n,ScopeType st)
    {
        String type;
        String id;

        ClassType ct;
        ct=(ClassType)st;


        type=n.f1.accept(this,null);
        id=n.f2.accept(this,null);

        MethodType MT=new MethodType(id,type,ct);

        if(!type.equals("int") && !type.equals("int[]")  && !type.equals("boolean"))
        {
            if(!STD.FindClass(type))
            {
                System.out.println("Undefined Method type "+type+" at "+MT.GetScopeName());
                STD.SetErrorFlag(true);


            }
        }


        n.f4.accept(this,MT);
        n.f7.accept(this,MT);

        //ct.InsertMethod(MT);



        return null;

    }
/////////////////////////////////////////////////////////////////

    /**ClassExtendsDeclaration
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */

    public String visit(ClassExtendsDeclaration n,ScopeType st)
    {

        String id;
        id=n.f1.accept(this,null);


        ClassType ct;

        ct=STD.GetClass(id);



        n.f5.accept(this,ct);
        n.f6.accept(this,ct);

        return null;

    }

    //////////////////////////////////////////////////////////////////////




    ///////////////////////////////////////////////////////////////////
    /**FormalParameterList
     * Grammar production:
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */

    public String visit(FormalParameterList n,ScopeType st)
    {

        MethodType mt=(MethodType)st;


        n.f0.accept(this,mt);
        n.f1.accept(this,mt);
        return null;

    }

    public String visit(FormalParameter n,ScopeType st)
    {
        String id,type;


        MethodType mt=(MethodType)st;

        type=n.f0.accept(this,null);
        id=n.f1.accept(this,null);


//        if(!mt.InsertVariable(id,type))
//        {
//            System.out.println("Identifier "+id+" already declared in "+st.GetScopeName());
//            STD.SetErrorFlag(true);
//
//        }
        mt.ChangeId(type);

        return null;
    }

    public String visit(FormalParameterTail n,ScopeType st)
    {
        MethodType mt=(MethodType)st;


        n.f0.accept(this,mt);
        return null;
    }

    public String visit(FormalParameterTerm n,ScopeType st)
    {
        MethodType mt=(MethodType)st;

        n.f1.accept(this,mt);
        return null;
    }


    ////////////////////////////////////////////////////////////////



    /**
     * Grammar production:
     * f0 -> <IDENTIFIER>
     */

    public String visit(Identifier n, ScopeType st)
    {

        return n.f0.tokenImage;


    }











}
