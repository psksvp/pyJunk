; ModuleID = '/Users/psksvp/MyCode/pyJunk/test_for_loop_sum.c'
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-macosx10.12.0"

; Function Attrs: nounwind ssp uwtable
define void @__VERIFIER_assert(i32 %cond) #0 {
entry:
  %cond.addr = alloca i32, align 4
  store i32 %cond, i32* %cond.addr, align 4
  call void @llvm.dbg.declare(metadata i32* %cond.addr, metadata !18, metadata !19), !dbg !20
  %0 = load i32, i32* %cond.addr, align 4, !dbg !21
  %tobool = icmp ne i32 %0, 0, !dbg !23
  br i1 %tobool, label %if.end, label %if.then, !dbg !24

if.then:                                          ; preds = %entry
  br label %ERROR, !dbg !25

ERROR:                                            ; preds = %if.then
  call void (...) @__VERIFIER_error() #3, !dbg !26
  unreachable, !dbg !26

if.end:                                           ; preds = %entry
  ret void, !dbg !28
}

; Function Attrs: nounwind readnone
declare void @llvm.dbg.declare(metadata, metadata, metadata) #1

; Function Attrs: noreturn
declare void @__VERIFIER_error(...) #2

; Function Attrs: nounwind ssp uwtable
define i32 @main(i32 %argc, i8** %arg) #0 {
entry:
  %retval = alloca i32, align 4
  %argc.addr = alloca i32, align 4
  %arg.addr = alloca i8**, align 8
  %a = alloca i32, align 4
  %i = alloca i32, align 4
  store i32 0, i32* %retval
  store i32 %argc, i32* %argc.addr, align 4
  call void @llvm.dbg.declare(metadata i32* %argc.addr, metadata !29, metadata !19), !dbg !30
  store i8** %arg, i8*** %arg.addr, align 8
  call void @llvm.dbg.declare(metadata i8*** %arg.addr, metadata !31, metadata !19), !dbg !32
  call void @llvm.dbg.declare(metadata i32* %a, metadata !33, metadata !19), !dbg !34
  store i32 0, i32* %a, align 4, !dbg !34
  call void @llvm.dbg.declare(metadata i32* %i, metadata !35, metadata !19), !dbg !37
  store i32 1, i32* %i, align 4, !dbg !37
  br label %for.cond, !dbg !38

for.cond:                                         ; preds = %for.inc, %entry
  %0 = load i32, i32* %i, align 4, !dbg !39
  %cmp = icmp sle i32 %0, 10, !dbg !41
  br i1 %cmp, label %for.body, label %for.end, !dbg !42

for.body:                                         ; preds = %for.cond
  %1 = load i32, i32* %a, align 4, !dbg !43
  %add = add nsw i32 %1, 1, !dbg !45
  store i32 %add, i32* %a, align 4, !dbg !46
  br label %for.inc, !dbg !47

for.inc:                                          ; preds = %for.body
  %2 = load i32, i32* %i, align 4, !dbg !48
  %inc = add nsw i32 %2, 1, !dbg !48
  store i32 %inc, i32* %i, align 4, !dbg !48
  br label %for.cond, !dbg !49

for.end:                                          ; preds = %for.cond
  %3 = load i32, i32* %a, align 4, !dbg !50
  %cmp1 = icmp eq i32 %3, 10, !dbg !51
  %conv = zext i1 %cmp1 to i32, !dbg !51
  call void @__VERIFIER_assert(i32 %conv), !dbg !52
  ret i32 0, !dbg !53
}

attributes #0 = { nounwind ssp uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="core2" "target-features"="+cx16,+sse,+sse2,+sse3,+ssse3" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { nounwind readnone }
attributes #2 = { noreturn "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="core2" "target-features"="+cx16,+sse,+sse2,+sse3,+ssse3" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #3 = { noreturn }

!llvm.dbg.cu = !{!0}
!llvm.module.flags = !{!14, !15, !16}
!llvm.ident = !{!17}

