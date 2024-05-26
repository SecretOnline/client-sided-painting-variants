import { randomUUID } from "node:crypto";
import { appendFile, readFile } from "node:fs/promises";
import { EOL } from "node:os";
import { join } from "node:path";

// Guess which is easier:
// 1. Use ncc to bundle this file with its dependencies
// 2. Write your own implementation of get/set output
// The answer is 1, but here's 2 anyway.
// Make sure to keep in sync with other actions that use JS.

/**
 * Roughly based on original implementation
 * https://github.com/actions/toolkit/blob/415c42d27ca2a24f3801dd9406344aaea00b7866/packages/core/src/core.ts#L126
 * @param {string} name
 * @param {{ required?: boolean; trimWhitespace?: boolean }} options
 * @returns {string}
 */
function getInput(name, options) {
  const val =
    process.env[`INPUT_${name.replace(/ /g, "_").toUpperCase()}`] || "";
  if (options && options.required && !val) {
    throw new Error(`Input required and not supplied: ${name}`);
  }

  if (options && options.trimWhitespace === false) {
    return val;
  }

  return val.trim();
}

/**
 * Roughly based on original implementation
 * https://github.com/actions/toolkit/blob/415c42d27ca2a24f3801dd9406344aaea00b7866/packages/core/src/core.ts#L192C1-L200C2
 * @param {string} name
 * @param {string} value
 * @returns {Promise<void>}
 */
async function setOutput(name, value) {
  const filePath = process.env["GITHUB_OUTPUT"] || "";
  if (!filePath) {
    throw new Error(
      "Command mode outputs are not supported by this implementation because it's too hard"
    );
  }

  const delimiter = `EOL_${randomUUID()}`;
  await appendFile(
    filePath,
    `${name}<<${delimiter}${EOL}${value}${EOL}${delimiter}${EOL}`,
    { encoding: "utf8" }
  );
}

/**
 * @param {string} version
 * @returns {[number, number, number]}
 */
function versionToNumbers(version) {
  const match = version.match(/(\d+)\.(\d+)(?:\.(\d+))?/);
  if (!match) {
    throw new Error(`Invalid version ${version}`);
  }

  return [
    Number.parseInt(match[1], 10),
    Number.parseInt(match[2], 10),
    Number.parseInt(match[3] ?? "0", 10),
  ];
}

/**
 * @param {string} version
 * @param {string} range
 * @returns {boolean}
 */
function matchSingleRange(version, range) {
  let match;
  match = range.match(/^(\d+\.\d+(?:\.\d+)?)$/);
  if (match) {
    const singleVersion = match[1];

    if (version === singleVersion) {
      return true;
    }
    return false;
  }

  match = range.match(/^(\d+\.\d+(?:\.\d+)?) - (\d+\.\d+(?:\.\d+)?)$/);
  if (match) {
    const firstVersion = versionToNumbers(match[1]);
    const lastVersion = versionToNumbers(match[2]);
    const matchingVersion = versionToNumbers(version);

    if (firstVersion[0] === lastVersion[0]) {
      if (firstVersion[1] === lastVersion[1]) {
        if (firstVersion[2] === lastVersion[2]) {
          return (
            matchingVersion[0] === firstVersion[0] &&
            matchingVersion[1] === firstVersion[1] &&
            matchingVersion[2] === firstVersion[2]
          );
        } else {
          return (
            matchingVersion[0] === firstVersion[0] &&
            matchingVersion[1] === firstVersion[1] &&
            matchingVersion[2] >= firstVersion[2] &&
            matchingVersion[2] <= lastVersion[2]
          );
        }
      } else {
        return (
          matchingVersion[0] === firstVersion[0] &&
          matchingVersion[1] >= firstVersion[1] &&
          matchingVersion[1] <= lastVersion[1]
        );
      }
    } else {
      return (
        matchingVersion[0] >= firstVersion[0] &&
        matchingVersion[0] <= lastVersion[0]
      );
    }
  }

  throw new Error(`Unknown version range type: ${range}`);
}

/**
 * @param {string} version
 * @param {string} range
 * @returns {boolean}
 */
function matchVersion(version, range) {
  const ranges = range.split("||").map((s) => s.trim());
  return !!ranges.find((r) => matchSingleRange(version, r));
}

const fileString = await readFile(
  join(process.cwd(), "src/main/resources", "fabric.mod.json")
);
const modJson = JSON.parse(fileString);
const knownVersionsRange = modJson.recommends.minecraft;

const versionsManifest = await (
  await fetch("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")
).json();

const matchingVersions = versionsManifest.versions.filter(
  (version) =>
    version.type === "release" && matchVersion(version.id, knownVersionsRange)
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

await setOutput("test-matrix", JSON.stringify(versionEntries));

/**
 * @typedef TestMatrixEntry
 * @property {string} name
 * @property {string} minecraft-version
 * @property {string} java-version
 */
