; ModuleID = '/Users/psksvp/MyCode/pyJunk/test_loop_nested_for.clang.ll'
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-macosx10.12.0"

; Function Attrs: nounwind ssp uwtable
define void @__VERIFIER_assert(i32 %cond) #0 {
entry:
  %cond.addr = alloca i32, align 4
  store i32 %cond, i32* %cond.addr, align 4
  %0 = load i32, i32* %cond.addr, align 4, !dbg !18
  %tobool = icmp ne i32 %0, 0, !dbg !20
  br i1 %tobool, label %if.end, label %ERROR, !dbg !21

ERROR:                                            ; preds = %entry
  call void (...) @__VERIFIER_error() #3, !dbg !22
  unreachable, !dbg !22

if.end:                                           ; preds = %entry
  ret void, !dbg !24
}

; Function Attrs: noreturn
declare void @__VERIFIER_error(...) #1

; Function Attrs: nounwind ssp uwtable
define i32 @main(i32 %argc, i8** %arg) #0 {
entry:
  %cond.addr.i = alloca i32, align 4
  %retval = alloca i32, align 4
  %argc.addr = alloca i32, align 4
  %arg.addr = alloca i8**, align 8
  %a = alloca i32, align 4
  %i = alloca i32, align 4
  %j = alloca i32, align 4
  store i32 0, i32* %retval
  store i32 %argc, i32* %argc.addr, align 4
  store i8** %arg, i8*** %arg.addr, align 8
  store i32 0, i32* %a, align 4, !dbg !25
  store i32 0, i32* %i, align 4, !dbg !26
  br label %for.cond, !dbg !28

for.cond:                                         ; preds = %for.inc.5, %entry
  %0 = load i32, i32* %i, align 4, !dbg !29
  %cmp = icmp slt i32 %0, 1000, !dbg !31
  br i1 %cmp, label %for.body, label %for.end.7, !dbg !32

for.body:                                         ; preds = %for.cond
  store i32 0, i32* %j, align 4, !dbg !33
  br label %for.cond.1, !dbg !36

for.cond.1:                                       ; preds = %for.body.3, %for.body
  %1 = load i32, i32* %j, align 4, !dbg !37
  %cmp2 = icmp slt i32 %1, 1000, !dbg !39
  br i1 %cmp2, label %for.body.3, label %for.inc.5, !dbg !40

for.body.3:                                       ; preds = %for.cond.1
  %2 = load i32, i32* %a, align 4, !dbg !41
  %inc = add nsw i32 %2, 1, !dbg !41
  store i32 %inc, i32* %a, align 4, !dbg !41
  %3 = load i32, i32* %j, align 4, !dbg !43
  %inc4 = add nsw i32 %3, 1, !dbg !43
  store i32 %inc4, i32* %j, align 4, !dbg !43
  br label %for.cond.1, !dbg !44

for.inc.5:                                        ; preds = %for.cond.1
  %4 = load i32, i32* %i, align 4, !dbg !45
  %inc6 = add nsw i32 %4, 1, !dbg !45
  store i32 %inc6, i32* %i, align 4, !dbg !45
  br label %for.cond, !dbg !46

for.end.7:                                        ; preds = %for.cond
  %5 = load i32, i32* %a, align 4, !dbg !47
  %cmp8 = icmp eq i32 %5, 1000000, !dbg !48
  %conv = zext i1 %cmp8 to i32, !dbg !48
  %6 = bitcast i32* %cond.addr.i to i8*, !dbg !49
  call void @llvm.lifetime.start(i64 4, i8* %6), !dbg !49
  store i32 %conv, i32* %cond.addr.i, align 4, !dbg !49
  %7 = load i32, i32* %cond.addr.i, align 4, !dbg !50
  %tobool.i = icmp ne i32 %7, 0, !dbg !52
  br i1 %tobool.i, label %__VERIFIER_assert.exit, label %ERROR.i, !dbg !53

ERROR.i:                                          ; preds = %for.end.7
  call void (...) @__VERIFIER_error() #4, !dbg !54
  unreachable, !dbg !54

__VERIFIER_assert.exit:                           ; preds = %for.end.7
  %8 = bitcast i32* %cond.addr.i to i8*, !dbg !55
  call void @llvm.lifetime.end(i64 4, i8* %8), !dbg !55
  ret i32 0, !dbg !56
}

