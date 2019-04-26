import java.util.*;








class ScopeType
{
    protected HashMap<String, String> Variables;
    private String scopename;


    public boolean InsertVariable(String id, String p)
    {
        if(Variables.containsKey(id))
        {
            return false;
        }
        else
        {
            Variables.put(id,p);
            return true;
        }
    }

    public String GetScopeName(){
        return scopename;
    }


    public ScopeType(String name)
    {
        this.scopename=name;
        Variables =new HashMap<>();
    }

    public HashMap<String,String> GetVariables()
    {
        return Variables;
    }



    public String GetType(String id)
    {
       return Variables.get(id);
    }



}

class MethodType extends ScopeType
{
    private String name;
    private String id;
    private String type;
    private List<String> ParametersTypes;
    private ClassType ClassPertain;

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
    public String GetType(String id)
    {
        if(Variables.get(id)==null)
        {

            return ClassPertain.GetType(id);

        }
        else
        {
            String str_type;

            str_type=Variables.get(id);

            return str_type;
        }
    }


    public void ChangeId(String a)
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

    public boolean CheckParametersMatch(String params,STDataStructure std){

        if(params==null && ParametersTypes.size()==0)
        {
            return true;
        }
        else if(params==null )
        {
            return false;
        }
        String[] parts=params.split(",");
        int i;

        if(parts.length!=ParametersTypes.size()){
            return false;
        }

        for(i=0;i<parts.length;i++)
        {
            if(!ParametersTypes.get(i).equals(parts[i]))
            {
                if(!parts[i].equals("int") && !parts[i].equals("boolean") && !parts[i].equals("int[]")){
                    ClassType base_class_type,classType,original_baseclass;

                    classType=std.GetClass(parts[i]);
                    base_class_type=std.GetClass(ParametersTypes.get(i));
                    original_baseclass=classType.GetBaseClass();

                    if(original_baseclass==null)
                    {
                        return false;
                    }

                    return base_class_type.GetName().equals(original_baseclass.GetName());


                }
                else
                {
                    return false;
                }
            }
        }
        return true;

    }

}

class ClassType extends ScopeType
{
    private String name;
    private ClassType BaseClass;
    private HashMap<String, MethodType> Methods;

    public ClassType(String n)
    {
        super("class "+n);
        name=n;
        Methods=new HashMap<>();
        BaseClass=null;
    }
    public boolean IsTypeOf(String id)
    {

    }

    public boolean InsertMethod(MethodType MT)
    {

        if(Methods.containsKey(MT.GetName())) {
            return false;
        }


        if(BaseClass==null)
        {
            Methods.put(MT.GetName(),MT);
            return true;
        }

        MethodType base_class_meth;

        base_class_meth=BaseClass.GetMethod(MT.GetName());

        if(base_class_meth==null) {
            Methods.put(MT.GetName(),MT);
            return true;
        }
        else {

            String base_funid;
            base_funid=MT.GetId();

            String base_class_funid=base_class_meth.GetId();
            if(base_funid.equals(base_class_funid))
            {
                Methods.put(MT.GetName(),MT);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public String GetName(){return name;}

    public boolean FindMethod(String id)
    {
        return  Methods.get(id)!=null;
    }


    public void SetBaseClass(ClassType id)
    {
        BaseClass=id;
    }

    public MethodType GetMethod(String id) {

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
    public String GetType(String id)
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

    public HashMap<String, MethodType> GetMethods()
    {
        return Methods;
    }

    public ClassType GetBaseClass(){return  BaseClass;}



}


public class STDataStructure {

    private ScopeType MainVariables;
    private HashMap<String,ClassType> Classes;

    public STDataStructure(){
        MainVariables=new ScopeType("Main");

        Classes=new HashMap<>();
    }


    public ScopeType GetMainVariables()
    {
        return MainVariables;
    }

    public boolean InsertClass(String id)
    {
        ClassType c=new ClassType(id);

        if(this.Classes.containsKey(id))
        {
            return false;
        }
        else
        {
            this.Classes.put(id,c);
            return true;
        }

    }

    public ClassType GetClass(String id)
    {

        return Classes.get(id);

    }

    public boolean FindClass(String id)
    {
        return Classes.containsKey(id);
    }



}