!0 = distinct !DICompileUnit(language: DW_LANG_C99, file: !1, producer: "clang version 3.7.1 (tags/RELEASE_371/final)", isOptimized: false, runtimeVersion: 0, emissionKind: 1, enums: !2, subprograms: !3)
!1 = !DIFile(filename: "/Users/psksvp/MyCode/pyJunk/test_for_loop_sum.c", directory: "/Users/psksvp/MyCode/skink")
!2 = !{}
!3 = !{!4, !8}
!4 = !DISubprogram(name: "__VERIFIER_assert", scope: !1, file: !1, line: 3, type: !5, isLocal: false, isDefinition: true, scopeLine: 3, flags: DIFlagPrototyped, isOptimized: false, function: void (i32)* @__VERIFIER_assert, variables: !2)
!5 = !DISubroutineType(types: !6)
!6 = !{null, !7}
!7 = !DIBasicType(name: "int", size: 32, align: 32, encoding: DW_ATE_signed)
!8 = !DISubprogram(name: "main", scope: !1, file: !1, line: 11, type: !9, isLocal: false, isDefinition: true, scopeLine: 12, flags: DIFlagPrototyped, isOptimized: false, function: i32 (i32, i8**)* @main, variables: !2)
!9 = !DISubroutineType(types: !10)
!10 = !{!7, !7, !11}
!11 = !DIDerivedType(tag: DW_TAG_pointer_type, baseType: !12, size: 64, align: 64)
!12 = !DIDerivedType(tag: DW_TAG_pointer_type, baseType: !13, size: 64, align: 64)
!13 = !DIBasicType(name: "char", size: 8, align: 8, encoding: DW_ATE_signed_char)
!14 = !{i32 2, !"Dwarf Version", i32 2}
!15 = !{i32 2, !"Debug Info Version", i32 3}
!16 = !{i32 1, !"PIC Level", i32 2}
!17 = !{!"clang version 3.7.1 (tags/RELEASE_371/final)"}
!18 = !DILocalVariable(tag: DW_TAG_arg_variable, name: "cond", arg: 1, scope: !4, file: !1, line: 3, type: !7)
!19 = !DIExpression()
!20 = !DILocation(line: 3, column: 28, scope: !4)
!21 = !DILocation(line: 4, column: 9, scope: !22)
!22 = distinct !DILexicalBlock(scope: !4, file: !1, line: 4, column: 7)
!23 = !DILocation(line: 4, column: 8, scope: !22)
!24 = !DILocation(line: 4, column: 7, scope: !4)
!25 = !DILocation(line: 4, column: 16, scope: !22)
!26 = !DILocation(line: 5, column: 12, scope: !27)
!27 = distinct !DILexicalBlock(scope: !22, file: !1, line: 4, column: 16)
!28 = !DILocation(line: 7, column: 3, scope: !4)
!29 = !DILocalVariable(tag: DW_TAG_arg_variable, name: "argc", arg: 1, scope: !8, file: !1, line: 11, type: !7)
!30 = !DILocation(line: 11, column: 14, scope: !8)
!31 = !DILocalVariable(tag: DW_TAG_arg_variable, name: "arg", arg: 2, scope: !8, file: !1, line: 11, type: !11)
!32 = !DILocation(line: 11, column: 27, scope: !8)
!33 = !DILocalVariable(tag: DW_TAG_auto_variable, name: "a", scope: !8, file: !1, line: 13, type: !7)
!34 = !DILocation(line: 13, column: 7, scope: !8)
!35 = !DILocalVariable(tag: DW_TAG_auto_variable, name: "i", scope: !36, file: !1, line: 14, type: !7)
!36 = distinct !DILexicalBlock(scope: !8, file: !1, line: 14, column: 3)
!37 = !DILocation(line: 14, column: 11, scope: !36)
!38 = !DILocation(line: 14, column: 7, scope: !36)
!39 = !DILocation(line: 14, column: 18, scope: !40)
!40 = distinct !DILexicalBlock(scope: !36, file: !1, line: 14, column: 3)
!41 = !DILocation(line: 14, column: 20, scope: !40)
!42 = !DILocation(line: 14, column: 3, scope: !36)
!43 = !DILocation(line: 16, column: 9, scope: !44)
!44 = distinct !DILexicalBlock(scope: !40, file: !1, line: 15, column: 3)
!45 = !DILocation(line: 16, column: 11, scope: !44)
!46 = !DILocation(line: 16, column: 7, scope: !44)
!47 = !DILocation(line: 17, column: 3, scope: !44)
!48 = !DILocation(line: 14, column: 28, scope: !40)
!49 = !DILocation(line: 14, column: 3, scope: !40)
!50 = !DILocation(line: 18, column: 21, scope: !8)
!51 = !DILocation(line: 18, column: 23, scope: !8)
!52 = !DILocation(line: 18, column: 3, scope: !8)
!53 = !DILocation(line: 19, column: 3, scope: !8)
