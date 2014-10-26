Image-Tracking
==============

A very basic approach to Object tracking through openCV and Xtend

I searched the interwebz for motion detection and image-tracking, and all the solutions felt tough to execute(because no prior experience
in openCV).So, I decided to do it on my own and learn on the way.

Working:

Firstly, all the contours are found, then seperated on the basis of vertices. Only those ones are worked upon which have 4 vertices.
After that, the submat corresponding to the contour is checked if it's black and in the center of screen. If these conditions are satisfied,
then the object is locked on, and it's area and center is stored. Now that the image is locked, it's tracked through changes in it's mid point.
