# more-paintings-on-the-client

## For resource/data pack developers

Players with this mod installed can have your paintings added into the pool of available paintings wherever they go with your resource pack, and not just on servers with the data pack. Since this mod doesn't remove any of Minecraft's default paintings, it's a nice non-destructive way to have your artwork integrated into the game.

In order to make your resource pack work, all you need to do is copy the `data/<namespace>/painting_variants/` folder with all the JSON files from your _data_ pack, and move it to `assets/<namespace>/painting_variants/` in your _resource_ pack. Done!

I'd love to make this a no-effort thing for pack developers (which would also have the benefit of working out-of-the-box), but unfortunately the resource/data pack split makes the ideal version of this project impossible. Instead, I think this is the next best thing.
