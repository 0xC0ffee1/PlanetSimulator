#version 150

#define saturate(a) clamp( a, 0.0, 1.0 )

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;
uniform sampler2D depthTexture;

uniform vec3 planetCentre;
uniform float planetRadius;
uniform float atmosphereRadius;
uniform vec3 cameraViewDir;

uniform mat4 inverseProjection;
uniform mat4 inverseView;

uniform vec3 worldSpaceCameraPos;
uniform vec3 dirToSun;

const float maxFloat = 3.402823466e+38;
const float near = 1.0;
const float far = 35000000000.0;

vec3 _ScreenToWorld(vec3 pos) {
    vec4 posP = vec4(pos.xyz * 2.0 - 1.0, 1.0);
    vec4 posVS = inverseProjection * posP;
    vec4 posWS = inverseView * vec4((posVS.xyz / posVS.w), 1.0);
    return posWS.xyz;
}

vec2 raySphere(vec3 sphereCentre, float sphereRadius, vec3 rayOrigin, vec3 rayDir){
    vec3 offset = rayOrigin - sphereCentre;
    float a = 1; // Set to dot(rayDir, rayDir) if rayDir might not be normalized
    float b = 2 * dot(offset, rayDir);
    float c = dot (offset, offset) - sphereRadius * sphereRadius;
    float d = b * b - 4 * a * c; // Discriminant from quadratic formula

    // Number of intersections: 0 when d < 0; 1 when d = 0; 2 when d > 0
    if (d > 0) {
        float s = sqrt(d);
        float dstToSphereNear = max(0, (-b - s) / (2 * a));
        float dstToSphereFar = (-b + s) / (2 * a);

        // Ignore intersections that occur behind the ray
        if (dstToSphereFar >= 0) {
            return vec2(dstToSphereNear, dstToSphereFar - dstToSphereNear);
        }
    }
    // Ray did not intersect sphere
    return vec2(maxFloat, 0);
}


float linearize_depth(in float depth){
    float a = far / (far - near);
    float b = far * near / (near - far);
    return a + b / depth;
}

float reconstruct_depth(){
    float depth = texture(depthTexture, textureCoords).r;
    return pow(2.0, depth * log2(far + 1.0)) - 1.0;
}

vec3 reconstruct_world_pos(){
    vec4 wpos = inverseView * inverseProjection *
    (vec4(textureCoords, linearize_depth(reconstruct_depth()), 1.0) * 2.0 - 1.0);
    return wpos.xyz / wpos.w;
}


void main(){
    vec3 viewVector = (inverseProjection * vec4(textureCoords.xy * 2 - 1, 0, 1)).xyz;
    viewVector = (inverseView * vec4(viewVector, 0)).xyz;
    vec4 originalCol = texture(colourTexture, textureCoords);
    vec4 depthNonLinear = texture(depthTexture, textureCoords);
    float floorDistance = linearize_depth(reconstruct_depth());

    vec3 viewRay = reconstruct_world_pos();
    float sceneDepth = floorDistance * length(viewRay);
    vec3 rayDir = normalize(viewRay - worldSpaceCameraPos);

    vec2 hitInfo = raySphere(planetCentre, planetRadius+500, worldSpaceCameraPos, rayDir);

    float dstToOcean = hitInfo.x;
    float dstThroughOcean = hitInfo.y;

    float oceanViewDepth = min(dstThroughOcean, sceneDepth - dstToOcean);

    if(oceanViewDepth > 0){
        float opticalDepth01 = 1 - exp(-oceanViewDepth * 0.01);
        float alpha = 1 - exp(-oceanViewDepth * 0.1);
        vec3 oceanNormal = normalize(worldSpaceCameraPos + rayDir * dstToOcean);
        float specularAngle = acos(dot(normalize(dirToSun - rayDir), oceanNormal));
        float specularExponent = specularAngle / (1-0.95);
        float specularHighlight = exp(-specularExponent * specularExponent);
        float diffuseLighting = saturate(dot(oceanNormal, dirToSun));

        vec4 oceanCol = mix(vec4(0,0.41 * 1,0.58 * 1, 1), vec4(0,0.122 * 1,0.3 * 1, 1), opticalDepth01) * diffuseLighting + specularHighlight;
        out_Colour = mix(originalCol, oceanCol, alpha);
        return;
    }
    out_Colour = originalCol;
}
