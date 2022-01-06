# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html) as closely as it can.

## [3.1.0] for 1.18.1 - 2022-01-06

## Added
- Special Recipe API (Smithing only right now)
  - Allows devs to specify special (non-json-standard) recipe types for common crafting methods
    - Should only be used where a standard recipe file is not enough (e.g. armor dying, tool repair)
- Command DSL
  - Added some additional shortcut methods for common argument types
- Auto-Registration
  - Added some additional shortcut methods for common registration types

## Fixed
- Command DSL
  - Custom argument method is no longer private

## [3.0.0] for 1.18 - 2021-11-23

## Changed
- General
  - Updated to 1.18
- Text DSL
  - Changed the method to add siblings from `+` syntax to an `add*` syntax.
    - E.g. `addLiteral`, `addTranslate`, etc.
- Persistence (Experimental)
  - Total rework of experimental data persistence
  - Data is now stored per file, which can be stored in a world-specific config file, or a general config file

## [2.1.0] for 1.17.1 - 2021-11-01

### Added
- Added some math extensions for:
  - Vec3d
  - Vec3i
  - Vec3f
  - MatrixStack

### Changed
- Command DSL API
  - Command arguments now pass their parameter into their lambda function. When invoked in a command, the value can be retrieved.
- Logger API
  - Kambrik's internal logger has been moved. This should not affect anyone, but fixes (#7).

## [1.1.0] for 1.17.1 - 2021-08-28

### Added
- New Keybind API (thanks to isXander and Ejektaflex)
    - Allows devs to easily define and register keybinds
    - Allows possible realtime key updates rather than per tick
    - Allows specifying different pressed and unpressed functionality
- File API
    - getBaseFolder now returns a Folder (File) instead of a Path
    - getBaseFolderPath now returns a Path
- Serial API
    - Add an Identifier serializer
- New Persistence API (Unstable/Experimental)
    - Allows devs to easily add and access persistent data
    - Useful for config data and serverside data
    
  
## [1.0.0] for 1.17.1 - 2021-08-25
- Initial release of Kambrik
