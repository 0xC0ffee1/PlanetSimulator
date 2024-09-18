#version 150

#define saturate(a) clamp( a, 0.0, 1.0 )
#define PI 3.141592
#define PRIMARY_STEP_COUNT 32
#define LIGHT_STEP_COUNT 8

in vec2 textureCoords;

out vec4 out_Colour;

uniform float weatherClarity;

uniform sampler2D colourTexture;
uniform sampler2D depthTexture;

uniform vec3 planetCentre;
uniform float planetRadius;
uniform float atmosphereRadius;
uniform vec3 cameraViewDir;

uniform vec3 betaR;

uniform mat4 inverseProjection;
uniform mat4 inverseView;

uniform vec3 worldSpaceCameraPos;
uniform vec3 dirToSun;

const int numInScatteringPoints = 10;
const int numOpticalDepthPoints = 10;
const float intensity = 1;

const float scatterR = pow(400 / 700, 4) * 3;
const float scatterG = pow(400 / 530, 4) * 3;
const float scatterB = pow(400 / 440, 4) * 3;

const float near = 1.0;
const float far = 35000000000.0;

const vec4 scatteringCoefficients = vec4(scatterR,scatterG,scatterB,1);
const float ditherStrength = 4;
const float ditherScale = 1;
const float densityFalloff = 7;

const float maxFloat = 3.402823466e+38;

const float Fcoef = 2.0 / log2(far + 1.0);

bool _RayIntersectsSphere(
vec3 rayStart, vec3 rayDir, vec3 sphereCenter, float sphereRadius, out float t0, out float t1) {
    vec3 oc = rayStart - sphereCenter;
    float a = dot(rayDir, rayDir);
    float b = 2.0 * dot(oc, rayDir);
    float c = dot(oc, oc) - sphereRadius * sphereRadius;
    float d =  b * b - 4.0 * a * c;

    // Also skip single point of contact
    if (d <= 0.0) {
        return false;
    }

    float r0 = (-b - sqrt(d)) / (2.0 * a);
    float r1 = (-b + sqrt(d)) / (2.0 * a);

    t0 = min(r0, r1);
    t1 = max(r0, r1);

    return (t1 >= 0.0);
}

vec3 _ScreenToWorld(vec3 posS) {
    float depthValue = posS.z;
    float v_depth = pow(2.0, depthValue / (Fcoef * 0.5));
    float z_view = v_depth - 1.0;
    vec4 posCLIP = vec4(posS.xy * 2.0 - 1.0, 0.0, 1.0);
    vec4 posVS = inverseProjection * posCLIP;
    posVS = vec4(posVS.xyz / posVS.w, 1.0);
    posVS.xyz = normalize(posVS.xyz) * z_view;
    vec4 posWS = inverseView * posVS;
    return posWS.xyz;
}

float _SoftLight(float a, float b) {
    return (b < 0.5 ?
    (2.0 * a * b + a * a * (1.0 - 2.0 * b)) :
    (2.0 * a * (1.0 - b) + sqrt(a) * (2.0 * b - 1.0))
    );
}
vec3 _SoftLight(vec3 a, vec3 b) {
    return vec3(
    _SoftLight(a.x, b.x),
    _SoftLight(a.y, b.y),
    _SoftLight(a.z, b.z)
    );
}



vec3 _SampleLightRay(
vec3 origin, vec3 sunDir, float planetScale, float planetRadius, float totalRadius,
float rayleighScale, float mieScale, float absorptionHeightMax, float absorptionFalloff) {

    float t0, t1;
    _RayIntersectsSphere(origin, sunDir, planetCentre, totalRadius, t0, t1);

    float actualLightStepSize = (t1 - t0) / float(LIGHT_STEP_COUNT);
    float virtualLightStepSize = actualLightStepSize * planetScale;
    float lightStepPosition = 0.0;

    vec3 opticalDepthLight = vec3(0.0);

    for (int j = 0; j < LIGHT_STEP_COUNT; j++) {
        vec3 currentLightSamplePosition = origin + sunDir * (lightStepPosition + actualLightStepSize * 0.5);

        // Calculate the optical depths and accumulate
        float currentHeight = length(currentLightSamplePosition) - planetRadius;
        float currentOpticalDepthRayleigh = exp(-currentHeight / rayleighScale) * virtualLightStepSize;
        float currentOpticalDepthMie = exp(-currentHeight / mieScale) * virtualLightStepSize;
        float currentOpticalDepthOzone = (1.0 / cosh((absorptionHeightMax - currentHeight) / absorptionFalloff));
        currentOpticalDepthOzone *= currentOpticalDepthRayleigh * virtualLightStepSize;

        opticalDepthLight += vec3(
        currentOpticalDepthRayleigh,
        currentOpticalDepthMie,
        currentOpticalDepthOzone);

        lightStepPosition += actualLightStepSize;
    }

    return opticalDepthLight;
}



