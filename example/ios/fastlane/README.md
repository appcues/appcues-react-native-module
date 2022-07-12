fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

## iOS

### ios compile_example

```sh
[bundle exec] fastlane ios compile_example
```

Sanity check to make sure the example app compiles

### ios prep_match

```sh
[bundle exec] fastlane ios prep_match
```

Setup code signing

### ios deploy_example

```sh
[bundle exec] fastlane ios deploy_example
```

Push example app to Testflight beta

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
