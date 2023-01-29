    .text
	.file	"myBuiltin.cpp"
	.globl	print                   # -- Begin function print
	.p2align	2
	.type	print,@function
print:                                  # @print
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	mv	a1, a0
	sw	a0, 8(sp)
	lui	a0, %hi(.L.str)
	addi	a0, a0, %lo(.L.str)
	call	printf
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end0:
	.size	print, .Lfunc_end0-print
	.cfi_endproc
                                        # -- End function
	.globl	println                 # -- Begin function println
	.p2align	2
	.type	println,@function
println:                                # @println
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	mv	a1, a0
	sw	a0, 8(sp)
	lui	a0, %hi(.L.str.1)
	addi	a0, a0, %lo(.L.str.1)
	call	printf
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end1:
	.size	println, .Lfunc_end1-println
	.cfi_endproc
                                        # -- End function
	.globl	printInt                # -- Begin function printInt
	.p2align	2
	.type	printInt,@function
printInt:                               # @printInt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	mv	a1, a0
	sw	a0, 8(sp)
	lui	a0, %hi(.L.str.2)
	addi	a0, a0, %lo(.L.str.2)
	call	printf
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end2:
	.size	printInt, .Lfunc_end2-printInt
	.cfi_endproc
                                        # -- End function
	.globl	printlnInt              # -- Begin function printlnInt
	.p2align	2
	.type	printlnInt,@function
printlnInt:                             # @printlnInt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	mv	a1, a0
	sw	a0, 8(sp)
	lui	a0, %hi(.L.str.3)
	addi	a0, a0, %lo(.L.str.3)
	call	printf
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end3:
	.size	printlnInt, .Lfunc_end3-printlnInt
	.cfi_endproc
                                        # -- End function
	.globl	getString               # -- Begin function getString
	.p2align	2
	.type	getString,@function
getString:                              # @getString
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -272
	.cfi_def_cfa_offset 272
	sw	ra, 268(sp)
	sw	s0, 264(sp)
	.cfi_offset ra, -4
	.cfi_offset s0, -8
	addi	a0, zero, 12
	mv	a1, zero
	call	malloc
	sw	a0, 256(sp)
	lui	a0, %hi(.L.str)
	addi	a0, a0, %lo(.L.str)
	mv	s0, sp
	mv	a1, s0
	call	__isoc99_scanf
	mv	a0, s0
	call	strlen
	lw	a1, 256(sp)
	sw	a0, 0(a1)
	lw	a0, 256(sp)
	lw	a0, 0(a0)
	addi	a0, a0, 1
	srai	a1, a0, 31
	call	malloc
	lw	a1, 256(sp)
	sw	a0, 4(a1)
	lw	a0, 256(sp)
	lw	a0, 4(a0)
	mv	a1, s0
	call	strcpy
	lw	a0, 256(sp)
	lw	s0, 264(sp)
	lw	ra, 268(sp)
	addi	sp, sp, 272
	ret
.Lfunc_end4:
	.size	getString, .Lfunc_end4-getString
	.cfi_endproc
                                        # -- End function
	.globl	getInt                  # -- Begin function getInt
	.p2align	2
	.type	getInt,@function
getInt:                                 # @getInt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	lui	a0, %hi(.L.str.2)
	addi	a0, a0, %lo(.L.str.2)
	addi	a1, sp, 8
	call	__isoc99_scanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end5:
	.size	getInt, .Lfunc_end5-getInt
	.cfi_endproc
                                        # -- End function
	.globl	toString                # -- Begin function toString
	.p2align	2
	.type	toString,@function
toString:                               # @toString
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -32
	.cfi_def_cfa_offset 32
	sw	ra, 28(sp)
	.cfi_offset ra, -4
	sw	a0, 24(sp)
	sw	zero, 20(sp)
	addi	a1, zero, 1
	sw	a1, 16(sp)
	srli	a1, a0, 31
	addi	a2, zero, -1
	sb	a1, 15(sp)
	blt	a2, a0, .LBB6_2
