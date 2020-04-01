# Shared Autonomous Electric Vehicles Simulator

![Autonomous Uber](https://s.marketwatch.com/public/resources/images/MW-EV890_uberse_ZH_20160914102258.jpg)

## Run using IntelliJ IDEA

Ensure the following are installed on your computer 

1.) Java (https://www.java.com/en/download)

2.) IntelliJ Community Edition (https://www.jetbrains.com/idea/download)

3.) Git (https://git-scm.com/downloads)

4.) CPLEX Optimizer (https://www.ibm.com/analytics/cplex-optimizer)

*Note: Make sure to download IBM's Download Director before installing the CPLEX Optimizer*
    
### Setting Up IntelliJ Project

1.) Open IntelliJ IDEA and click *Check out from Version Control* from the main menu

2.) Enter Git URL (https://github.com/sharm588/SAEV-Simulator.git) and login credentials

3.) Install the Lombok plugin for IntelliJ IDE
    
    1. Navigate to File -> Settings -> Plugins and search Lombok. 
    2. Restart InelliJ and make sure to enable annotations processing when prompted in the bottom left corner

4.)  Overwrite current IDE settings with provided settings to ensure project runs correctly
    
    1. Go to File -> Settings Repository
    2. Enter the settings repository URL (https://github.com/sharm588/SAEV-Simulator-Settings.git)
    3. Click 'Overwrite Local'
    4. Enter access token when requested: b00412df1d4c49081939b50c6bd715951bffaf04
    
5.) Set environment variable for resources folder to 'RESOURCES_FOLDER' when using main file *Application*
   
    1. Open the project directory on the left hand side (press the button on the left edge of IntelliJ with the folder icon)
    2. Navigate to src -> main -> java -> org -> umn -> research -> evsimulator
    3. Right click on the Application class and select the option to create a configuration of Application.main(). Press 'Ok' when   prompted
    4. Click 'Edit Configurations...' in the top right hand corner
    5. In the 'Environment Variables' section under 'Configuration', type 'RESOURCES_FOLDER=' followed by the path for the resources     folder (right click the resources folder in the project directory and select 'Copy Path...')
    6. Click the 'Apply' button then 'Ok'
    
### Adding CPLEX Optimizer

1.) Add CPLEX library to project
    
    1. Select the 'Project Structure' folder icon in the top right hand corner and press the 'Libraries' tab
    2. Click the '+' button located above the list of libraries and click the 'Java' option
    3. Select the path for cplex.jar
        
        a.) /.../CPLEX_Studio1210/cplex/lib/cplex.jar for macOS
        b.) C:\...\CPLEX_Studio1210\cplex\lib\cplex.jar for Windows
        
    4. Click the 'Apply' button then 'Ok'

2.) Add the path for the CPLEX files to *VM options* in the main file *Application* 

    1. Click the box in the top right hand corner with the file's name
    2. Click 'Edit Configurations...'
    3. In the 'VM options' section under 'Configuration', type '-Djava.library.path=' followed by the path for the files for the CPLEX  library
    
        a.) -Djava.library.path=/.../CPLEX_Studio1210/cplex/bin/x86-64_osx for macOS 
        b.) -Djava.library.path="C:\Program Files\IBM\ILOG\CPLEX_Studio1210\cplex\bin\x64_win64" for Windows
        
    4. Click the 'Apply' button then 'Ok'
    
3.) Go to the *build.gradle* file and compile the cplex.jar file with your cplex.jar path under *dependencies*

   *Note: Make sure to click the 'import changes' pop-up that may show up when using IntelliJ*
    
    1. compile files('/.../CPLEX_Studio1210/cplex/lib/cplex.jar') for macOS
    2. compile files('C:\\...\\CPLEX_Studio1210\\cplex\\lib\\cplex.jar') for Windows
