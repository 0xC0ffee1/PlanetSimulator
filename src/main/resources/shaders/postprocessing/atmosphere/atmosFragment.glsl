#version 150

in vec2 textureCoords;

out vec4 out_Colour;

uniform sampler2D colourTexture;

//depth texture is used to retrieve the distance to surface
uniform sampler2D depthTexture;

uniform vec3 planetCentre;
uniform float planetRadius;
uniform float atmosphereRadius;
uniform vec3 cameraViewDir;

//matrices used to calculate the direction of the viewray
uniform mat4 inverseProjection;
uniform mat4 inverseView;

uniform vec3 worldSpaceCameraPos;
uniform vec3 dirToSun;


uniform int magicValue;
uniform int numInScatteringPoints;
uniform int numOpticalDepthPoints;
uniform float intensity;

uniform float inScatterR;
uniform float inScatterG;
uniform float inScatterB;

uniform vec3 scatter;

float scatterR = pow(400 / scatter.r, 4) * intensity;
float scatterG = pow(400 / scatter.g, 4) * intensity;
float scatterB = pow(400 / scatter.b, 4) * intensity;

const float near = 1.0;
const float far = 35000000000.0;

vec4 scatteringCoefficients = vec4(scatterR,scatterG,scatterB,1);
const float ditherStrength = 4;
const float ditherScale = 1;
uniform float densityFalloff;

const float maxFloat = 3.402823466e+38;


//For logarithmic linear depth value reading
const float Fcoef = 2.0 / log2(far + 1.0);

vec3 toneMapReinhard(vec3 color) {
    color = color / (color + vec3(1.0));
    return pow(color, vec3(1.0/2.2)); // Gamma correction
}

float densityAtPoint(vec3 densitySamplePoint) {
    float heightAboveSurface = length(densitySamplePoint - planetCentre) - planetRadius;
    float height01 = heightAboveSurface / (atmosphereRadius - planetRadius);
    float localDensity = exp(-height01 * densityFalloff) * (1 - height01);
    //float localDensity = exp(-height01 * densityFalloff);
    return localDensity;
}

float opticalDepth(vec3 rayOrigin, vec3 rayDir, float rayLength) {
    vec3 densitySamplePoint = rayOrigin;
    float stepSize = rayLength / (numOpticalDepthPoints - 1);
    float opticalDepth = 0;

    for (int i = 0; i < numOpticalDepthPoints; i ++) {
        float localDensity = densityAtPoint(densitySamplePoint);
        opticalDepth += localDensity * stepSize;
        densitySamplePoint += rayDir * stepSize;
    }
    return opticalDepth;
}


//Ray Sphere intersection method
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

vec3 calculateLight(vec3 rayOrigin, vec3 rayDir, float rayLength, vec3 originalCol, vec2 uv) {
    //    float blueNoise = tex2Dlod(_BlueNoise, vec4(squareUV(uv) * ditherScale,0,0));
    //    blueNoise = (blueNoise - 0.5) * ditherStrength;

    vec3 inScatterPoint = rayOrigin;
    float stepSize = rayLength / (numInScatteringPoints - 1);
    vec3 inScatteredLight = vec3(0);
    float viewRayOpticalDepth = 0;


    for (int i = 0; i < numInScatteringPoints; i ++) {
        float sunRayLength = raySphere(planetCentre, atmosphereRadius, inScatterPoint, dirToSun).y;
        float sunRayOpticalDepth = opticalDepth(inScatterPoint, dirToSun, sunRayLength);
        viewRayOpticalDepth = opticalDepth(inScatterPoint, -rayDir, stepSize * i);
        vec3 transmittance = exp(-(sunRayOpticalDepth + viewRayOpticalDepth) * scatteringCoefficients.xyz);
        float localDensity = densityAtPoint(inScatterPoint);

        inScatteredLight += localDensity * transmittance * scatteringCoefficients.xyz * stepSize;
        inScatterPoint += rayDir * stepSize;
    }
    float originalColTransmittance = exp(-viewRayOpticalDepth);
    return originalCol * originalColTransmittance + inScatteredLight;
}


void main(void){
    vec3 viewVector = (inverseProjection * vec4(textureCoords.xy * 2 - 1, 0, 1)).xyz;
    viewVector = (inverseView * vec4(viewVector, 0)).xyz;

    vec4 originalCol = texture(colourTexture, textureCoords);
    vec4 depthNonLinear = texture(depthTexture, textureCoords);

    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depthNonLinear.r - 1.0) * (far - near));

    float test = depthNonLinear.r * far;

    //The distance to the surface
    float v_depth = pow(2.0, depthNonLinear.r / (Fcoef * 0.5));

    float sceneDepth = floorDistance;

    //vec3 viewRay = _ScreenToWorld(vec3(textureCoords.x, textureCoords.y, depthNonLinear.x));
    vec3 rayOrigin = worldSpaceCameraPos;
    vec3 rayDir = normalize(viewVector);

    //planetRadius+some magic offset, might be the way to get the atmosphere density right?
    float dstToSurface = raySphere(planetCentre, planetRadius+magicValue, rayOrigin, rayDir).x;
    //float dstToSurface = v_depth + 6100;

    vec2 hitInfo = raySphere(planetCentre, atmosphereRadius, rayOrigin, rayDir);
    float dstToAtmosphere = hitInfo.x;
    float dstThroughAtmosphere = min(hitInfo.y, dstToSurface - dstToAtmosphere);

    //Length of the ray through the atmosphere
    //The min will make sure that nearest occluding objects will cut the ray short
    dstThroughAtmosphere = min(v_depth, dstThroughAtmosphere);

    if(dstThroughAtmosphere > 0){
        const float epsilon = 0.0001;
        vec3 pointInAtmosphere = rayOrigin + rayDir * (dstToAtmosphere + epsilon);
        vec3 light = calculateLight(pointInAtmosphere, rayDir, dstThroughAtmosphere - epsilon * 2, originalCol.xyz, textureCoords);
        out_Colour = vec4(toneMapReinhard(light), 0);
        //out_Colour = 1.0 - exp(-out_Colour);
    }
    else{
        out_Colour = vec4(toneMapReinhard(originalCol.rgb), originalCol.a);
    }
}
