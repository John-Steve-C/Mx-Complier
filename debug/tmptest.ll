target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"


declare void @f_print_1(i8* %str_1)
declare void @f_println_1(i8* %str_2)
declare void @f_printInt_1(i32 %n_1)
declare void @f_printlnInt_1(i32 %n_2)
declare i8* @f_getString_1()
declare i32 @f_getInt_1()
declare i8* @f_toString_1(i32 %i_1)
declare i1 @_stringcmp_eq_1(i8* %str1_1,i8* %str2_1)
declare i1 @_stringcmp_neq_1(i8* %str1_2,i8* %str2_2)
declare i1 @_stringcmp_less_1(i8* %str1_3,i8* %str2_3)
declare i1 @_stringcmp_greater_1(i8* %str1_4,i8* %str2_4)
declare i1 @_stringcmp_leq_1(i8* %str1_5,i8* %str2_5)
declare i1 @_stringcmp_geq_1(i8* %str1_6,i8* %str2_6)
declare i8* @_string_merge_1(i8* %str1_7,i8* %str2_7)
declare i8* @_heap_malloc_1(i32 %n_3)
declare i32 @_string_length_1(i8* %str_3)
declare i8* @_string_substring_1(i8* %str_4,i32 %l_1,i32 %r_1)
declare i32 @_string_parseInt_1(i8* %str_5)
declare i32 @_string_ord_1(i8* %str_6,i32 %ord_1)
@N_glo = global i32 zeroinitializer, align 4
@h_glo = global i32 zeroinitializer, align 4
@i_glo = global i32 zeroinitializer, align 4
@j_glo = global i32 zeroinitializer, align 4
@k_glo = global i32 zeroinitializer, align 4
@total_glo = global i32 zeroinitializer, align 4
@llvm.global_ctors = appending global [1 x { i32, void ()*, i8* }] [{ i32, void ()*, i8* } { i32 65535, void ()* @_GLOBAL_, i8* null }]
define void @_global_var_init()	{
h_bb:
	store i32 99, i32* @h_glo, align 4
	br label %h_bb1
h_bb1:					 ;preds = %h_bb
	ret void
}

define void @_global_var_init1()	{
i_bb:
	store i32 100, i32* @i_glo, align 4
	br label %i_bb1
i_bb1:					 ;preds = %i_bb
	ret void
}

define void @_global_var_init2()	{
j_bb:
	store i32 101, i32* @j_glo, align 4
	br label %j_bb1
j_bb1:					 ;preds = %j_bb
	ret void
}

define void @_global_var_init3()	{
k_bb:
	store i32 102, i32* @k_glo, align 4
	br label %k_bb1
k_bb1:					 ;preds = %k_bb
	ret void
}

define void @_global_var_init4()	{
total_bb:
	store i32 0, i32* @total_glo, align 4
	br label %total_bb1
total_bb1:					 ;preds = %total_bb
	ret void
}

define void @_GLOBAL_()	{
_GLOBAL__bb:
	call void @_global_var_init()
	call void @_global_var_init1()
	call void @_global_var_init2()
	call void @_global_var_init3()
	call void @_global_var_init4()
	ret void
}

