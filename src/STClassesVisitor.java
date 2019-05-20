import syntaxtree.*;
import visitor.*;


class STClassesVisitor extends GJDepthFirst<String,ScopeType>{

    private final STDataStructure STD=new STDataStructure();

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

    /////////////////////////////////////////////////////////////////////////

    public String visit(MainClass n,ScopeType st)
    {
        String id;

        id=n.f1.accept(this,null);

        STD.InsertClass(id);
        return null;
    }


    ////////////////////////////////////////////////////////////////////////////
    /**TypeDeclaration
     * Grammar production:
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */

    public String visit(TypeDeclaration n,ScopeType st)
    {

        return n.f0.accept(this,null);

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
        ClassType ct;

        id=n.f1.accept(this,null);
        STD.InsertClass(id);

        ct=STD.GetClass(id);

        n.f3.accept(this,ct);
        n.f4.accept(this,ct);




        return null;

    }

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


        ST.InsertVariable(id,type);
        return null;
    }

    /**
     * Grammar production:
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */

    public String visit(MethodDeclaration n, ScopeType st)
    {
        String type;
        String id;

        ClassType ct;
        ct=(ClassType)st;


        type=n.f1.accept(this,null);
        id=n.f2.accept(this,null);

        MethodType MT=new MethodType(id,type,ct);
        ct.InsertMethod(MT,STD);



        n.f4.accept(this,MT);
        n.f7.accept(this,MT);




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

        String id,base_id;

        id=n.f1.accept(this,null);
        base_id=n.f3.accept(this,null);

        STD.InsertClass(id);

        ClassType ct,base_ct;

        ct=STD.GetClass(id);

        base_ct=STD.GetClass(base_id);
        ct.SetBaseClass(base_ct);
        n.f5.accept(this,ct);
        n.f6.accept(this,ct);


        return null;

    }

    //////////////////////////////////////////////////////////////////////

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


        mt.InsertVariable(id,type);
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






    /**
     * Grammar production:
     * f0 -> <IDENTIFIER>
     */

    public String visit(Identifier n, ScopeType st)
    {

       return n.f0.tokenImage;


    }
















}