# %bb.1:
	lw	a0, 16(sp)
	lw	a1, 24(sp)
	addi	a0, a0, 1
	sw	a0, 16(sp)
	neg	a0, a1
	sw	a0, 24(sp)
.LBB6_2:
	lw	a0, 24(sp)
	beqz	a0, .LBB6_9
# %bb.3:
	lw	a0, 24(sp)
	sw	a0, 20(sp)
	addi	a0, zero, 1
	lui	a1, 419430
	addi	a1, a1, 1639
	lw	a2, 24(sp)
	blt	a2, a0, .LBB6_5
.LBB6_4:                                # =>This Inner Loop Header: Depth=1
	lw	a2, 24(sp)
	mulh	a2, a2, a1
	srli	a3, a2, 31
	lw	a4, 16(sp)
	srai	a2, a2, 2
	add	a2, a2, a3
	sw	a2, 24(sp)
	addi	a2, a4, 1
	sw	a2, 16(sp)
	lw	a2, 24(sp)
	bge	a2, a0, .LBB6_4
.LBB6_5:
	lw	a0, 16(sp)
	srai	a1, a0, 31
	call	malloc
	lbu	a1, 15(sp)
	sw	a0, 8(sp)
	addi	a6, zero, 1
	andi	a1, a1, 1
	sw	a6, 4(sp)
	beqz	a1, .LBB6_7
# %bb.6:
	lw	a1, 8(sp)
	addi	a2, zero, 45
	sb	a2, 0(a1)
.LBB6_7:
	lui	a1, 419430
	addi	a1, a1, 1639
	addi	a7, zero, 10
	lw	a3, 20(sp)
	blt	a3, a6, .LBB6_10
.LBB6_8:                                # =>This Inner Loop Header: Depth=1
	lw	a3, 20(sp)
	mulh	a4, a3, a1
	srli	a5, a4, 31
	srli	a4, a4, 2
	add	a4, a4, a5
	mul	a4, a4, a7
	lw	a5, 16(sp)
	lw	a0, 4(sp)
	lw	a2, 8(sp)
	sub	a3, a3, a4
	addi	a3, a3, 48
	sub	a0, a5, a0
	add	a0, a0, a2
	sb	a3, -1(a0)
	lw	a0, 4(sp)
	lw	a2, 20(sp)
	addi	a0, a0, 1
	sw	a0, 4(sp)
	mulh	a0, a2, a1
	srli	a2, a0, 31
	srai	a0, a0, 2
	add	a0, a0, a2
	sw	a0, 20(sp)
	lw	a3, 20(sp)
	bge	a3, a6, .LBB6_8
	j	.LBB6_10
.LBB6_9:
	addi	a0, zero, 2
	sw	a0, 16(sp)
	addi	a0, zero, 2
	mv	a1, zero
	call	malloc
	sw	a0, 8(sp)
	addi	a1, zero, 48
	sb	a1, 0(a0)
	lw	a0, 8(sp)
	sb	zero, 1(a0)
.LBB6_10:
	addi	a0, zero, 12
	mv	a1, zero
	call	malloc
	lw	a1, 8(sp)
	sw	a0, 0(sp)
	sw	a1, 4(a0)
	lw	a0, 16(sp)
	lw	a1, 0(sp)
	addi	a0, a0, -1
	sw	a0, 0(a1)
	lw	a0, 0(sp)
	lw	ra, 28(sp)
	addi	sp, sp, 32
	ret
.Lfunc_end6:
	.size	toString, .Lfunc_end6-toString
	.cfi_endproc
                                        # -- End function
	.globl	_string_stringAppend    # -- Begin function _string_stringAppend
	.p2align	2
	.type	_string_stringAppend,@function
