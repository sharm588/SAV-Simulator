# Shared Autonomous Electric Vehicles Simulator

![Autonomous Uber](https://s.marketwatch.com/public/resources/images/MW-EV890_uberse_ZH_20160914102258.jpg)

## Run using IntelliJ IDEA

1.) Ensure the following are installed on your computer  

    1. Java (https://www.java.com/en/download)
    2. IntelliJ Community Edition (https://www.jetbrains.com/idea/download)
    3. Git (https://git-scm.com/downloads)

2.) Open IntelliJ IDEA and click *Check out from Version Control* from the main menu

3.) Enter Git URL (https://github.com/sharm588/SAEV-Simulator.git) and login credentials

4.) Accept any installations needed for project to run

5.)  Overwrite current IDE settings with provided settings to ensure project runs correctly
    
    1. Go to File -> Settings Repository
    2. Enter the settings repository URL (https://github.com/sharm588/SAEV-Simulator-Settings.git)
    3. Click 'Overwrite Local'
    4. Enter access token when requested: b00412df1d4c49081939b50c6bd715951bffaf04
    
6.) Set environment variable for resources folder to 'RESOURCES_FOLDER' when using main file *Application*
   
    1. Click the box in the top right hand corner with the file's name
    2. Click 'Edit Configurations...'
    3. In the 'Environment Variables' section under 'Configuration', type 'RESOURCES_FOLDER=' followed by the path for the resources folder

7.) Project can be run be clicking the green play button in the top right hand corner
