# Implementation Plan - Fix Build Errors and Warnings

This plan aims to resolve the build errors related to dependency version mismatches and fix various IDE warnings across the project's Java files without changing any functional logic.

## User Review Required

> [!IMPORTANT]
> I will be updating the `compileSdk` to 36 and the Android Gradle Plugin (AGP) to 8.9.1 to resolve build errors. This is necessary because some of your dependencies (like `androidx.core:core:1.18.0`) require these versions.

## Proposed Changes

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/Satyam upadhyaya/Desktop/login/app/build.gradle.kts)
- Update `compileSdk` to 36.

#### [MODIFY] [gradle/libs.versions.toml](file:///C:/Users/Satyam upadhyaya/Desktop/login/gradle/libs.versions.toml)
- Update `agp` version to `8.9.1`.

### Java Code Cleanup (All Files)
I will systematically go through all Java files and apply the following fixes where applicable:
- Remove unused imports.
- Convert anonymous classes (like `Comparator` or `View.OnClickListener`) to lambdas.
- Convert statement lambdas to expression lambdas where possible.
- Fix redundant type arguments (use diamond operator `<>`).
- Fix `Collections.sort` to `List.sort`.
- Replace deprecated `overridePendingTransition` with newer API if applicable (or suppress if trivial replacement isn't available).
- Address "Field can be local" warnings.
- Fix `if` statements that can be replaced with `Objects.requireNonNullElse()`.

## Verification Plan

### Automated Tests
- Run `:app:assembleDebug` to ensure the project builds successfully.
- Run `analyze_file` on key files to verify warnings are gone.

### Manual Verification
- None required as logic remains unchanged.
