; ModuleID = '../pyJunk/test_loop_dec_do_nondet.c'
source_filename = "../pyJunk/test_loop_dec_do_nondet.c"
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-macosx10.12.0"

; Function Attrs: nounwind ssp uwtable
define void @__VERIFIER_assert(i32) local_unnamed_addr #0 !dbg !7 {
  %2 = icmp eq i32 %0, 0, !dbg !9
  br i1 %2, label %3, label %4, !dbg !10

; <label>:3:                                      ; preds = %1
  tail call void (...) @__VERIFIER_error() #3, !dbg !11
  unreachable, !dbg !11

; <label>:4:                                      ; preds = %1
  ret void, !dbg !12
}

; Function Attrs: noreturn
declare void @__VERIFIER_error(...) local_unnamed_addr #1

; Function Attrs: nounwind ssp uwtable
define i32 @main() local_unnamed_addr #0 !dbg !13 {
  %1 = tail call i32 (...) @__VERIFIER_nondet_uint() #4, !dbg !14
  %2 = sub i32 -2, %1, !dbg !15
  %3 = icmp slt i32 %2, -1, !dbg !16
  br i1 %3, label %5, label %4, !dbg !17

; <label>:4:                                      ; preds = %0
  tail call void (...) @__VERIFIER_error() #3, !dbg !19
  unreachable, !dbg !19

; <label>:5:                                      ; preds = %0
  ret i32 0, !dbg !20
}

declare i32 @__VERIFIER_nondet_uint(...) local_unnamed_addr #2

attributes #0 = { nounwind ssp uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { noreturn "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #3 = { noreturn nounwind }
attributes #4 = { nounwind }

!llvm.dbg.cu = !{!0}
!llvm.module.flags = !{!3, !4, !5}
!llvm.ident = !{!6}

!0 = distinct !DICompileUnit(language: DW_LANG_C99, file: !1, producer: "Apple LLVM version 8.1.0 (clang-802.0.42)", isOptimized: true, runtimeVersion: 0, emissionKind: LineTablesOnly, enums: !2)
!1 = !DIFile(filename: "../pyJunk/test_loop_dec_do_nondet.c", directory: "/Users/psksvp/MyCode/skink")
!2 = !{}
!3 = !{i32 2, !"Dwarf Version", i32 4}
!4 = !{i32 2, !"Debug Info Version", i32 700000003}
!5 = !{i32 1, !"PIC Level", i32 2}
!6 = !{!"Apple LLVM version 8.1.0 (clang-802.0.42)"}
!7 = distinct !DISubprogram(name: "__VERIFIER_assert", scope: !1, file: !1, line: 3, type: !8, isLocal: false, isDefinition: true, scopeLine: 3, flags: DIFlagPrototyped, isOptimized: true, unit: !0, variables: !2)
!8 = !DISubroutineType(types: !2)
!9 = !DILocation(line: 4, column: 8, scope: !7)
!10 = !DILocation(line: 4, column: 7, scope: !7)
!11 = !DILocation(line: 5, column: 12, scope: !7)
!12 = !DILocation(line: 7, column: 3, scope: !7)
!13 = distinct !DISubprogram(name: "main", scope: !1, file: !1, line: 12, type: !8, isLocal: false, isDefinition: true, scopeLine: 13, isOptimized: true, unit: !0, variables: !2)
!14 = !DILocation(line: 14, column: 11, scope: !13)
!15 = !DILocation(line: 16, column: 3, scope: !13)
!16 = !DILocation(line: 25, column: 23, scope: !13)
!17 = !DILocation(line: 4, column: 7, scope: !7, inlinedAt: !18)
!18 = distinct !DILocation(line: 25, column: 3, scope: !13)
!19 = !DILocation(line: 5, column: 12, scope: !7, inlinedAt: !18)
!20 = !DILocation(line: 26, column: 3, scope: !13)
