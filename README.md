<p align="center">
<img src="https://s.marketwatch.com/public/resources/images/MW-EV890_uberse_ZH_20160914102258.jpg" width=500>
</p>
<h1 align="center">
Shared Autonomous Vehicles Simulator
</h1>
<p align="center">
Repository for a simulation of autonomous vehicles in ridesharing
</p>

# About

Autonomous vehicles remove the need for a driver, which reduces the price for ridesharing to the point where it is competitive to the use of personal vehicles. However, using a fleet of shared autonomous vehicles (SAV) poses several new challenges, such as deciding which vehicle is assigned to which passenger. Using Dikjstra's shortest path algorithm, a CPLEX optimization equation, and a genetic algorithm with an SAV simulation allows us to find the most efficient vehicle-passenger assignments to pick-up/drop-off passengers to minimize passenger wait times.

# Run using IntelliJ IDEA

Install the following: 

1.) Java (https://www.java.com/en/download)

2.) IntelliJ Community Edition (https://www.jetbrains.com/idea/download)

3.) Git (https://git-scm.com/downloads)

4.) CPLEX Optimizer (https://www.ibm.com/analytics/cplex-optimizer)

*Note: Download IBM's Download Director before installing the CPLEX Optimizer*
    
## Setting Up IntelliJ Project

1.) Open IntelliJ IDEA and click *Check out from Version Control* from the main menu

2.) Enter Git URL (https://github.com/sharm588/SAEV-Simulator.git) and login credentials

3.) Wait for the project to open. Install the Lombok plugin for IntelliJ IDE
    
    1. Navigate to File -> Settings -> Plugins and search Lombok
    2. Restart IntelliJ and make sure to enable annotations processing when prompted in the bottom left corner

4.)  If provided an access token, overwrite current IDE settings with provided settings to ensure project runs correctly (optional)
    
    1. Go to File -> Settings Repository
    2. Enter the settings repository URL (https://github.com/sharm588/SAV-Simulator-Settings.git)
    3. Click 'Overwrite Local'
    4. Enter access token when requested
    
5.) Set environment variable for resources folder to 'RESOURCES_FOLDER' when using main file *Application*
   
    1. Open the project directory on the left hand side
    2. Navigate to src -> main -> java -> org -> umn -> research -> evsimulator
    3. Right click on the Application class and select the option to create a configuration of Application.main(). Press 'Ok' when prompted
    4. Click 'Edit Configurations...' in the top right hand corner
    5. In the 'Environment Variables' section under 'Configuration', type 'RESOURCES_FOLDER=' followed by the path for the resources folder
    6. Click 'Apply' then 'Ok'
    
## Adding CPLEX Optimizer

1.) Go to the *build.gradle* file and compile the cplex.jar file with your cplex.jar path under *dependencies*

   *Note: Click the 'import changes' pop-up that may show up when using IntelliJ*
    
    1. 'compile files('/.../CPLEX_Studio1210/cplex/lib/cplex.jar')' for macOS
    2. 'compile files('C:\\...\\CPLEX_Studio1210\\cplex\\lib\\cplex.jar')' for Windows

2.) Add the path for the CPLEX files to *VM options* in the main file *Application* 

    1. Click the box in the top right hand corner with the file's name
    2. Click 'Edit Configurations...'
    3. In the 'VM options' section under 'Configuration', enter '-Djava.library.path=' followed by the path for the CPLEX library files
    
        a.) '-Djava.library.path=/.../CPLEX_Studio1210/cplex/bin/x86-64_osx' for macOS 
        b.) '-Djava.library.path="C:\...\CPLEX_Studio1210\cplex\bin\x64_win64"' for Windows
        
    4. Click 'Apply' then 'Ok'

3.) Run the simulation by clicking the green play button in the top right hand corner!
