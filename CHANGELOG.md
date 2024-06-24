# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).
The versioning scheme is listed in the README.

<!-- ### Known Issues -->
<!-- ### Added -->
<!-- ### Updated -->
<!-- ### Changed -->
<!-- ### Deprecated -->
<!-- ### Removed -->
<!-- ### Fixed -->
<!-- ### Security -->

## Unreleased - DATE

Minecraft 1.x.x

## v1.1.1 - 2024-06-24

Minecraft 1.21

### Fixed

- Fix painting variants not updating when servers reloaded resource packs containing painting variants.

## v1.1.0 - 2024-06-14

### Updated

- Updated to Minecraft 1.21

## v1.0.1 - 2024-05-27

Minecraft 1.20.5 & 1.20.6

### Added

- Test resource pack included with the mod.
  - For now it only contains a 2x2 painting, but I may add other sizes later.
- Support for both 1.20.5 and 1.20.6
  - Behind the scenes I'm working on dealing with Minecraft updates automatically. This release is a test of part of that process.

### Fixed

- Incorrect key for access widener in `fabric.mod.json`

## v1.0.0 - 2024-05-09

Minecraft 1.20.6

### Added

- Initial release!
- Override painting front textures based on entity's UUID.
  - Change of replacement is dependent on how many painting variants there are.
  - The end result is that every variant (real or added) should be equally likely.
