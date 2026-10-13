# Converts the cubes of a simple GeckoLib/Bedrock item model (one bone, no rotations, box UV) into the
# "elements" of a vanilla Java item model, the way GeoItemRenderer would place them.
#
# Used for the Enchanter's Gauntlet and the Enchanter's Fishing Rod (see HANDOFF.md, "Conversion des modèles").
#
# Usage (Git Bash):
#   awk -v CUBES="ox,oy,oz,sx,sy,sz,u,v;..." -v TEX="ars_backports:item/<texture>" -v TW=32 -f tools/geo_to_vanilla_model.awk
#   - one "origin(3) size(3) uv(2)" group per cube, copied from the .geo.json "cubes" array;
#   - TW = texture width in pixels (texture_width of the .geo.json, square textures only).
# The output stops after "elements": append the "display" block of the official models/item/<item>.json
# (and "overrides" if needed), then close the object.
#
# Conversion rules:
#   - Bedrock X is mirrored: java x = 8 - (ox + sx) .. 8 - ox ; z = 8 + oz ; y = 8.16 + oy
#     (GeoItemRenderer translates the model by (0.5, 0.51, 0.5) block units).
#   - Box UV layout (pixels): east (u, v+sz), north (u+sz, v+sz), west (u+sz+sx, v+sz), south (u+2sz+sx, v+sz),
#     each sy high; up (u+sz..u+sz+sx, v..v+sz) flipped on both axes; down (u+sz+sx..u+sz+2sx, v..v+sz) flipped on U.
#   - Vanilla UVs are in 0..16 units: pixel / (TW / 16). Zero-area faces (flat planes) are skipped.

function face(name, u1, v1, u2, v2) {
    if (u2 - u1 == 0 || v2 - v1 == 0) return
    out[nf++] = sprintf("        \"%s\": {\"uv\": [%g, %g, %g, %g], \"texture\": \"#0\"}", name, u1 / k, v1 / k, u2 / k, v2 / k)
}

BEGIN {
    k = (TW ? TW : 32) / 16
    n = split(CUBES, C, ";")
    printf "{\n  \"texture_size\": [%d, %d],\n  \"textures\": {\n    \"0\": \"%s\",\n    \"particle\": \"%s\"\n  },\n  \"elements\": [\n", k * 16, k * 16, TEX, TEX
    for (i = 1; i <= n; i++) {
        split(C[i], a, ",")
        ox = a[1]; oy = a[2]; oz = a[3]; sx = a[4]; sy = a[5]; sz = a[6]; u = a[7]; v = a[8]; nf = 0
        face("north", u + sz, v + sz, u + sz + sx, v + sz + sy)
        face("east", u, v + sz, u + sz, v + sz + sy)
        face("south", u + 2 * sz + sx, v + sz, u + 2 * sz + 2 * sx, v + sz + sy)
        face("west", u + sz + sx, v + sz, u + 2 * sz + sx, v + sz + sy)
        if (sx != 0 && sz != 0) {
            out[nf++] = sprintf("        \"up\": {\"uv\": [%g, %g, %g, %g], \"texture\": \"#0\"}", (u + sz + sx) / k, (v + sz) / k, (u + sz) / k, v / k)
            out[nf++] = sprintf("        \"down\": {\"uv\": [%g, %g, %g, %g], \"texture\": \"#0\"}", (u + sz + 2 * sx) / k, v / k, (u + sz + sx) / k, (v + sz) / k)
        }
        printf "    {\n      \"from\": [%g, %g, %g],\n      \"to\": [%g, %g, %g],\n      \"faces\": {\n", 8 - (ox + sx), 8.16 + oy, 8 + oz, 8 - ox, 8.16 + oy + sy, 8 + oz + sz
        for (j = 0; j < nf; j++) printf "%s%s\n", out[j], (j < nf - 1 ? "," : "")
        printf "      }\n    }%s\n", (i < n ? "," : "")
    }
    printf "  ],\n"
}