define i32 @main()	{
main_bb:
	%_return.alloc = alloca i32, align 4
	%a.alloc = alloca i32, align 4
	%b.alloc = alloca i32, align 4
	%c.alloc = alloca i32, align 4
	%d.alloc = alloca i32, align 4
	%e.alloc = alloca i32, align 4
	%f.alloc = alloca i32, align 4
	%_call_f_getInt = call i32 @f_getInt_1()
	store i32 %_call_f_getInt, i32* @N_glo, align 4
	store i32 1, i32* %a.alloc, align 4
	%logic_and.alloc = alloca i8, align 4
	%logic_and.alloc1 = alloca i8, align 4
	%logic_and.alloc2 = alloca i8, align 4
	%logic_and.alloc3 = alloca i8, align 4
	%logic_and.alloc4 = alloca i8, align 4
	%logic_and.alloc5 = alloca i8, align 4
	%logic_and.alloc6 = alloca i8, align 4
	%logic_and.alloc7 = alloca i8, align 4
	%logic_and.alloc8 = alloca i8, align 4
	%logic_and.alloc9 = alloca i8, align 4
	%logic_and.alloc10 = alloca i8, align 4
	%logic_and.alloc11 = alloca i8, align 4
	%logic_and.alloc12 = alloca i8, align 4
	%logic_and.alloc13 = alloca i8, align 4
	br label %for_condition_bb
main_bb1:					 ;preds = %main_bb2
	%_return.load = load i32, i32* %_return.alloc, align 4
	ret i32 %_return.load
for_condition_bb:					 ;preds = %main_bb, %for_iter_bb
	%N.load = load i32, i32* @N_glo, align 4
	%a.load = load i32, i32* %a.alloc, align 4
	%sle = icmp sle i32 %a.load, %N.load
	%zext_ = zext i1 %sle to i8
	%trunc_ = trunc i8 %zext_ to i1
	br i1 %trunc_, label %for_body_bb, label %main_bb2
for_iter_bb:					 ;preds = %main_bb3
	%a.load10 = load i32, i32* %a.alloc, align 4
	%add6 = add i32 %a.load10, 1
	store i32 %add6, i32* %a.alloc, align 4
	br label %for_condition_bb
for_body_bb:					 ;preds = %for_condition_bb
	store i32 1, i32* %b.alloc, align 4
	br label %for_condition_bb1
main_bb2:					 ;preds = %for_condition_bb
	%total.load1 = load i32, i32* @total_glo, align 4
	%_call_f_toString = call i8* @f_toString_1(i32 %total.load1)
	call void @f_println_1(i8* %_call_f_toString)
	store i32 0, i32* %_return.alloc, align 4
	br label %main_bb1
for_condition_bb1:					 ;preds = %for_body_bb, %for_iter_bb1
	%N.load1 = load i32, i32* @N_glo, align 4
	%b.load = load i32, i32* %b.alloc, align 4
	%sle1 = icmp sle i32 %b.load, %N.load1
	%zext_1 = zext i1 %sle1 to i8
	%trunc_1 = trunc i8 %zext_1 to i1
	br i1 %trunc_1, label %for_body_bb1, label %main_bb3
for_iter_bb1:					 ;preds = %main_bb4
	%b.load8 = load i32, i32* %b.alloc, align 4
	%add5 = add i32 %b.load8, 1
	store i32 %add5, i32* %b.alloc, align 4
	br label %for_condition_bb1
for_body_bb1:					 ;preds = %for_condition_bb1
	store i32 1, i32* %c.alloc, align 4
	br label %for_condition_bb2
main_bb3:					 ;preds = %for_condition_bb1
	br label %for_iter_bb
for_condition_bb2:					 ;preds = %for_body_bb1, %for_iter_bb2
	%N.load2 = load i32, i32* @N_glo, align 4
	%c.load = load i32, i32* %c.alloc, align 4
	%sle2 = icmp sle i32 %c.load, %N.load2
	%zext_2 = zext i1 %sle2 to i8
	%trunc_2 = trunc i8 %zext_2 to i1
	br i1 %trunc_2, label %for_body_bb2, label %main_bb4
for_iter_bb2:					 ;preds = %main_bb5
	%c.load3 = load i32, i32* %c.alloc, align 4
	%add4 = add i32 %c.load3, 1
	store i32 %add4, i32* %c.alloc, align 4
	br label %for_condition_bb2
for_body_bb2:					 ;preds = %for_condition_bb2
	store i32 1, i32* %d.alloc, align 4
	br label %for_condition_bb3
main_bb4:					 ;preds = %for_condition_bb2
	br label %for_iter_bb1
for_condition_bb3:					 ;preds = %for_body_bb2, %for_iter_bb3
	%N.load3 = load i32, i32* @N_glo, align 4
	%d.load = load i32, i32* %d.alloc, align 4
	%sle3 = icmp sle i32 %d.load, %N.load3
	%zext_3 = zext i1 %sle3 to i8
	%trunc_3 = trunc i8 %zext_3 to i1
	br i1 %trunc_3, label %for_body_bb3, label %main_bb5
for_iter_bb3:					 ;preds = %main_bb6
	%d.load3 = load i32, i32* %d.alloc, align 4
	%add3 = add i32 %d.load3, 1
	store i32 %add3, i32* %d.alloc, align 4
	br label %for_condition_bb3
for_body_bb3:					 ;preds = %for_condition_bb3
	store i32 1, i32* %e.alloc, align 4
	br label %for_condition_bb4
main_bb5:					 ;preds = %for_condition_bb3
	br label %for_iter_bb2
for_condition_bb4:					 ;preds = %for_body_bb3, %for_iter_bb4
	%N.load4 = load i32, i32* @N_glo, align 4
	%e.load = load i32, i32* %e.alloc, align 4
	%sle4 = icmp sle i32 %e.load, %N.load4
	%zext_4 = zext i1 %sle4 to i8
	%trunc_4 = trunc i8 %zext_4 to i1
	br i1 %trunc_4, label %for_body_bb4, label %main_bb6
for_iter_bb4:					 ;preds = %main_bb7
	%e.load3 = load i32, i32* %e.alloc, align 4
	%add2 = add i32 %e.load3, 1
	store i32 %add2, i32* %e.alloc, align 4
	br label %for_condition_bb4
for_body_bb4:					 ;preds = %for_condition_bb4
	store i32 1, i32* %f.alloc, align 4
	br label %for_condition_bb5
main_bb6:					 ;preds = %for_condition_bb4
	br label %for_iter_bb3
for_condition_bb5:					 ;preds = %for_body_bb4, %for_iter_bb5
	%N.load5 = load i32, i32* @N_glo, align 4
	%f.load = load i32, i32* %f.alloc, align 4
	%sle5 = icmp sle i32 %f.load, %N.load5
	%zext_5 = zext i1 %sle5 to i8
	%trunc_5 = trunc i8 %zext_5 to i1
	br i1 %trunc_5, label %for_body_bb5, label %main_bb7
for_iter_bb5:					 ;preds = %main_bb8
	%f.load3 = load i32, i32* %f.alloc, align 4
	%add1 = add i32 %f.load3, 1
	store i32 %add1, i32* %f.alloc, align 4
	br label %for_condition_bb5
for_body_bb5:					 ;preds = %for_condition_bb5
	%b.load1 = load i32, i32* %b.alloc, align 4
	%a.load1 = load i32, i32* %a.alloc, align 4
	%ne = icmp ne i32 %a.load1, %b.load1
	%zext_6 = zext i1 %ne to i8
	%trunc_6 = trunc i8 %zext_6 to i1
	br i1 %trunc_6, label %_sBlock_bb, label %_dBlock_bb
main_bb7:					 ;preds = %for_condition_bb5
	br label %for_iter_bb4
if_then_bb:					 ;preds = %_tBlock_bb13
	%total.load = load i32, i32* @total_glo, align 4
	%add = add i32 %total.load, 1
	store i32 %add, i32* @total_glo, align 4
	br label %main_bb8
main_bb8:					 ;preds = %_tBlock_bb13, %if_then_bb
	br label %for_iter_bb5
_dBlock_bb:					 ;preds = %for_body_bb5
	store i8 %zext_6, i8* %logic_and.alloc, align 4
	br label %_tBlock_bb
_sBlock_bb:					 ;preds = %for_body_bb5
	%c.load1 = load i32, i32* %c.alloc, align 4
	%a.load2 = load i32, i32* %a.alloc, align 4
	%ne1 = icmp ne i32 %a.load2, %c.load1
	%zext_7 = zext i1 %ne1 to i8
	store i8 %zext_7, i8* %logic_and.alloc, align 4
	br label %_tBlock_bb
_tBlock_bb:					 ;preds = %_dBlock_bb, %_sBlock_bb
	%circuit.load = load i8, i8* %logic_and.alloc, align 4
	%trunc_7 = trunc i8 %circuit.load to i1
	br i1 %trunc_7, label %_sBlock_bb1, label %_dBlock_bb1
_dBlock_bb1:					 ;preds = %_tBlock_bb
	store i8 %circuit.load, i8* %logic_and.alloc1, align 4
	br label %_tBlock_bb1
_sBlock_bb1:					 ;preds = %_tBlock_bb
	%d.load1 = load i32, i32* %d.alloc, align 4
	%a.load3 = load i32, i32* %a.alloc, align 4
	%ne2 = icmp ne i32 %a.load3, %d.load1
	%zext_8 = zext i1 %ne2 to i8
	store i8 %zext_8, i8* %logic_and.alloc1, align 4
	br label %_tBlock_bb1
_tBlock_bb1:					 ;preds = %_dBlock_bb1, %_sBlock_bb1
	%circuit.load1 = load i8, i8* %logic_and.alloc1, align 4
	%trunc_8 = trunc i8 %circuit.load1 to i1
	br i1 %trunc_8, label %_sBlock_bb2, label %_dBlock_bb2
_dBlock_bb2:					 ;preds = %_tBlock_bb1
	store i8 %circuit.load1, i8* %logic_and.alloc2, align 4
	br label %_tBlock_bb2
_sBlock_bb2:					 ;preds = %_tBlock_bb1
	%e.load1 = load i32, i32* %e.alloc, align 4
	%a.load4 = load i32, i32* %a.alloc, align 4
	%ne3 = icmp ne i32 %a.load4, %e.load1
	%zext_9 = zext i1 %ne3 to i8
	store i8 %zext_9, i8* %logic_and.alloc2, align 4
	br label %_tBlock_bb2
_tBlock_bb2:					 ;preds = %_dBlock_bb2, %_sBlock_bb2
	%circuit.load2 = load i8, i8* %logic_and.alloc2, align 4
	%trunc_9 = trunc i8 %circuit.load2 to i1
	br i1 %trunc_9, label %_sBlock_bb3, label %_dBlock_bb3
_dBlock_bb3:					 ;preds = %_tBlock_bb2
	store i8 %circuit.load2, i8* %logic_and.alloc3, align 4
	br label %_tBlock_bb3
_sBlock_bb3:					 ;preds = %_tBlock_bb2
	%f.load1 = load i32, i32* %f.alloc, align 4
	%a.load5 = load i32, i32* %a.alloc, align 4
	%ne4 = icmp ne i32 %a.load5, %f.load1
	%zext_10 = zext i1 %ne4 to i8
	store i8 %zext_10, i8* %logic_and.alloc3, align 4
	br label %_tBlock_bb3
_tBlock_bb3:					 ;preds = %_dBlock_bb3, %_sBlock_bb3
	%circuit.load3 = load i8, i8* %logic_and.alloc3, align 4
	%trunc_10 = trunc i8 %circuit.load3 to i1
	br i1 %trunc_10, label %_sBlock_bb4, label %_dBlock_bb4
_dBlock_bb4:					 ;preds = %_tBlock_bb3
	store i8 %circuit.load3, i8* %logic_and.alloc4, align 4
	br label %_tBlock_bb4
_sBlock_bb4:					 ;preds = %_tBlock_bb3
	%h.load = load i32, i32* @h_glo, align 4
	%a.load6 = load i32, i32* %a.alloc, align 4
	%ne5 = icmp ne i32 %a.load6, %h.load
	%zext_11 = zext i1 %ne5 to i8
	store i8 %zext_11, i8* %logic_and.alloc4, align 4
	br label %_tBlock_bb4
_tBlock_bb4:					 ;preds = %_dBlock_bb4, %_sBlock_bb4
	%circuit.load4 = load i8, i8* %logic_and.alloc4, align 4
	%trunc_11 = trunc i8 %circuit.load4 to i1
	br i1 %trunc_11, label %_sBlock_bb5, label %_dBlock_bb5
_dBlock_bb5:					 ;preds = %_tBlock_bb4
	store i8 %circuit.load4, i8* %logic_and.alloc5, align 4
	br label %_tBlock_bb5
_sBlock_bb5:					 ;preds = %_tBlock_bb4
	%i.load = load i32, i32* @i_glo, align 4
	%a.load7 = load i32, i32* %a.alloc, align 4
	%ne6 = icmp ne i32 %a.load7, %i.load
	%zext_12 = zext i1 %ne6 to i8
	store i8 %zext_12, i8* %logic_and.alloc5, align 4
	br label %_tBlock_bb5
_tBlock_bb5:					 ;preds = %_dBlock_bb5, %_sBlock_bb5
	%circuit.load5 = load i8, i8* %logic_and.alloc5, align 4
	%trunc_12 = trunc i8 %circuit.load5 to i1
	br i1 %trunc_12, label %_sBlock_bb6, label %_dBlock_bb6
_dBlock_bb6:					 ;preds = %_tBlock_bb5
	store i8 %circuit.load5, i8* %logic_and.alloc6, align 4
	br label %_tBlock_bb6
_sBlock_bb6:					 ;preds = %_tBlock_bb5
	%j.load = load i32, i32* @j_glo, align 4
	%a.load8 = load i32, i32* %a.alloc, align 4
	%ne7 = icmp ne i32 %a.load8, %j.load
	%zext_13 = zext i1 %ne7 to i8
	store i8 %zext_13, i8* %logic_and.alloc6, align 4
	br label %_tBlock_bb6
_tBlock_bb6:					 ;preds = %_dBlock_bb6, %_sBlock_bb6
	%circuit.load6 = load i8, i8* %logic_and.alloc6, align 4
	%trunc_13 = trunc i8 %circuit.load6 to i1
	br i1 %trunc_13, label %_sBlock_bb7, label %_dBlock_bb7
_dBlock_bb7:					 ;preds = %_tBlock_bb6
	store i8 %circuit.load6, i8* %logic_and.alloc7, align 4
	br label %_tBlock_bb7
_sBlock_bb7:					 ;preds = %_tBlock_bb6
	%k.load = load i32, i32* @k_glo, align 4
	%a.load9 = load i32, i32* %a.alloc, align 4
	%ne8 = icmp ne i32 %a.load9, %k.load
	%zext_14 = zext i1 %ne8 to i8
	store i8 %zext_14, i8* %logic_and.alloc7, align 4
	br label %_tBlock_bb7
_tBlock_bb7:					 ;preds = %_dBlock_bb7, %_sBlock_bb7
	%circuit.load7 = load i8, i8* %logic_and.alloc7, align 4
	%trunc_14 = trunc i8 %circuit.load7 to i1
	br i1 %trunc_14, label %_sBlock_bb8, label %_dBlock_bb8
_dBlock_bb8:					 ;preds = %_tBlock_bb7
	store i8 %circuit.load7, i8* %logic_and.alloc8, align 4
	br label %_tBlock_bb8
_sBlock_bb8:					 ;preds = %_tBlock_bb7
	%c.load2 = load i32, i32* %c.alloc, align 4
	%b.load2 = load i32, i32* %b.alloc, align 4
	%ne9 = icmp ne i32 %b.load2, %c.load2
	%zext_15 = zext i1 %ne9 to i8
	store i8 %zext_15, i8* %logic_and.alloc8, align 4
	br label %_tBlock_bb8
_tBlock_bb8:					 ;preds = %_dBlock_bb8, %_sBlock_bb8
	%circuit.load8 = load i8, i8* %logic_and.alloc8, align 4
	%trunc_15 = trunc i8 %circuit.load8 to i1
	br i1 %trunc_15, label %_sBlock_bb9, label %_dBlock_bb9
_dBlock_bb9:					 ;preds = %_tBlock_bb8
	store i8 %circuit.load8, i8* %logic_and.alloc9, align 4
	br label %_tBlock_bb9
_sBlock_bb9:					 ;preds = %_tBlock_bb8
	%d.load2 = load i32, i32* %d.alloc, align 4
	%b.load3 = load i32, i32* %b.alloc, align 4
	%ne10 = icmp ne i32 %b.load3, %d.load2
	%zext_16 = zext i1 %ne10 to i8
	store i8 %zext_16, i8* %logic_and.alloc9, align 4
	br label %_tBlock_bb9
_tBlock_bb9:					 ;preds = %_dBlock_bb9, %_sBlock_bb9
	%circuit.load9 = load i8, i8* %logic_and.alloc9, align 4
	%trunc_16 = trunc i8 %circuit.load9 to i1
	br i1 %trunc_16, label %_sBlock_bb10, label %_dBlock_bb10
_dBlock_bb10:					 ;preds = %_tBlock_bb9
	store i8 %circuit.load9, i8* %logic_and.alloc10, align 4
	br label %_tBlock_bb10
_sBlock_bb10:					 ;preds = %_tBlock_bb9
	%e.load2 = load i32, i32* %e.alloc, align 4
	%b.load4 = load i32, i32* %b.alloc, align 4
	%ne11 = icmp ne i32 %b.load4, %e.load2
	%zext_17 = zext i1 %ne11 to i8
	store i8 %zext_17, i8* %logic_and.alloc10, align 4
	br label %_tBlock_bb10
_tBlock_bb10:					 ;preds = %_dBlock_bb10, %_sBlock_bb10
	%circuit.load10 = load i8, i8* %logic_and.alloc10, align 4
	%trunc_17 = trunc i8 %circuit.load10 to i1
	br i1 %trunc_17, label %_sBlock_bb11, label %_dBlock_bb11
_dBlock_bb11:					 ;preds = %_tBlock_bb10
	store i8 %circuit.load10, i8* %logic_and.alloc11, align 4
	br label %_tBlock_bb11
_sBlock_bb11:					 ;preds = %_tBlock_bb10
	%f.load2 = load i32, i32* %f.alloc, align 4
	%b.load5 = load i32, i32* %b.alloc, align 4
	%ne12 = icmp ne i32 %b.load5, %f.load2
	%zext_18 = zext i1 %ne12 to i8
	store i8 %zext_18, i8* %logic_and.alloc11, align 4
	br label %_tBlock_bb11
_tBlock_bb11:					 ;preds = %_dBlock_bb11, %_sBlock_bb11
	%circuit.load11 = load i8, i8* %logic_and.alloc11, align 4
	%trunc_18 = trunc i8 %circuit.load11 to i1
	br i1 %trunc_18, label %_sBlock_bb12, label %_dBlock_bb12
_dBlock_bb12:					 ;preds = %_tBlock_bb11
	store i8 %circuit.load11, i8* %logic_and.alloc12, align 4
	br label %_tBlock_bb12
_sBlock_bb12:					 ;preds = %_tBlock_bb11
	%h.load1 = load i32, i32* @h_glo, align 4
	%b.load6 = load i32, i32* %b.alloc, align 4
	%ne13 = icmp ne i32 %b.load6, %h.load1
	%zext_19 = zext i1 %ne13 to i8
	store i8 %zext_19, i8* %logic_and.alloc12, align 4
	br label %_tBlock_bb12
_tBlock_bb12:					 ;preds = %_dBlock_bb12, %_sBlock_bb12
	%circuit.load12 = load i8, i8* %logic_and.alloc12, align 4
	%trunc_19 = trunc i8 %circuit.load12 to i1
	br i1 %trunc_19, label %_sBlock_bb13, label %_dBlock_bb13
_dBlock_bb13:					 ;preds = %_tBlock_bb12
	store i8 %circuit.load12, i8* %logic_and.alloc13, align 4
	br label %_tBlock_bb13
_sBlock_bb13:					 ;preds = %_tBlock_bb12
	%i.load1 = load i32, i32* @i_glo, align 4
	%b.load7 = load i32, i32* %b.alloc, align 4
	%ne14 = icmp ne i32 %b.load7, %i.load1
	%zext_20 = zext i1 %ne14 to i8
	store i8 %zext_20, i8* %logic_and.alloc13, align 4
	br label %_tBlock_bb13
_tBlock_bb13:					 ;preds = %_dBlock_bb13, %_sBlock_bb13
	%circuit.load13 = load i8, i8* %logic_and.alloc13, align 4
	%trunc_20 = trunc i8 %circuit.load13 to i1
	br i1 %trunc_20, label %if_then_bb, label %main_bb8
}