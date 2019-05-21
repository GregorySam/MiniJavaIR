
import syntaxtree.MethodDeclaration;

import java.io.PrintWriter;
import java.util.*;



class ScopeType
{

    public static  String GetLlvmType(String type)
    {
        if(type.equals("boolean"))
        {
            return "i1";
        }
        else if(type.equals("int"))
        {
            return "i32";
        }
        else if(type.equals("int[]"))
        {
            return "i32*";
        }
        else
        {
            return "i8*";
        }
    }

    final LinkedHashMap<String, String> Variables;
    private final String scopename;


    public void InsertVariable(String id, String p)              //Insert viaribale if dows not exist
    {
        Variables.put(id,p);
    }

    public String GetScopeName(){               //get class-method name
        return scopename;
    }


    public ScopeType(String name)
    {
        this.scopename=name;
        Variables =new LinkedHashMap<>();
    }

    public String GetType(String id)            //return type of identifier
    {
       return Variables.get(id);
    }



}

class MethodType extends ScopeType
{
    private final String name;
    private String id;
    private final String type;
    private final List<String> ParametersTypes;
    private final ClassType ClassPertain;
    private String parameters_ir;
    private int offset;





    public MethodType(String name,String type,ClassType CT)
    {
        super("class "+CT.GetName()+" "+name);
        this.name=name;
        this.type=type;
        this.id=type+name;
        this.ParametersTypes=new ArrayList<>();
        this.ClassPertain=CT;

    }

    @Override
    public String GetType(String id)                        //get type of variable if type not found search in class and bas class
    {
        if(Variables.get(id)==null)
        {

            return ClassPertain.GetName()+"."+ClassPertain.GetType(id);

        }
        else
        {
            String str_type;

            str_type=Variables.get(id);

            return str_type;
        }
    }

    public int getOffset(){
        return offset;
    }


    public void setOffset(int offset){

        this.offset=offset;
    }

    public void ChangeId(String a)                  //id of function
    {
        id=id+a;
        ParametersTypes.add(a);
    }

    public String GetName()
    {
        return name;
    }

    public String GetType()
    {
        return type;
    }


    public String GetId()
    {
        return id;
    }

    public ClassType getClassPertain()
    {
        return ClassPertain;
    }



    public void PrintV_Table(PrintWriter pw){

        String type=GetLlvmType(this.type);
        String parameters="";


        for (String par : ParametersTypes) {
            parameters=parameters+","+GetLlvmType(par);
        }

        this.parameters_ir=parameters;
        pw.print("i8* bitcast ("+type+" (i8*"+parameters+")* @"+ClassPertain.GetName()+"."+this.name+" to "+"i8*)");

    }

    public String Get_parametersIR(){
        return parameters_ir;
    }


}

class ClassType extends ScopeType
{
    private final String name;
    private ClassType BaseClass;
    private LinkedHashMap<String, MethodType> Methods;

    private int var_offset;
    private LinkedHashMap<String ,Integer> VariablesOffsets;

    private int methods_offset;


    public LinkedHashMap<String, MethodType> getMethods()
    {
        return Methods;
    }

    public ClassType(String n)
    {
        super("class "+n);
        name=n;
        Methods=new LinkedHashMap<>();
        VariablesOffsets=new LinkedHashMap<>();
        BaseClass=null;
        methods_offset=-1;
        var_offset=8;
    }
    public boolean IsTypeOf(String id)
    {
        if(id.equals(name))
        {
            return true;
        }

        if(BaseClass==null)
        {
            return false;
        }
        return BaseClass.IsTypeOf(id);

    }

    static private int GetSize(String type)         //get offset size of types
    {
        if(type.equals("int"))
        {
           return 4;
        }
        else if(type.equals("int[]"))
        {
            return 8;
        }
        else if(type.equals("boolean")){
            return 1;
        }
        else
        {
            return 8;
        }
    }

    public int GetVariablesOffset()
    {
        return var_offset;
    }

    public int GetMethodsOffset()
    {
        return methods_offset;
    }




    public void InsertMethod(MethodType MT,STDataStructure std)      //inseret method in map
    {


        if(Methods.containsKey(MT.GetName())){
            MT.setOffset(std.getMethod(MT.GetName()).getOffset());
            Methods.put(MT.GetName(),MT);
            return;
        }

        methods_offset=methods_offset+1;

        MT.setOffset(methods_offset);
        std.AddMethod(MT.GetName(),MT);

        Methods.put(MT.GetName(),MT);


    }

