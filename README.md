# More paintings on the client

A mod that allows you to add new types of painting without removing any existing paintings.

This mod requires [Fabric API](https://modrinth.com/mod/fabric-api), so make sure it's installed with this mod.

## Wait, how is this different from 1.21's painting variants?

1.21's painting variants rely on data packs to specify new variants, which then means that every client connected to the server gets new paintings. What this mod does is extend that idea further, allowing resource packs to define more painting variants for any players using the mod on their clients.

This does lead to the one limitation of this mod: it can't add new sizes of paintings, just add more variants to existing sizes (whether they're defined by Minecraft or a data pack).

## How does this work on multiplayer?

- Players without the mod will see the original paintings as sent by the server.
- Players with the mod will see custom paintings in addition to the original paintings.
  - If two players with the mod installed have the same resource packs installed, then they will see the same paintings.

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
