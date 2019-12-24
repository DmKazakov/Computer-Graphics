#version 330                                                                        
                                                                                    
layout (location = 0) in float Type;                                                
layout (location = 1) in vec3 Position;                                             
layout (location = 2) in vec3 Velocity;                                             
layout (location = 3) in float Age;                                                 
                                                                                    
out float type;
out vec3 position;
out vec3 velocity;
out float age;
                                                                                    
void main()                                                                         
{                                                                                   
    type = Type;
    position = Position;
    velocity = Velocity;
    age = Age;
}
