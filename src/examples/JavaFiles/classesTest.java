class classTest{

    public static void main(String[] s){
        A a;
        B b;
        int j;

        a=new A();
        j=((a.bla(a)).bla(a)).foo();


    }


}

class A{


    B b;
    public A bla(A a)
    {
        b=new B();
        return b;
    }

    public int foo()
    {
        System.out.println(1);
        return 0;
    }
}

class B extends A{
    public int foo()
    {
        System.out.println(2);
        return 0;
    }

}