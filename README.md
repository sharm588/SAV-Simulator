# Shared Autonomous Electric Vehicles Simulator

![Autonomous Uber](https://s.marketwatch.com/public/resources/images/MW-EV890_uberse_ZH_20160914102258.jpg)

## Run using IntelliJ IDEA IDE

1.) Ensure the following are installed on your computer  

    i. Java (https://www.java.com/en/download)
    ii. IntelliJ Community Edition (https://www.jetbrains.com/idea/download)
    iii. Git (https://git-scm.com/downloads)

2.) Open IntelliJ IDEA and click *Check out from Version Control* from the main menu

3.) Enter Git URL (https://github.com/sharm588/SAEV-Simulator.git) and login credentials

4.) Accept any installations needed for project to run (such as Gradle if prompted)

5.) Go to File -> Settings Repository and enter the settings repository URL (https://github.com/sharm588/SAEV-Simulator-Settings.git)
    
    i. Click 'Overwrite Local'
    ii. Enter access token when requested: b00412df1d4c49081939b50c6bd715951bffaf04
    
6.) Set environment variable for resources folder to "RESOURCES_FOLDER" when using main file *Application*
   
    i. Click the box in the top right hand corner with the file's name
    ii. Click 'Edit Configurations...'
    iii. In the Environment Variables section under Configuration, type "RESOURCES_FOLDER=" followed by the path for the resources folder

7.) Project can be run be clicking the green play button in the top right hand corner.
