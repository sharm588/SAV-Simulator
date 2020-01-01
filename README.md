# Shared Autonomous Electric Vehicles Simulator

![Autonomous Uber](https://s.marketwatch.com/public/resources/images/MW-EV890_uberse_ZH_20160914102258.jpg)

## Run using IntelliJ IDEA

### Setting Up IntelliJ Environment

1.) Ensure the following are installed on your computer  

    1. Java (https://www.java.com/en/download)
    2. IntelliJ Community Edition (https://www.jetbrains.com/idea/download)
    3. Git (https://git-scm.com/downloads)
    4. CPLEX Optimizer (https://www.ibm.com/analytics/cplex-optimizer)

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
    4. Click the 'Apply' button then 'Ok'
    
### Adding the CPLEX Optimizer

1.) Add CPLEX library to project
    
    1.) Select the 'Project Structure' folder icon in the top right hand corner and press the 'Libraries' tab
    2.) Click the '+' button located above the list of libraries and click the 'Java' option
    3.) Find the path for cplex.jar (/.../CPLEX_Studio1210/cplex/lib/cplex.jar for macOS)
    4.) Click the 'Apply' button then 'Ok'

2.) Add the path for the CPLEX files to *VM options* in the main file *Application* 

    1. Click the box in the top right hand corner with the file's name
    2. Click 'Edit Configurations...'
    3. In the 'VM options' section under 'Configuration', type '-Djava.library.path=' followed by the path for the files for the CPLEX library (looks like '-Djava.library.path=/.../CPLEX_Studio1210/cplex/bin/x86-64_osx' for macOS)
    4. Click the 'Apply' button then 'Ok'
