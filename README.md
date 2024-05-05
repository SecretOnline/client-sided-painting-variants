# More paintings on the client

A mod that allows you to add new types of painting without removing any existing paintings.

This mod requires [Fabric API](https://modrinth.com/mod/fabric-api), so make sure it's installed with this mod.

## Caveats

As this is a client-side mod with no interaction with the server, this mod does not add new sizes of painting. It can only add new artwork for existing sizes.

## For resource/data pack developers

Players with this mod installed can have your paintings added into the pool of available paintings wherever they go with your resource pack, and not just on servers with the data pack. Since this mod doesn't remove any of Minecraft's default paintings, it's a nice non-destructive way to have your artwork integrated into the game.

There are only two requirements for this mod to work.

1. Ensure your painting textures are in your resource pack's `assets/<namespace>/textures/painting/` folder (where the game's default paintings are, so hopefully this is where you've put your painting textures too).
2. Add the painting variant JSON files to your resource pack's `assets/<namespace>/painting_variants/` folder.
   - These are the same format as adding painting variants in a data pack.
     - `width`: Width of the painting in blocks. Must be in the range of 1-16.
     - `height`: Height of the painting in blocks. Must be in the range of 1-16.
     - `asset_id`: Identifier of the texture in the `paintings` atlas. `<namespace>/<filename-without-ext>`
   - Ref: [24w18a changelog](https://www.minecraft.net/en-us/article/minecraft-snapshot-24w18a).

I'd love to make this a no-effort thing for pack developers (which would also have the benefit of working out-of-the-box), but unfortunately the resource/data pack split makes the ideal version of this project impossible. Instead, I think this is the next best thing.
