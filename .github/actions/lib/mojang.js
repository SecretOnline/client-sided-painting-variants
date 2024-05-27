/**
 * @typedef AllVersionsManifest
 * @property {{release:string;snapshot:string}} latest
 * @property {AllVersionsManifestVersion[]} versions
 */
/**
 * @typedef AllVersionsManifestVersion
 * @property {string} id
 * @property {'release'|'snapshot'} type
 * @property {string} url
 * @property {string} time
 * @property {string} releaseTime
 * @property {string} sha1
 * @property {number} complianceLevel
 */

/**
 * @returns {Promise<AllVersionsManifest>}
 */
export async function getAllMinecraftVersions() {
  const response = await fetch(
    "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json"
  );
  const data = await response.json();
  return data;
}