    public String GetName(){return name;}

    public boolean FindMethod(String id)
    {
        return  Methods.get(id)!=null;
    }


    public void SetBaseClass(ClassType id)
    {
        BaseClass=id;
        var_offset=BaseClass.GetVariablesOffset();
        methods_offset=BaseClass.GetMethodsOffset();
        Methods=new LinkedHashMap<>(BaseClass.getMethods());

    }

    public MethodType GetMethod(String id) {            //search for method in curent class or base class

        if(Methods.get(id)==null)
        {
            if(BaseClass==null)
            {
                return null;
            }
            else
            {
                return BaseClass.GetMethod(id);
            }
        }

        return Methods.get(id);
    }
    @Override
    public String GetType(String id)                    //get type of identifier
    {
        if(Variables.get(id)==null)
        {
            if(BaseClass==null)
            {
                return null;
            }
            else
            {
                return BaseClass.GetType(id);
            }
        }

        return Variables.get(id);
    }

    public int getVariableOffset(String id){

        if(VariablesOffsets.get(id)==null)
        {
            if(BaseClass==null)
            {
                return -1;
            }
            else
            {
                return BaseClass.getVariableOffset(id);
            }
        }

        return VariablesOffsets.get(id);
    }



    @Override
    public void InsertVariable(String id,String type)
    {
        Variables.put(id,type);
        VariablesOffsets.put(id,var_offset);
        var_offset=var_offset+GetSize(type);

    }

    public void PrintV_Table(PrintWriter pw)
    {
        int i;

        if(Methods.size()==0)
        {
            pw.println("[0 x i8*][]");
            return;
        }

        pw.print("["+Methods.size()+" x i8*] [");
        i=1;
        for (Map.Entry<String, MethodType> entry : Methods.entrySet()) {
            String key = entry.getKey();
            MethodType mt=entry.getValue();

            mt.PrintV_Table(pw);

            if(i!=Methods.size()){
                pw.print(", ");
            }
            i++;

        }
        pw.println("]");
    }

}


public class STDataStructure {

    private final ScopeType MainVariables;
    private final LinkedHashMap<String,ClassType> Classes;
    private  HashMap<String,MethodType> Methods;
    private boolean error_flag;


    public STDataStructure(){
        MainVariables=new ScopeType("Main");
        error_flag=false;
        Classes=new LinkedHashMap<String,ClassType>();
        Methods=new HashMap<>();
    }

    public boolean getErrorFlag(){
        return error_flag;
    }



    public ScopeType GetMainVariables()
    {
        return MainVariables;
    }

    public void SetErrorFlag(boolean f)
    {
        error_flag=f;
    }

    public void InsertClass(String id)
    {
        ClassType c=new ClassType(id);

        Classes.put(id,c);

    }

    public ClassType GetClass(String id)
    {

        return Classes.get(id);

    }

    public MethodType getMethod(String meth_name){
        return Methods.get(meth_name);
    }

    public void AddMethod(String meth_name,MethodType MT){
        Methods.put(meth_name,MT);
    }

    public boolean FindClass(String id)
    {
        return Classes.containsKey(id);
    }

    public void WriteV_TablesToFile(PrintWriter pw)
    {

        for (Map.Entry<String, ClassType> entry : Classes.entrySet()) {
            String key = entry.getKey();
            ClassType ct=entry.getValue();

            pw.print("@."+key+"_vtable = global ");
            ct.PrintV_Table(pw);

        }
        pw.println();
        pw.println();
        pw.println("declare i8* @calloc(i32, i32)\n" +
                "declare i32 @printf(i8*, ...)\n" +
                "declare void @exit(i32)\n" +
                "\n" +
                "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n" +
                "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n" +
                "define void @print_int(i32 %i) {\n" +
                "    %_str = bitcast [4 x i8]* @_cint to i8*\n" +
                "    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n" +
                "    ret void\n" +
                "}\n" +
                "\n" +
                "define void @throw_oob() {\n" +
                "    %_str = bitcast [15 x i8]* @_cOOB to i8*\n" +
                "    call i32 (i8*, ...) @printf(i8* %_str)\n" +
                "    call void @exit(i32 1)\n" +
                "    ret void\n" +
                "}\n");

    }



}