; Function Attrs: nounwind
declare void @llvm.lifetime.start(i64, i8* nocapture) #2

; Function Attrs: nounwind
declare void @llvm.lifetime.end(i64, i8* nocapture) #2

attributes #0 = { nounwind ssp uwtable "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="core2" "target-features"="+cx16,+sse,+sse2,+sse3,+ssse3" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { noreturn "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="core2" "target-features"="+cx16,+sse,+sse2,+sse3,+ssse3" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { nounwind }
attributes #3 = { noreturn }
attributes #4 = { noreturn nounwind }

!llvm.dbg.cu = !{!0}
!llvm.module.flags = !{!14, !15, !16}
!llvm.ident = !{!17}

!0 = distinct !DICompileUnit(language: DW_LANG_C99, file: !1, producer: "clang version 3.7.1 (tags/RELEASE_371/final)", isOptimized: false, runtimeVersion: 0, emissionKind: 1, enums: !2, subprograms: !3)
!1 = !DIFile(filename: "/Users/psksvp/MyCode/pyJunk/test_loop_nested_for.c", directory: "/Users/psksvp/MyCode/skink")
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
!18 = !DILocation(line: 4, column: 9, scope: !19)
!19 = distinct !DILexicalBlock(scope: !4, file: !1, line: 4, column: 7)
!20 = !DILocation(line: 4, column: 8, scope: !19)
!21 = !DILocation(line: 4, column: 7, scope: !4)
!22 = !DILocation(line: 5, column: 12, scope: !23)
!23 = distinct !DILexicalBlock(scope: !19, file: !1, line: 4, column: 16)
!24 = !DILocation(line: 7, column: 3, scope: !4)
!25 = !DILocation(line: 13, column: 7, scope: !8)
!26 = !DILocation(line: 14, column: 11, scope: !27)
!27 = distinct !DILexicalBlock(scope: !8, file: !1, line: 14, column: 3)
!28 = !DILocation(line: 14, column: 7, scope: !27)
!29 = !DILocation(line: 14, column: 18, scope: !30)
!30 = distinct !DILexicalBlock(scope: !27, file: !1, line: 14, column: 3)
!31 = !DILocation(line: 14, column: 20, scope: !30)
!32 = !DILocation(line: 14, column: 3, scope: !27)
!33 = !DILocation(line: 16, column: 13, scope: !34)
!34 = distinct !DILexicalBlock(scope: !35, file: !1, line: 16, column: 5)
!35 = distinct !DILexicalBlock(scope: !30, file: !1, line: 15, column: 3)
!36 = !DILocation(line: 16, column: 9, scope: !34)
!37 = !DILocation(line: 16, column: 20, scope: !38)
!38 = distinct !DILexicalBlock(scope: !34, file: !1, line: 16, column: 5)
!39 = !DILocation(line: 16, column: 22, scope: !38)
!40 = !DILocation(line: 16, column: 5, scope: !34)
!41 = !DILocation(line: 18, column: 8, scope: !42)
!42 = distinct !DILexicalBlock(scope: !38, file: !1, line: 17, column: 5)
!43 = !DILocation(line: 16, column: 31, scope: !38)
!44 = !DILocation(line: 16, column: 5, scope: !38)
!45 = !DILocation(line: 14, column: 29, scope: !30)
!46 = !DILocation(line: 14, column: 3, scope: !30)
!47 = !DILocation(line: 22, column: 21, scope: !8)
!48 = !DILocation(line: 22, column: 23, scope: !8)
!49 = !DILocation(line: 22, column: 3, scope: !8)
!50 = !DILocation(line: 4, column: 9, scope: !19, inlinedAt: !51)
!51 = distinct !DILocation(line: 22, column: 3, scope: !8)
!52 = !DILocation(line: 4, column: 8, scope: !19, inlinedAt: !51)
!53 = !DILocation(line: 4, column: 7, scope: !4, inlinedAt: !51)
!54 = !DILocation(line: 5, column: 12, scope: !23, inlinedAt: !51)
!55 = !DILocation(line: 7, column: 3, scope: !4, inlinedAt: !51)
!56 = !DILocation(line: 23, column: 3, scope: !8)
