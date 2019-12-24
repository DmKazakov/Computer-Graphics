#version 330

layout(points) in;
layout(points) out;
layout(max_vertices = 100) out;

in float type[];
in vec3 position[];
in vec3 velocity[];
in float age[];

out float newType;
out vec3 newPosition;
out vec3 newVelocity;
out float newAge;

uniform float deltaTimeMillis;
uniform float time;
uniform sampler1D randomTexture;
uniform float launcherLifetime;
uniform float shellLifetime;
uniform float secondaryShellLifetime;


vec3 GetRandomDir(float TexCoord) {
     vec3 Dir = texture(randomTexture, TexCoord).xyz;
     Dir -= vec3(0.5, 0.5, 0.5);
     return Dir;
}

bool equals(float a, float b) {
    return abs(a - b) < 0.1;
}

void main() {
    float PARTICLE_TYPE_LAUNCHER = 0.0;
    float PARTICLE_TYPE_SHELL = 1.0;
    float PARTICLE_TYPE_SECONDARY_SHELL = 2.0;

    float nextAge = age[0] + deltaTimeMillis;
    if (equals(type[0], PARTICLE_TYPE_LAUNCHER)) {
        if (nextAge >= launcherLifetime) {
            newType = PARTICLE_TYPE_SHELL;
            newPosition = position[0];
            vec3 Dir = GetRandomDir(time / 1000.0);
            Dir.y = max(Dir.y, 0.5);
            newVelocity = normalize(Dir) / 2.0;
            newAge = 0.0;
            EmitVertex();
            EndPrimitive();
            nextAge = 0.0;
        }

        newType = PARTICLE_TYPE_LAUNCHER;
        newPosition = position[0];
        newVelocity = velocity[0];
        newAge = nextAge;
        EmitVertex();
        EndPrimitive();
    }
    else {
        float deltaTimeSecs = deltaTimeMillis / 1000.0f;
        vec3 dPos = deltaTimeSecs * velocity[0];
        vec3 dVel = vec3(0.0, -0.003, 0.0);

        if (equals(type[0], PARTICLE_TYPE_SHELL))  {
	        if (nextAge < shellLifetime) {
	            newType = PARTICLE_TYPE_SHELL;
	            newPosition = position[0] + dPos;
	            newVelocity = velocity[0] + dVel;
	            newAge = nextAge;
	            EmitVertex();
	            EndPrimitive();
	        } else {
                for (int i = 0 ; i < 10 ; i++) {
                     newType = PARTICLE_TYPE_SECONDARY_SHELL;
                     newPosition = position[0];
                     vec3 direction = GetRandomDir((time + i) / 1000.0);
                     newVelocity = normalize(direction) / 2.0;
                     newAge = 0.0f;
                     EmitVertex();
                     EndPrimitive();
                }
            }
        } else {
            if (nextAge < secondaryShellLifetime) {
                newType = PARTICLE_TYPE_SECONDARY_SHELL;
                newPosition = position[0] + dPos;
                newVelocity = velocity[0] + dVel;
                newAge = nextAge;
                EmitVertex();
                EndPrimitive();
            }
        }
    }
}