//Implemented using https://www.shadertoy.com/view/wlBXWK
void _ComputeScattering(
vec3 worldSpacePos, vec3 rayDirection, vec3 rayOrigin, vec3 sunDir,
out vec3 scatteringColour, out vec3 scatteringOpacity) {

    vec3 betaRayleigh = betaR;

    float betaMie = 21e-6;
    vec3 betaAbsorption = vec3(2.04e-5, 4.97e-5, 1.95e-6);
    float g = 0.46;
    float sunIntensity = 45.0;

    float atmosphereRadius = atmosphereRadius - planetRadius;
    float totalRadius = planetRadius + atmosphereRadius;
    float referencePlanetRadius = 6371000.0;
    float referenceAtmosphereRadius = 100000.0;
    float referenceTotalRadius = referencePlanetRadius + referenceAtmosphereRadius;
    float referenceRatio = referencePlanetRadius / referenceAtmosphereRadius;
    float scaleRatio = planetRadius / atmosphereRadius;
    float planetScale = referencePlanetRadius / planetRadius;
    float atmosphereScale = scaleRatio / referenceRatio;
    float maxDist = distance(worldSpacePos, rayOrigin);
    float rayleighScale = 8500.0 / (planetScale * atmosphereScale);
    float mieScale = 1200.0 / (planetScale * atmosphereScale);
    float absorptionHeightMax = 32000.0 * (planetScale * atmosphereScale);
    float absorptionFalloff = 3000.0 / (planetScale * atmosphereScale);
    float mu = dot(rayDirection, sunDir);
    float mumu = mu * mu;
    float gg = g * g;
    float phaseRayleigh = 3.0 / (16.0 * PI) * (1.0 + mumu);
    float phaseMie = 3.0 / (8.0 * PI) * ((1.0 - gg) * (mumu + 1.0)) / (pow(1.0 + gg - 2.0 * mu * g, 1.5) * (2.0 + gg));

    float t0, t1;
    if (!_RayIntersectsSphere(rayOrigin, rayDirection, planetCentre, totalRadius, t0, t1)) {
        scatteringOpacity = vec3(1);
        return;
    }

    t0 = max(0.0, t0);
    t1 = min(maxDist, t1);
    float actualPrimaryStepSize = (t1 - t0) / float(PRIMARY_STEP_COUNT);
    float virtualPrimaryStepSize = actualPrimaryStepSize * planetScale;
    float primaryStepPosition = 0.0;
    vec3 accumulatedRayleigh = vec3(0.0);
    vec3 accumulatedMie = vec3(0.0);
    vec3 opticalDepth = vec3(0.0);


    for (int i = 0; i < PRIMARY_STEP_COUNT; i++) {
        vec3 currentPrimarySamplePosition = rayOrigin + rayDirection * (
        primaryStepPosition + actualPrimaryStepSize * 0.5);
        float currentHeight = max(0.0, length(currentPrimarySamplePosition) - planetRadius);
        float currentOpticalDepthRayleigh = exp(-currentHeight / rayleighScale) * virtualPrimaryStepSize;
        float currentOpticalDepthMie = exp(-currentHeight / mieScale) * virtualPrimaryStepSize;

        float currentOpticalDepthOzone = (1.0 / cosh((absorptionHeightMax - currentHeight) / absorptionFalloff));
        currentOpticalDepthOzone *= currentOpticalDepthRayleigh * virtualPrimaryStepSize;
        opticalDepth += vec3(currentOpticalDepthRayleigh, currentOpticalDepthMie, currentOpticalDepthOzone);


        vec3 opticalDepthLight = _SampleLightRay(
        currentPrimarySamplePosition, sunDir,
        planetScale, planetRadius, totalRadius,
        rayleighScale, mieScale, absorptionHeightMax, absorptionFalloff);
        vec3 r = (
        betaRayleigh * (opticalDepth.x + opticalDepthLight.x) +
        betaMie * (opticalDepth.y + opticalDepthLight.y) +
        betaAbsorption * (opticalDepth.z + opticalDepthLight.z));
        vec3 attn = exp(-r);
        accumulatedRayleigh += currentOpticalDepthRayleigh * attn;
        accumulatedMie += currentOpticalDepthMie * attn;
        primaryStepPosition += actualPrimaryStepSize;
    }
    scatteringColour = sunIntensity * (phaseRayleigh * betaRayleigh * accumulatedRayleigh + phaseMie * betaMie * accumulatedMie);
    scatteringOpacity = exp(
    -(betaMie * opticalDepth.y + betaRayleigh * opticalDepth.x + betaAbsorption * opticalDepth.z));
}


