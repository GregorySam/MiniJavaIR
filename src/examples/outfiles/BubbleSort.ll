@.BubbleSort_vtable = global [0 x i8*][]
@.BBS_vtable = global [4 x i8*] [i8* bitcast (i32 (i8*,i32)* @BBS.Start to i8*), i8* bitcast (i32 (i8*)* @BBS.Sort to i8*), i8* bitcast (i32 (i8*)* @BBS.Print to i8*), i8* bitcast (i32 (i8*,i32)* @BBS.Init to i8*)]


declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
define void @print_int(i32 %i) {
    %_str = bitcast [4 x i8]* @_cint to i8*
    call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
    ret void
}

define void @throw_oob() {
    %_str = bitcast [15 x i8]* @_cOOB to i8*
    call i32 (i8*, ...) @printf(i8* %_str)
    call void @exit(i32 1)
    ret void
}

define i32 @main(){
    %_0 = call i8* @calloc(i32 1, i32 20)
    %_1 = bitcast i8* %_0 to i8***
    %_2 = getelementptr [4 x i8*], [4 x i8*]* @.BBS_vtable, i32 0, i32 0
    store i8** %_2, i8*** %_1
    %_3 = bitcast i8* %_0 to i8***
    %_4 = load i8**, i8*** %_3
    %_5 = getelementptr i8*, i8** %_4, i32 0
    %_6 = load i8*, i8** %_5
    %_7 = bitcast i8* %_6 to i32 (i8*,i32)*
    %_8 = call i32 %_7(i8* %_0, i32 10)
    call void (i32) @print_int(i32 %_8)

    ret i32 0
}