_string_stringAppend:                   # @_string_stringAppend
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -32
	.cfi_def_cfa_offset 32
	sw	ra, 28(sp)
	.cfi_offset ra, -4
	sw	a0, 24(sp)
	sw	a1, 16(sp)
	lw	a0, 0(a0)
	lw	a1, 0(a1)
	add	a0, a0, a1
	addi	a0, a0, 1
	srai	a1, a0, 31
	call	malloc
	lw	a1, 24(sp)
	sw	a0, 8(sp)
	lw	a1, 4(a1)
	call	strcpy
	lw	a0, 24(sp)
	lw	a1, 16(sp)
	lw	a2, 8(sp)
	lw	a0, 0(a0)
	lw	a1, 4(a1)
	add	a0, a2, a0
	call	strcpy
	addi	a0, zero, 12
	mv	a1, zero
	call	malloc
	lw	a1, 8(sp)
	sw	a0, 0(sp)
	sw	a1, 4(a0)
	lw	a0, 24(sp)
	lw	a1, 16(sp)
	lw	a0, 0(a0)
	lw	a1, 0(a1)
	lw	a2, 0(sp)
	add	a0, a0, a1
	sw	a0, 0(a2)
	lw	a0, 0(sp)
	lw	ra, 28(sp)
	addi	sp, sp, 32
	ret
.Lfunc_end7:
	.size	_string_stringAppend, .Lfunc_end7-_string_stringAppend
	.cfi_endproc
                                        # -- End function
	.globl	_string_substring       # -- Begin function _string_substring
	.p2align	2
	.type	_string_substring,@function
_string_substring:                      # @_string_substring
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -32
	.cfi_def_cfa_offset 32
	sw	ra, 28(sp)
	.cfi_offset ra, -4
	sw	a0, 24(sp)
	sw	a1, 20(sp)
	sw	a2, 16(sp)
	sub	a0, a2, a1
	addi	a0, a0, 1
	srai	a1, a0, 31
	call	malloc
	lw	a1, 20(sp)
	sw	a0, 8(sp)
	sw	a1, 4(sp)
.LBB8_1:                                # =>This Inner Loop Header: Depth=1
	lw	a0, 4(sp)
	lw	a1, 16(sp)
	bge	a0, a1, .LBB8_3
# %bb.2:                                #   in Loop: Header=BB8_1 Depth=1
	lw	a0, 24(sp)
	lw	a0, 4(a0)
	lw	a1, 4(sp)
	add	a0, a0, a1
	lw	a2, 20(sp)
	lw	a3, 8(sp)
	lb	a0, 0(a0)
	sub	a1, a1, a2
	add	a1, a3, a1
	sb	a0, 0(a1)
	lw	a0, 4(sp)
	addi	a0, a0, 1
	sw	a0, 4(sp)
	j	.LBB8_1
.LBB8_3:
	lw	a0, 16(sp)
	lw	a1, 20(sp)
	lw	a2, 8(sp)
	sub	a0, a0, a1
	add	a0, a2, a0
	sb	zero, 0(a0)
	addi	a0, zero, 12
	mv	a1, zero
	call	malloc
	lw	a1, 8(sp)
	sw	a0, 0(sp)
	sw	a1, 4(a0)
	lw	a0, 16(sp)
	lw	a1, 20(sp)
	lw	a2, 0(sp)
	sub	a0, a0, a1
	sw	a0, 0(a2)
	lw	a0, 0(sp)
	lw	ra, 28(sp)
	addi	sp, sp, 32
	ret
.Lfunc_end8:
	.size	_string_substring, .Lfunc_end8-_string_substring
	.cfi_endproc
                                        # -- End function
	.globl	_string_parseInt        # -- Begin function _string_parseInt
	.p2align	2
	.type	_string_parseInt,@function
_string_parseInt:                       # @_string_parseInt
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -32
	.cfi_def_cfa_offset 32
	sw	a0, 24(sp)
	sw	zero, 20(sp)
	lw	a0, 0(a0)
	sw	a0, 16(sp)
	sw	zero, 12(sp)
	addi	a0, zero, 57
	addi	a1, zero, 48
	addi	a2, zero, 10