vec3 _ApplySpaceFog(
in vec3 rgb,
in float distToPoint,
in float height,
in vec3 worldSpacePos,
in vec3 rayOrigin,
in vec3 rayDir,
in vec3 sunDir)
{
    float atmosphereThickness = (atmosphereRadius - planetRadius);
    float t0 = -1.0;
    float t1 = -1.0;
    // This is a hack since the world mesh has seams that we haven't fixed yet.
    if (_RayIntersectsSphere(
    rayOrigin, rayDir, planetCentre, planetRadius, t0, t1)) {
        if (distToPoint > t0) {
            distToPoint = t0;
            worldSpacePos = rayOrigin + t0 * rayDir;
        }
    }
    if (!_RayIntersectsSphere(
        rayOrigin, rayDir, planetCentre, planetRadius + atmosphereThickness * 5.0, t0, t1)) {
        return rgb * 0.5;
    }
    // Figure out a better way to do this
    float silhouette = saturate((distToPoint - 10000.0) / 10000.0);
    // Glow around planet
    float scaledDistanceToSurface = 0.0;
    // Calculate the closest point between ray direction and planet. Use a point in front of the
    // camera to force differences as you get closer to planet.
    vec3 fakeOrigin = rayOrigin + rayDir * atmosphereThickness;
    float t = max(0.0, dot(rayDir, planetCentre - fakeOrigin) / dot(rayDir, rayDir));
    vec3 pb = fakeOrigin + t * rayDir;
    scaledDistanceToSurface = saturate((distance(pb, planetCentre) - planetRadius) / atmosphereThickness);
    scaledDistanceToSurface = smoothstep(0.0, 1.0, 1.0 - scaledDistanceToSurface);
    //scaledDistanceToSurface = smoothstep(0.0, 1.0, scaledDistanceToSurface);
    float scatteringFactor = scaledDistanceToSurface * silhouette;
    // Fog on surface
    t0 = max(0.0, t0);
    t1 = min(distToPoint, t1);
    vec3 intersectionPoint = rayOrigin + t1 * rayDir;
    vec3 normalAtIntersection = normalize(intersectionPoint);
    float distFactor = exp(-distToPoint * 0.0005 / (atmosphereThickness));
    float fresnel = 1.0 - saturate(dot(-rayDir, normalAtIntersection));
    fresnel = smoothstep(0.0, 1.0, fresnel);
    float extinctionFactor = saturate(fresnel * distFactor) * (1.0 - silhouette);
    // Front/Back Lighting
    vec3 BLUE = vec3(0.5, 0.6, 0.75);
    vec3 YELLOW = vec3(1.0, 0.9, 0.7);
    vec3 RED = vec3(0.035, 0.0, 0.0);
    float NdotL = dot(normalAtIntersection, sunDir);
    float wrap = 0.5;
    float NdotL_wrap = max(0.0, (NdotL + wrap) / (1.0 + wrap));
    float RdotS = max(0.0, dot(rayDir, sunDir));
    float sunAmount = RdotS;
    vec3 backLightingColour = YELLOW * 0.1;
    vec3 frontLightingColour = mix(BLUE, YELLOW, pow(sunAmount, 32.0));
    vec3 fogColour = mix(backLightingColour, frontLightingColour, NdotL_wrap);
    extinctionFactor *= NdotL_wrap;
    // Sun
    float specular = pow((RdotS + 0.5) / (1.0 + 0.5), 64.0);
    fresnel = 1.0 - saturate(dot(-rayDir, normalAtIntersection));
    fresnel *= fresnel;
    float sunFactor = (length(pb) - planetRadius) / (atmosphereThickness * 5.0);
    sunFactor = (1.0 - saturate(sunFactor));
    sunFactor *= sunFactor;
    sunFactor *= sunFactor;
    sunFactor *= specular * fresnel;
    vec3 baseColour = mix(rgb, fogColour, extinctionFactor);
    vec3 litColour = baseColour + _SoftLight(fogColour * scatteringFactor + YELLOW * sunFactor, baseColour);
    vec3 blendedColour = mix(baseColour, fogColour, scatteringFactor);
    blendedColour += blendedColour + _SoftLight(YELLOW * sunFactor, blendedColour);
    return mix(litColour, blendedColour, scaledDistanceToSurface * 0.25);
}