define i32 @BBS.Start(i8* %this, i32 %.sz) {
    %sz = alloca i32
    store i32%.sz, i32* %sz
    %aux01 = alloca i32

    %_0 = bitcast i8* %this to i8***
    %_1 = load i8**, i8*** %_0
    %_2 = getelementptr i8*, i8** %_1, i32 3
    %_3 = load i8*, i8** %_2
    %_4 = bitcast i8* %_3 to i32 (i8*,i32)*
    %_5 = load i32, i32* %sz
    %_6 = call i32 %_4(i8* %this, i32 %_5)
    store i32 %_6, i32* %aux01
    %_7 = bitcast i8* %this to i8***
    %_8 = load i8**, i8*** %_7
    %_9 = getelementptr i8*, i8** %_8, i32 2
    %_10 = load i8*, i8** %_9
    %_11 = bitcast i8* %_10 to i32 (i8*)*
    %_12 = call i32 %_11(i8* %this)
    store i32 %_12, i32* %aux01
    call void (i32) @print_int(i32 99999)
    %_13 = bitcast i8* %this to i8***
    %_14 = load i8**, i8*** %_13
    %_15 = getelementptr i8*, i8** %_14, i32 1
    %_16 = load i8*, i8** %_15
    %_17 = bitcast i8* %_16 to i32 (i8*)*
    %_18 = call i32 %_17(i8* %this)
    store i32 %_18, i32* %aux01
    %_19 = bitcast i8* %this to i8***
    %_20 = load i8**, i8*** %_19
    %_21 = getelementptr i8*, i8** %_20, i32 2
    %_22 = load i8*, i8** %_21
    %_23 = bitcast i8* %_22 to i32 (i8*)*
    %_24 = call i32 %_23(i8* %this)
    store i32 %_24, i32* %aux01

    ret i32 0
}
define i32 @BBS.Sort(i8* %this) {
    %nt = alloca i32

    %i = alloca i32

    %aux02 = alloca i32

    %aux04 = alloca i32

    %aux05 = alloca i32

    %aux06 = alloca i32

    %aux07 = alloca i32

    %j = alloca i32

    %t = alloca i32

    %_1 = getelementptr i8, i8* %this, i32 16
    %_2 = bitcast i8* %_1 to i32*
    %_0 = load i32, i32* %_2
    %_3 = sub i32 %_0, 1
    store i32 %_3, i32* %i
    %_4 = sub i32 0, 1
    store i32 %_4, i32* %aux02
    br label %Label0

Label0:
    %_5 = load i32, i32* %aux02
    %_6 = load i32, i32* %i
    %_7 = icmp slt i32 %_5, %_6
    br i1 %_7, label %Label1, label %Label2

Label1:
    store i32 1, i32* %j
    br label %Label3

Label3:
    %_8 = load i32, i32* %j
    %_9 = load i32, i32* %i
    %_10 = add i32 %_9, 1
    %_11 = icmp slt i32 %_8, %_10
    br i1 %_11, label %Label4, label %Label5

Label4:
    %_12 = load i32, i32* %j
    %_13 = sub i32 %_12, 1
    store i32 %_13, i32* %aux07
    %_15 = getelementptr i8, i8* %this, i32 8
    %_16 = bitcast i8* %_15 to i32**
    %_14 = load i32*, i32** %_16
    %_17 = load i32, i32* %aux07
    %_18 = load i32, i32* %_14
    %_19 = icmp slt i32 %_17, %_18
    br i1 %_19, label %Label7, label %Label6

Label6:
    call void @throw_oob()
    br label %Label7

Label7:
    %_20 = icmp sle i32 0, %_17
    br i1 %_20, label %Label9, label %Label8

Label8:
    call void @throw_oob()
    br label %Label9

Label9:
    %_21 = add i32 %_17, 1
    %_22 = getelementptr i32, i32* %_14, i32 %_21
    %_23 = load i32, i32* %_22
    store i32 %_23, i32* %aux04
    %_25 = getelementptr i8, i8* %this, i32 8
    %_26 = bitcast i8* %_25 to i32**
    %_24 = load i32*, i32** %_26
    %_27 = load i32, i32* %j
    %_28 = load i32, i32* %_24
    %_29 = icmp slt i32 %_27, %_28
    br i1 %_29, label %Label11, label %Label10

Label10:
    call void @throw_oob()
    br label %Label11

Label11:
    %_30 = icmp sle i32 0, %_27
    br i1 %_30, label %Label13, label %Label12

Label12:
    call void @throw_oob()
    br label %Label13

Label13:
    %_31 = add i32 %_27, 1
    %_32 = getelementptr i32, i32* %_24, i32 %_31
    %_33 = load i32, i32* %_32
    store i32 %_33, i32* %aux05
    %_34 = load i32, i32* %aux05
    %_35 = load i32, i32* %aux04
    %_36 = icmp slt i32 %_34, %_35
    br i1 %_36, label %Label14, label %Label15

Label14:
    %_37 = load i32, i32* %j
    %_38 = sub i32 %_37, 1
    store i32 %_38, i32* %aux06
    %_40 = getelementptr i8, i8* %this, i32 8
    %_41 = bitcast i8* %_40 to i32**
    %_39 = load i32*, i32** %_41
    %_42 = load i32, i32* %aux06
    %_43 = load i32, i32* %_39
    %_44 = icmp slt i32 %_42, %_43
    br i1 %_44, label %Label18, label %Label17

Label17:
    call void @throw_oob()
    br label %Label18

Label18:
    %_45 = icmp sle i32 0, %_42
    br i1 %_45, label %Label20, label %Label19

Label19:
    call void @throw_oob()
    br label %Label20

Label20:
    %_46 = add i32 %_42, 1
    %_47 = getelementptr i32, i32* %_39, i32 %_46
    %_48 = load i32, i32* %_47
    store i32 %_48, i32* %t
    %_50 = getelementptr i8, i8* %this, i32 8
    %_51 = bitcast i8* %_50 to i32**
    %_49 = load i32*, i32** %_51
    %_52 = load i32, i32* %_49
    %_53 = load i32, i32* %aux06
    %_54 = icmp slt i32 %_53, %_52
    br i1 %_54, label %Label22, label %Label21

Label21:
    call void @throw_oob()
    br label %Label22

Label22:
    %_55 = icmp sle i32 0, %_53
    br i1 %_55, label %Label24, label %Label23

Label23:
    call void @throw_oob()
    br label %Label24

Label24:
    %_56 = add i32 %_53, 1
    %_57 = getelementptr i32, i32* %_49, i32 %_56
    %_59 = getelementptr i8, i8* %this, i32 8
    %_60 = bitcast i8* %_59 to i32**
    %_58 = load i32*, i32** %_60
    %_61 = load i32, i32* %j
    %_62 = load i32, i32* %_58
    %_63 = icmp slt i32 %_61, %_62
    br i1 %_63, label %Label26, label %Label25

Label25:
    call void @throw_oob()
    br label %Label26

Label26:
    %_64 = icmp sle i32 0, %_61
    br i1 %_64, label %Label28, label %Label27

Label27:
    call void @throw_oob()
    br label %Label28

Label28:
    %_65 = add i32 %_61, 1
    %_66 = getelementptr i32, i32* %_58, i32 %_65
    %_67 = load i32, i32* %_66
    store i32 %_67, i32* %_57
    %_69 = getelementptr i8, i8* %this, i32 8
    %_70 = bitcast i8* %_69 to i32**
    %_68 = load i32*, i32** %_70
    %_71 = load i32, i32* %_68
    %_72 = load i32, i32* %j
    %_73 = icmp slt i32 %_72, %_71
    br i1 %_73, label %Label30, label %Label29

Label29:
    call void @throw_oob()
    br label %Label30

Label30:
    %_74 = icmp sle i32 0, %_72
    br i1 %_74, label %Label32, label %Label31

Label31:
    call void @throw_oob()
    br label %Label32

Label32:
    %_75 = add i32 %_72, 1
    %_76 = getelementptr i32, i32* %_68, i32 %_75
    %_77 = load i32, i32* %t
    store i32 %_77, i32* %_76
    br label%Label16

Label15:
    store i32 0, i32* %nt
    br label%Label16

Label16:
    %_78 = load i32, i32* %j
    %_79 = add i32 %_78, 1
    store i32 %_79, i32* %j
    br label %Label3
Label5:
    %_80 = load i32, i32* %i
    %_81 = sub i32 %_80, 1
    store i32 %_81, i32* %i
    br label %Label0
Label2:

    ret i32 0
}
define i32 @BBS.Print(i8* %this) {
    %j = alloca i32

    store i32 0, i32* %j
    br label %Label0

Label0:
    %_0 = load i32, i32* %j
    %_2 = getelementptr i8, i8* %this, i32 16
    %_3 = bitcast i8* %_2 to i32*
    %_1 = load i32, i32* %_3
    %_4 = icmp slt i32 %_0, %_1
    br i1 %_4, label %Label1, label %Label2

Label1:
    %_6 = getelementptr i8, i8* %this, i32 8
    %_7 = bitcast i8* %_6 to i32**
    %_5 = load i32*, i32** %_7
    %_8 = load i32, i32* %j
    %_9 = load i32, i32* %_5
    %_10 = icmp slt i32 %_8, %_9
    br i1 %_10, label %Label4, label %Label3

Label3:
    call void @throw_oob()
    br label %Label4

Label4:
    %_11 = icmp sle i32 0, %_8
    br i1 %_11, label %Label6, label %Label5

Label5:
    call void @throw_oob()
    br label %Label6

Label6:
    %_12 = add i32 %_8, 1
    %_13 = getelementptr i32, i32* %_5, i32 %_12
    %_14 = load i32, i32* %_13
    call void (i32) @print_int(i32 %_14)
    %_15 = load i32, i32* %j
    %_16 = add i32 %_15, 1
    store i32 %_16, i32* %j
    br label %Label0
Label2:

    ret i32 0
}
define i32 @BBS.Init(i8* %this, i32 %.sz) {
    %sz = alloca i32
    store i32%.sz, i32* %sz
    %_0 = load i32, i32* %sz
    %_1 = getelementptr i8, i8* %this, i32 16
    %_2 = bitcast i8* %_1 to i32*
    store i32 %_0, i32* %_2
    %_3 = load i32, i32* %sz
    %_4 = icmp sle i32 0, %_3
    br i1 %_4, label %Label1, label %Label0

Label0:
    call void @throw_oob()
    br label %Label1

Label1:
    %_5 = add i32 %_3, 1
    %_6 = call i8* @calloc(i32 4, i32 %_5)
    %_7 = bitcast i8* %_6 to i32*
    store i32 %_5, i32* %_7
    %_8 = getelementptr i8, i8* %this, i32 8
    %_9 = bitcast i8* %_8 to i32**
    store i32* %_7, i32** %_9
    %_11 = getelementptr i8, i8* %this, i32 8
    %_12 = bitcast i8* %_11 to i32**
    %_10 = load i32*, i32** %_12
    %_13 = load i32, i32* %_10
    %_14 = icmp sle i32 0, %_13
    br i1 %_14, label %Label3, label %Label2

Label2:
    call void @throw_oob()
    br label %Label3

Label3:
    %_15 = icmp sle i32 0, 0
    br i1 %_15, label %Label5, label %Label4

Label4:
    call void @throw_oob()
    br label %Label5

Label5:
    %_16 = add i32 0, 1
    %_17 = getelementptr i32, i32* %_10, i32 %_16
    store i32 20, i32* %_17
    %_19 = getelementptr i8, i8* %this, i32 8
    %_20 = bitcast i8* %_19 to i32**
    %_18 = load i32*, i32** %_20
    %_21 = load i32, i32* %_18
    %_22 = icmp slt i32 1, %_21
    br i1 %_22, label %Label7, label %Label6

Label6:
    call void @throw_oob()
    br label %Label7

Label7:
    %_23 = icmp sle i32 0, 1
    br i1 %_23, label %Label9, label %Label8

Label8:
    call void @throw_oob()
    br label %Label9

Label9:
    %_24 = add i32 1, 1
    %_25 = getelementptr i32, i32* %_18, i32 %_24
    store i32 7, i32* %_25
    %_27 = getelementptr i8, i8* %this, i32 8
    %_28 = bitcast i8* %_27 to i32**
    %_26 = load i32*, i32** %_28
    %_29 = load i32, i32* %_26
    %_30 = icmp slt i32 2, %_29
    br i1 %_30, label %Label11, label %Label10

Label10:
    call void @throw_oob()
    br label %Label11

Label11:
    %_31 = icmp sle i32 0, 2
    br i1 %_31, label %Label13, label %Label12

Label12:
    call void @throw_oob()
    br label %Label13

Label13:
    %_32 = add i32 2, 1
    %_33 = getelementptr i32, i32* %_26, i32 %_32
    store i32 12, i32* %_33
    %_35 = getelementptr i8, i8* %this, i32 8
    %_36 = bitcast i8* %_35 to i32**
    %_34 = load i32*, i32** %_36
    %_37 = load i32, i32* %_34
    %_38 = icmp slt i32 3, %_37
    br i1 %_38, label %Label15, label %Label14

Label14:
    call void @throw_oob()
    br label %Label15

Label15:
    %_39 = icmp sle i32 0, 3
    br i1 %_39, label %Label17, label %Label16

Label16:
    call void @throw_oob()
    br label %Label17

Label17:
    %_40 = add i32 3, 1
    %_41 = getelementptr i32, i32* %_34, i32 %_40
    store i32 18, i32* %_41
    %_43 = getelementptr i8, i8* %this, i32 8
    %_44 = bitcast i8* %_43 to i32**
    %_42 = load i32*, i32** %_44
    %_45 = load i32, i32* %_42
    %_46 = icmp slt i32 4, %_45
    br i1 %_46, label %Label19, label %Label18

Label18:
    call void @throw_oob()
    br label %Label19

Label19:
    %_47 = icmp sle i32 0, 4
    br i1 %_47, label %Label21, label %Label20

Label20:
    call void @throw_oob()
    br label %Label21

Label21:
    %_48 = add i32 4, 1
    %_49 = getelementptr i32, i32* %_42, i32 %_48
    store i32 2, i32* %_49
    %_51 = getelementptr i8, i8* %this, i32 8
    %_52 = bitcast i8* %_51 to i32**
    %_50 = load i32*, i32** %_52
    %_53 = load i32, i32* %_50
    %_54 = icmp slt i32 5, %_53
    br i1 %_54, label %Label23, label %Label22

Label22:
    call void @throw_oob()
    br label %Label23

Label23:
    %_55 = icmp sle i32 0, 5
    br i1 %_55, label %Label25, label %Label24

Label24:
    call void @throw_oob()
    br label %Label25

Label25:
    %_56 = add i32 5, 1
    %_57 = getelementptr i32, i32* %_50, i32 %_56
    store i32 11, i32* %_57
    %_59 = getelementptr i8, i8* %this, i32 8
    %_60 = bitcast i8* %_59 to i32**
    %_58 = load i32*, i32** %_60
    %_61 = load i32, i32* %_58
    %_62 = icmp slt i32 6, %_61
    br i1 %_62, label %Label27, label %Label26

Label26:
    call void @throw_oob()
    br label %Label27

Label27:
    %_63 = icmp sle i32 0, 6
    br i1 %_63, label %Label29, label %Label28

Label28:
    call void @throw_oob()
    br label %Label29

Label29:
    %_64 = add i32 6, 1
    %_65 = getelementptr i32, i32* %_58, i32 %_64
    store i32 6, i32* %_65
    %_67 = getelementptr i8, i8* %this, i32 8
    %_68 = bitcast i8* %_67 to i32**
    %_66 = load i32*, i32** %_68
    %_69 = load i32, i32* %_66
    %_70 = icmp slt i32 7, %_69
    br i1 %_70, label %Label31, label %Label30

Label30:
    call void @throw_oob()
    br label %Label31

Label31:
    %_71 = icmp sle i32 0, 7
    br i1 %_71, label %Label33, label %Label32

Label32:
    call void @throw_oob()
    br label %Label33

Label33:
    %_72 = add i32 7, 1
    %_73 = getelementptr i32, i32* %_66, i32 %_72
    store i32 9, i32* %_73
    %_75 = getelementptr i8, i8* %this, i32 8
    %_76 = bitcast i8* %_75 to i32**
    %_74 = load i32*, i32** %_76
    %_77 = load i32, i32* %_74
    %_78 = icmp slt i32 8, %_77
    br i1 %_78, label %Label35, label %Label34

Label34:
    call void @throw_oob()
    br label %Label35

Label35:
    %_79 = icmp sle i32 0, 8
    br i1 %_79, label %Label37, label %Label36

Label36:
    call void @throw_oob()
    br label %Label37

Label37:
    %_80 = add i32 8, 1
    %_81 = getelementptr i32, i32* %_74, i32 %_80
    store i32 19, i32* %_81
    %_83 = getelementptr i8, i8* %this, i32 8
    %_84 = bitcast i8* %_83 to i32**
    %_82 = load i32*, i32** %_84
    %_85 = load i32, i32* %_82
    %_86 = icmp slt i32 9, %_85
    br i1 %_86, label %Label39, label %Label38

Label38:
    call void @throw_oob()
    br label %Label39

Label39:
    %_87 = icmp sle i32 0, 9
    br i1 %_87, label %Label41, label %Label40

Label40:
    call void @throw_oob()
    br label %Label41

Label41:
    %_88 = add i32 9, 1
    %_89 = getelementptr i32, i32* %_82, i32 %_88
    store i32 5, i32* %_89

    ret i32 0
}