.LBB9_1:                                # =>This Inner Loop Header: Depth=1
	lw	a3, 12(sp)
	lw	a4, 16(sp)
	bge	a3, a4, .LBB9_5
# %bb.2:                                #   in Loop: Header=BB9_1 Depth=1
	lw	a3, 24(sp)
	lw	a3, 4(a3)
	lw	a4, 12(sp)
	add	a3, a3, a4
	lb	a3, 0(a3)
	sb	a3, 11(sp)
	blt	a0, a3, .LBB9_5
# %bb.3:                                #   in Loop: Header=BB9_1 Depth=1
	lb	a3, 11(sp)
	blt	a3, a1, .LBB9_5
# %bb.4:                                #   in Loop: Header=BB9_1 Depth=1
	lw	a3, 20(sp)
	lb	a4, 11(sp)
	mul	a3, a3, a2
	lw	a5, 12(sp)
	add	a3, a3, a4
	addi	a3, a3, -48
	sw	a3, 20(sp)
	addi	a3, a5, 1
	sw	a3, 12(sp)
	j	.LBB9_1
.LBB9_5:
	lw	a0, 20(sp)
	addi	sp, sp, 32
	ret
.Lfunc_end9:
	.size	_string_parseInt, .Lfunc_end9-_string_parseInt
	.cfi_endproc
                                        # -- End function
	.globl	_string_ord             # -- Begin function _string_ord
	.p2align	2
	.type	_string_ord,@function
_string_ord:                            # @_string_ord
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	a0, 8(sp)
	sw	a1, 4(sp)
	lw	a0, 4(a0)
	add	a0, a0, a1
	lb	a0, 0(a0)
	addi	sp, sp, 16
	ret
.Lfunc_end10:
	.size	_string_ord, .Lfunc_end10-_string_ord
	.cfi_endproc
                                        # -- End function
	.globl	_string_length          # -- Begin function _string_length
	.p2align	2
	.type	_string_length,@function
_string_length:                         # @_string_length
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	a0, 8(sp)
	lw	a0, 0(a0)
	addi	sp, sp, 16
	ret
.Lfunc_end11:
	.size	_string_length, .Lfunc_end11-_string_length
	.cfi_endproc
                                        # -- End function
	.globl	_string_getStrcmp       # -- Begin function _string_getStrcmp
	.p2align	2
	.type	_string_getStrcmp,@function
_string_getStrcmp:                      # @_string_getStrcmp
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	sw	a0, 8(sp)
	sw	a1, 0(sp)
	lw	a0, 4(a0)
	lw	a1, 4(a1)
	call	strcmp
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end12:
	.size	_string_getStrcmp, .Lfunc_end12-_string_getStrcmp
	.cfi_endproc
                                        # -- End function
	.globl	myNew                   # -- Begin function myNew
	.p2align	2
	.type	myNew,@function
myNew:                                  # @myNew
	.cfi_startproc
# %bb.0:
	addi	sp, sp, -16
	.cfi_def_cfa_offset 16
	sw	ra, 12(sp)
	.cfi_offset ra, -4
	sw	a1, 4(sp)
	sw	a0, 0(sp)
	call	malloc
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end13:
	.size	myNew, .Lfunc_end13-myNew
	.cfi_endproc
                                        # -- End function
	.type	.L.str,@object          # @.str
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"%s"
	.size	.L.str, 3

	.type	.L.str.1,@object        # @.str.1
.L.str.1:
	.asciz	"%s\n"
	.size	.L.str.1, 4

	.type	.L.str.2,@object        # @.str.2
.L.str.2:
	.asciz	"%d"
	.size	.L.str.2, 3

	.type	.L.str.3,@object        # @.str.3
.L.str.3:
	.asciz	"%d\n"
	.size	.L.str.3, 4

	.section	".note.GNU-stack","",@progbits

