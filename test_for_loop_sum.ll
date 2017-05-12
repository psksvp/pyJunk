; ModuleID = '../pyJunk/test_for_loop_sum.c'
source_filename = "../pyJunk/test_for_loop_sum.c"
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-macosx10.12.0"

; Function Attrs: nounwind ssp uwtable
define void @__VERIFIER_assert(i32) #0 !dbg !7 {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4, !dbg !9
  %4 = icmp ne i32 %3, 0, !dbg !10
  br i1 %4, label %7, label %5, !dbg !11

; <label>:5:                                      ; preds = %1
  br label %6, !dbg !12

; <label>:6:                                      ; preds = %5
  call void (...) @__VERIFIER_error() #2, !dbg !14
  unreachable, !dbg !14

; <label>:7:                                      ; preds = %1
  ret void, !dbg !15
}

; Function Attrs: noreturn
declare void @__VERIFIER_error(...) #1

; Function Attrs: nounwind ssp uwtable
define i32 @main(i32, i8**) #0 !dbg !16 {
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i8**, align 8
  %6 = alloca i32, align 4
  %7 = alloca i32, align 4
  store i32 0, i32* %3, align 4
  store i32 %0, i32* %4, align 4
  store i8** %1, i8*** %5, align 8
  store i32 0, i32* %6, align 4, !dbg !17
  store i32 1, i32* %7, align 4, !dbg !18
  br label %8, !dbg !19

; <label>:8:                                      ; preds = %14, %2
  %9 = load i32, i32* %7, align 4, !dbg !20
  %10 = icmp sle i32 %9, 10, !dbg !22
  br i1 %10, label %11, label %17, !dbg !23

; <label>:11:                                     ; preds = %8
  %12 = load i32, i32* %6, align 4, !dbg !24
  %13 = add nsw i32 %12, 1, !dbg !25
  store i32 %13, i32* %6, align 4, !dbg !26
  br label %14, !dbg !27

; <label>:14:                                     ; preds = %11
  %15 = load i32, i32* %7, align 4, !dbg !28
  %16 = add nsw i32 %15, 1, !dbg !28
  store i32 %16, i32* %7, align 4, !dbg !28
  br label %8, !dbg !30, !llvm.loop !31

; <label>:17:                                     ; preds = %8
  %18 = load i32, i32* %6, align 4, !dbg !33
  %19 = icmp eq i32 %18, 10, !dbg !34
  %20 = zext i1 %19 to i32, !dbg !34
  call void @__VERIFIER_assert(i32 %20), !dbg !35
  ret i32 0, !dbg !36
}

attributes #0 = { nounwind ssp uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { noreturn "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { noreturn }

!llvm.dbg.cu = !{!0}
!llvm.module.flags = !{!3, !4, !5}
!llvm.ident = !{!6}

!0 = distinct !DICompileUnit(language: DW_LANG_C99, file: !1, producer: "Apple LLVM version 8.1.0 (clang-802.0.42)", isOptimized: false, runtimeVersion: 0, emissionKind: LineTablesOnly, enums: !2)
!1 = !DIFile(filename: "../pyJunk/test_for_loop_sum.c", directory: "/Users/psksvp/MyCode/skink")
!2 = !{}
!3 = !{i32 2, !"Dwarf Version", i32 4}
!4 = !{i32 2, !"Debug Info Version", i32 700000003}
!5 = !{i32 1, !"PIC Level", i32 2}
!6 = !{!"Apple LLVM version 8.1.0 (clang-802.0.42)"}
!7 = distinct !DISubprogram(name: "__VERIFIER_assert", scope: !1, file: !1, line: 3, type: !8, isLocal: false, isDefinition: true, scopeLine: 3, flags: DIFlagPrototyped, isOptimized: false, unit: !0, variables: !2)
!8 = !DISubroutineType(types: !2)
!9 = !DILocation(line: 4, column: 9, scope: !7)
!10 = !DILocation(line: 4, column: 8, scope: !7)
!11 = !DILocation(line: 4, column: 7, scope: !7)
!12 = !DILocation(line: 4, column: 16, scope: !13)
!13 = !DILexicalBlockFile(scope: !7, file: !1, discriminator: 1)
!14 = !DILocation(line: 5, column: 12, scope: !7)
!15 = !DILocation(line: 7, column: 3, scope: !7)
!16 = distinct !DISubprogram(name: "main", scope: !1, file: !1, line: 11, type: !8, isLocal: false, isDefinition: true, scopeLine: 12, flags: DIFlagPrototyped, isOptimized: false, unit: !0, variables: !2)
!17 = !DILocation(line: 13, column: 7, scope: !16)
!18 = !DILocation(line: 14, column: 11, scope: !16)
!19 = !DILocation(line: 14, column: 7, scope: !16)
!20 = !DILocation(line: 14, column: 18, scope: !21)
!21 = !DILexicalBlockFile(scope: !16, file: !1, discriminator: 1)
!22 = !DILocation(line: 14, column: 20, scope: !21)
!23 = !DILocation(line: 14, column: 3, scope: !21)
!24 = !DILocation(line: 16, column: 9, scope: !16)
!25 = !DILocation(line: 16, column: 11, scope: !16)
!26 = !DILocation(line: 16, column: 7, scope: !16)
!27 = !DILocation(line: 17, column: 3, scope: !16)
!28 = !DILocation(line: 14, column: 28, scope: !29)
!29 = !DILexicalBlockFile(scope: !16, file: !1, discriminator: 2)
!30 = !DILocation(line: 14, column: 3, scope: !29)
!31 = distinct !{!31, !32}
!32 = !DILocation(line: 14, column: 3, scope: !16)
!33 = !DILocation(line: 18, column: 21, scope: !16)
!34 = !DILocation(line: 18, column: 23, scope: !16)
!35 = !DILocation(line: 18, column: 3, scope: !16)
!36 = !DILocation(line: 19, column: 3, scope: !16)
