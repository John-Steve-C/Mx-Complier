/usr/lib/jvm/java-17-openjdk-amd64/bin/java -Dfile.encoding=UTF-8 -classpath /mnt/d/Coding/Compiler/out/production/Compiler:/mnt/d/Coding/Compiler/antlr-4.9.2-complete.jar Main
%struct.string = type { i32, i8* }

@.libro.str = constant [2 x i8] c"\0A\00", align 1

define i32 @main(){
	%1 = alloca i32, align 4
	%2 = alloca i32, align 4
	%3 = alloca i32, align 4
	%4 = alloca i32, align 4
	%5 = alloca i32, align 4
	%6 = alloca i32, align 4
	%7 = alloca i32*, align 4
	%8 = alloca i32, align 4

0:
;0 108
	store i32 0, i32* %1, align 4
	store i32 10000, i32* %2, align 4
	store i32 0, i32* %3, align 4
	store i32 2800, i32* %4, align 4
	store i32 0, i32* %5, align 4
	store i32 0, i32* %6, align 4
	%9 = call i8* @myNew(i32 11208)
	%10 = bitcast i8* %9 to i32*
	store i32 2801, i32* %10, align 4
	%11 = bitcast i8* %9 to i32*
	store i32* %11, i32** %7, align 4
	store i32 0, i32* %8, align 4
	br label %12

;loop check block main loopDepth 1 iterCount 0
12:
;12 108
	%13 = load i32, i32* %3, align 4
	%14 = load i32, i32* %4, align 4
	%15 = sub i32 %13, %14
	%16 = icmp ne i32 %15, 0
	br i1 %16, label %17, label %31

;loop body main loopDepth 1 iterCount 0
17:
;17 108
	%18 = load i32, i32* %2, align 4
	%19 = sdiv i32 %18, 5
	%20 = load i32*, i32** %7, align 4
	%21 = load i32, i32* %3, align 4
	%22 = mul i32 %21, 4
	%23 = add i32 %22, 4
	%24 = bitcast i32* %20 to i8*
	%25 = getelementptr i8, i8* %24, i32 %23
	%26 = bitcast i8* %25 to i32*
	%27 = load i32, i32* %26, align 4
	store i32 %19, i32* %26, align 4
	br label %28

;loop increase block main loopDepth 1 iterCount 0
28:
;28 108
	%29 = load i32, i32* %3, align 4
	%30 = add i32 %29, 1
	store i32 %30, i32* %3, align 4
	br label %12

;loop exit block main loopDepth 1 iterCount 0
31:
;31 108
	br label %32

;loop check block main loopDepth 1 iterCount 1
32:
;32 108
	br label %33

;loop body main loopDepth 1 iterCount 1
33:
;33 108
	%34 = load i32, i32* %5, align 4
	store i32 0, i32* %5, align 4
	%35 = load i32, i32* %4, align 4
	%36 = mul i32 %35, 2
	%37 = load i32, i32* %8, align 4
	store i32 %36, i32* %8, align 4
	%38 = load i32, i32* %8, align 4
	%39 = icmp eq i32 %38, 0
	br i1 %39, label %40, label %45

;True block in main selectCount 0
40:
;40 108
	br label %41

;loop exit block main loopDepth 1 iterCount 1
41:
;41 108
	%42 = bitcast [2 x i8]* @.libro.str to i8*
	call void @print(i8* %42)
	store i32 0, i32* %1, align 4
	br label %43

43:
;43 108
	%44 = load i32, i32* %1, align 4
	ret i32 %44

;Converge block in main selectCount 0
45:
;45 108
	%46 = load i32, i32* %4, align 4
	%47 = load i32, i32* %3, align 4
	store i32 %46, i32* %3, align 4
	br label %48

;loop check block main loopDepth 2 iterCount 1
48:
;48 108
	br label %49

;loop body main loopDepth 2 iterCount 1
49:
;49 108
	%50 = load i32, i32* %5, align 4
	%51 = load i32*, i32** %7, align 4
	%52 = load i32, i32* %3, align 4
	%53 = mul i32 %52, 4
	%54 = add i32 %53, 4
	%55 = bitcast i32* %51 to i8*
	%56 = getelementptr i8, i8* %55, i32 %54
	%57 = bitcast i8* %56 to i32*
	%58 = load i32, i32* %57, align 4
	%59 = load i32, i32* %2, align 4
	%60 = mul i32 %58, %59
	%61 = add i32 %50, %60
	%62 = load i32, i32* %5, align 4
	store i32 %61, i32* %5, align 4
	%63 = load i32, i32* %5, align 4
	%64 = load i32, i32* %8, align 4
	%65 = sub i32 %64, 1
	store i32 %65, i32* %8, align 4
	%66 = srem i32 %63, %65
	%67 = load i32*, i32** %7, align 4
	%68 = load i32, i32* %3, align 4
	%69 = mul i32 %68, 4
	%70 = add i32 %69, 4
	%71 = bitcast i32* %67 to i8*
	%72 = getelementptr i8, i8* %71, i32 %70
	%73 = bitcast i8* %72 to i32*
	%74 = load i32, i32* %73, align 4
	store i32 %66, i32* %73, align 4
	%75 = load i32, i32* %5, align 4
	%76 = load i32, i32* %8, align 4
	%77 = sdiv i32 %75, %76
	%78 = load i32, i32* %5, align 4
	store i32 %77, i32* %5, align 4
	%79 = load i32, i32* %8, align 4
	%80 = sub i32 %79, 1
	store i32 %80, i32* %8, align 4
	%81 = load i32, i32* %3, align 4
	%82 = sub i32 %81, 1
	store i32 %82, i32* %3, align 4
	%83 = icmp eq i32 %82, 0
	br i1 %83, label %84, label %102

;True block in main selectCount 1
84:
;84 108
	br label %85

;loop exit block main loopDepth 2 iterCount 1
85:
;85 108
	%86 = load i32, i32* %4, align 4
	%87 = sub i32 %86, 14
	%88 = load i32, i32* %4, align 4
	store i32 %87, i32* %4, align 4
	%89 = load i32, i32* %6, align 4
	%90 = load i32, i32* %5, align 4
	%91 = load i32, i32* %2, align 4
	%92 = sdiv i32 %90, %91
	%93 = add i32 %89, %92
	%94 = call %struct.string* @toString(i32 %93)
	%95 = getelementptr %struct.string, %struct.string* %94, i32 0, i32 1
	%96 = load i8*, i8** %95, align 4
	call void @print(i8* %96)
	br label %97

;loop increase block main loopDepth 1 iterCount 1
97:
;97 108
	%98 = load i32, i32* %5, align 4
	%99 = load i32, i32* %2, align 4
	%100 = srem i32 %98, %99
	%101 = load i32, i32* %6, align 4
	store i32 %100, i32* %6, align 4
	br label %32

;Converge block in main selectCount 1
102:
;102 108
	br label %103

;loop increase block main loopDepth 2 iterCount 1
103:
;103 108
	%104 = load i32, i32* %5, align 4
	%105 = load i32, i32* %3, align 4
	%106 = mul i32 %104, %105
	%107 = load i32, i32* %5, align 4
	store i32 %106, i32* %5, align 4
	br label %48
}


declare i32 @_string_length(%struct.string*)
declare %struct.string* @_string_substring(%struct.string*, i32, i32)
declare i32 @_string_parseInt(%struct.string*)
declare i32 @_string_ord(%struct.string*, i32)
declare %struct.string* @_string_stringAppend(%struct.string*, %struct.string*)
declare i32 @_string_getStrcmp(%struct.string*, %struct.string*)
declare %struct.string* @toString(i32)
declare void @print(i8*)
declare void @println(i8*)
declare void @printInt(i32)
declare void @printlnInt(i32)
declare i32 @getInt()
declare %struct.string* @getString()
declare i8* @myNew(i32)


Process finished with exit code 0
