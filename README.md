NeoScavengerModManager
======================

A Mod Manager/Tool for the game Neo Scavenger.

As of right now the programm can only be used to see all the information inside of a neogame.xml.
No changes can be saved yet.
The programm does however list all the conditions/itemProperties with both their id numbers and names for easier understanding of what's going on.

To Set-Up project
======================
Do to the use of LibGDX (http://libgdx.badlogicgames.com/) setup for the project isn't super straight forward.
First you need to download the LibGDX Setup App here http://libgdx.badlogicgames.com/download.html.
Create a test project with desktop suport and make sure you check the eclipse option in advance settings.
Next you'll want to add the core and desktop projects from the NeoScavengerModManager project.
Finally copy the .classpaths files from the test project to the NeoScavengerModManager core and desktop projects.

Everything should finally run now. You'll launch the game by launching the Desktop launcher file in the desktop project.

To-Do-List
======================

-Add explanations for all tables and columns

-Remove LibGDX code

-Create graphical editor for encounters

-Add mod saving support

-Add mod xml export

-Show images where needed

-Generate getmod.php and getimage.php files

-Improve UI
