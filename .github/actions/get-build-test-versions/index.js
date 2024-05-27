import { setOutput } from "@actions/core";
import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { satisfies } from "semver";
import { getAllMinecraftVersions } from "../lib/mojang.js";

const fileString = await readFile(
  join(process.cwd(), "src/main/resources", "fabric.mod.json"),
  { encoding: "utf8" }
);
const modJson = JSON.parse(fileString);
const knownVersionsRange = modJson.recommends.minecraft;

const versionsManifest = await getAllMinecraftVersions();

const matchingVersions = versionsManifest.versions.filter(
  (version) =>
    version.type === "release" && satisfies(version.id, knownVersionsRange)
);

const versionEntries = await Promise.all(
  matchingVersions.map(async (version) => {
    const data = await (await fetch(version.url)).json();

    /** @type {TestMatrixEntry} */
    const entry = {
      name: data.id,
      "minecraft-version": data.id,
      "java-version": data.javaVersion.majorVersion,
    };

    return entry;
  })
);

setOutput("test-matrix", JSON.stringify(versionEntries));

/**
 * @typedef TestMatrixEntry
 * @property {string} name
 * @property {string} minecraft-version
 * @property {string} java-version
 */
