# ManagerMeet Agent Instructions

ManagerMeet is a Smithware Studios Android app. Before release or DevHub work,
keep `PROJECT_CONTEXT.md`, GitHub Releases, and DevHub metadata aligned.

## Product Rules

- Keep v1 local-first.
- Do not add login, cloud sync, paid APIs, or network upload behavior without explicit approval.
- Preserve the UI privacy promise: "Your app ideas stay on this device."
- Prefer focused manager/product-planning flows over broad project-management scope.

## Release Rules

- Build with the local Android toolchain documented in `README.md`.
- Publish Android updates as GitHub Releases with APK assets.
- DevHub detects releases from APK-backed GitHub Releases, not source pushes alone.
