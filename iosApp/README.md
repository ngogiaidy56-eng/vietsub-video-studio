# iOS launcher

Create an Xcode iOS App target named `iosApp`, then link the generated `composeApp` framework and call `MainViewController()` from `ContentView.swift`.

The project intentionally keeps signing/provisioning out of source control; Fastlane/Match owns certificates in CI.
