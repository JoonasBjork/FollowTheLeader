# Follow The Leader
Simulation based on the paper "Steering Behaviors For Autonomous Characters" by Craig W. Reynolds

Paper: http://www.red3d.com/cwr/steer/gdc99/

## Summary
The basic idea of the project is a simulation where a leader is followed by followers. The followers avoid getting in the way of the leader and hitting the leader. There are many different modes and variables to how the leader and followers act and how well they perform in their tasks.

The program offers has an user interface made with Scala Swing. The program can read a simulation state from a csv file and has an editor that can be used to create csv files for different imulation states. 

The calculations of the followers has been parallelised for better performance especially with big numbers of followers. 

## Pictures of the progam
The default view
![main view](pictures/default_state.PNG)
One of the modes where followers follow the leader
![followers following previous](pictures/followers_following_previous.PNG)
The editor view
![editor view](pictures/editor.PNG)