vec3 _ApplyGroundFog(
in vec3 rgb,
float distToPoint,
float height,
in vec3 worldSpacePos,
in vec3 rayOrigin,
in vec3 rayDir,
in vec3 sunDir)
{
    vec3 up = normalize(rayOrigin);
    float skyAmt = dot(up, rayDir) * 0.25 + 0.75;
    skyAmt = saturate(skyAmt);
    skyAmt *= skyAmt;
    vec3 DARK_BLUE = vec3(0.8, 0.8, 0.8);
    vec3 LIGHT_BLUE = vec3(0.5, 0.5, 0.5);
    vec3 DARK_ORANGE = vec3(0.7, 0.4, 0.05);
    vec3 BLUE = vec3(0.5, 0.6, 0.7);
    vec3 YELLOW = vec3(1.0, 0.9, 0.7);
    vec3 fogCol = mix(LIGHT_BLUE, DARK_BLUE, skyAmt);
    float sunAmt = max(dot(rayDir, sunDir), 0.0);
    fogCol = mix(fogCol, YELLOW, pow(sunAmt, 16.0));
    float be = 0.0001;
    float fogAmt = (1.0 - exp(-distToPoint * be));
    // Sun
    sunAmt = 0.5 * saturate(pow(sunAmt, 128.0));
    return mix(rgb, fogCol, fogAmt) + sunAmt * YELLOW;
}

vec3 _ApplyFog(
in vec3 rgb,
in float distToPoint,
in float height,
in vec3 worldSpacePos,
in vec3 rayOrigin,
in vec3 rayDir,
in vec3 sunDir)
{
    float distToPlanet = max(0.0, length(rayOrigin) - planetRadius);
    float atmosphereThickness = (atmosphereRadius - planetRadius);
    vec3 groundCol = _ApplyGroundFog(
    rgb, distToPoint, height, worldSpacePos, rayOrigin, rayDir, sunDir);
    vec3 scatteringColour = vec3(0.0);
    vec3 scatteringOpacity = vec3(1.0, 1.0, 1.0);
    _ComputeScattering(
    worldSpacePos, rayDir, rayOrigin,
    sunDir, scatteringColour, scatteringOpacity
    );
    return rgb * scatteringOpacity + scatteringColour;
}


void main(void){
//    vec3 viewVector = (inverseProjection * vec4(textureCoords.xy * 2 - 1, 0, 1)).xyz;
//    viewVector = (inverseView * vec4(viewVector, 0)).xyz;
//
//    vec4 originalCol = texture(colourTexture, textureCoords);
//    vec4 depthNonLinear = texture(depthTexture, textureCoords);
//
//    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depthNonLinear.r - 1.0) * (far - near));
//
//    if(floorDistance > 1000){
//        out_Colour = vec4(1,0,0,1);
//    }
//    else if(floorDistance > 500){
//        out_Colour = vec4(1,1,0,1);
//    }
//    else if(floorDistance > 100){
//        out_Colour = vec4(0,1,1,1);
//    }
//    else if(floorDistance > 20){
//        out_Colour = vec4(1,0,1,1);
//    }
//    else if(floorDistance > 1){
//        out_Colour = vec4(0,1,0,1);
//    }
//    else{
//        out_Colour = vec4(0,0,1,1);
//    }
//    return;




    vec3 viewRay = _ScreenToWorld(vec3(textureCoords, texture(depthTexture, textureCoords).r));
    float dist = length(viewRay - worldSpaceCameraPos);
    float height = max(0.0, length(worldSpaceCameraPos) - planetRadius);
    vec3 camDir = normalize(viewRay - worldSpaceCameraPos);

    vec3 diffuse = texture(colourTexture, textureCoords).rgb;
    vec3 lightDir = normalize(dirToSun);

    diffuse = _ApplyFog(diffuse, dist, height, viewRay, worldSpaceCameraPos, camDir, lightDir);

    diffuse = 1.0 - exp(-diffuse);

    out_Colour.rgb = diffuse;
    out_Colour.a = 1;
}
