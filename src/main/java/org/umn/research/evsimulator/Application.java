package org.umn.research.evsimulator;

import ilog.concert.IloException;

import java.io.IOException;
import java.util.ArrayList;

public class Application {

    private static boolean writeToFile = true;

    public static void main(String [] args) throws IloException, IOException
    {
        double ratio = 1.0/6;
        double scale = 1.0;
        int size = 10;

        double betaVals[] = new double[] {3.392039646582563,
            2.7261594121252806,
            3.2252083248020535,
            0.4917708902934548,
            2.5698335537729666,
            7.575791481163725,
            5.171239604853916,
            5.503510737103531,
            4.398779117674996,
            2.8623889247422474,
            1.5267241099281392,
            7.374361259389541,
            5.516190000866851,
            9.153447193109473,
            0.0699176843213456,
            2.0758316149138754,
            3.8636239342711765,
            1.634021666472627,
            9.277213544267212,
            0.8641898440079865,
            9.603732454770102,
            0.38928142079836414,
            3.2070179581095895,
            2.6790343569213606,
            9.190351083216848,
            4.067125631403106,
            4.244044573128392,
            4.566854840264554,
            6.032073048518097,
            5.1779989666503825,
            8.98510031635978,
            4.172854998625434,
            3.0743194673347984,
            8.189441161737088,
            2.0884412140201225,
            5.150950573634113,
            2.322187076977965,
            4.785793088833565,
            5.262298976523886,
            8.461002310384906,
            3.8361115055053507,
            4.1888680816033705,
            3.7341904713726723,
            9.953972469439726,
            2.0546590031424214,
            0.2732991226214998,
            0.5211405828142168,
            7.38984921907369,
            0.4335217075529585,
            1.3606490812647054,
            9.611961620127826,
            8.089809460591074,
            6.3370526440883435,
            3.0392743533053257,
            2.776253094754729,
            1.7932418776880887,
            6.878451821122877,
            9.132312827868704,
            1.1307951177600972,
            1.9718865357169324,
            2.9769707403459145,
            1.0240674119117732,
            1.8225287779951294,
            2.847490222263893,
            3.2852748671937704,
            0.9554365908461826,
            2.228330660498835,
            8.666018230938414,
            1.8373657224100204,
            0.6119425883941876,
            8.925304349778498,
            6.400600465447406,
            3.2772320572527533,
            2.3532949128374616,
            6.351329780670331,
            2.9870361409567012,
            7.3506635253784705,
            6.771316298577785,
            0.5541200276015223,
            3.4398141856425433,
            9.977425132296162,
            6.913452251606525,
            6.7180616813654614,
            6.753255350521331,
            1.6682627869893774,
            6.4719726641480335,
            5.261619266859336,
            2.361515727676804,
            5.310963720839997,
            6.514366347341019,
            0.21410926997391777,
            1.1128374230289184,
            4.421974632803528,
            2.918319688794322,
            5.309154735810182,
            1.2075689231237652,
            9.844835844038776,
            4.575061656408286,
            7.773162224145377,
            1.3561182000796346,
            7.573960987961527,
            0.6377463587810872,
            7.140658215832549,
            0.5321487950326331,
            2.8920563222193607,
            3.6169371472738145,
            6.594358149538006,
            6.1279578252239215,
            6.331737140802206,
            3.9631459508546776,
            7.306341539931932,
            0.06312813761540581,
            5.106665010724587,
            0.9267704073555194,
            2.0477318828863167,
            5.454178489011933,
            9.604801489661034,
            3.9486663972987546,
            2.9377887161614624,
            4.8220809848576724,
            8.29612701167006,
            8.609979450793787,
            9.619340779777108,
            4.496152300467191,
            5.979452163164795,
            1.2577512774129662,
            8.376001506036175,
            8.86696948962036,
            9.677706416979921,
            6.823208273730053,
            9.747622538892614,
            2.447599492184711,
            3.688355904032835,
            5.062531763687067,
            2.0539621063780364,
            7.321571235309431,
            0.3053713172403849,
            1.4752930022196464,
            0.9025528805689553,
            8.487662567924755,
            5.551428683183717,
            0.4936361466832595,
            0.23527587522373272,
            9.855713576186664,
            9.893792020487894,
            0.18567698905169072,
            8.759161941109042,
            9.20431652539664,
            2.364594551744701,
            9.679475106230553,
            6.7104526093317745,
            1.280376871275909,
            2.7659984856151243,
            2.7893337162585286,
            1.8169752850876464,
            4.40217845342381,
            0.7129809311641688,
            3.914066149194263,
            0.0523963919724546,
            7.421726129886363,
            2.1512567538433682,
            5.177260122640414,
            6.316938769616844,
            0.7087798399342227,
            3.6851169235302264,
            6.992463800580511,
            2.532657824671433,
            6.938137776584663,
            2.559727030183211,
            7.761605397069834,
            5.807392045222505,
            4.893361686217607,
            5.464692469766562,
            2.2528514431524638,
            7.300017991038249,
            2.2442817241896007,
            2.4604672961513585,
            2.35449510018638,
            3.21065193895671,
            1.312258967643326,
            3.2764459063842932,
            8.364363105951703,
            2.922962958239533,
            5.206321066778691,
            1.9893439880784936,
            3.7496133455665492,
            5.741769088786521,
            0.394932077588771,
            2.662739164071155,
            1.3996931085582098,
            6.89761287433599,
            5.772292487006824,
            2.9134117596308027,
            4.795608490914828,
            5.531020043459439,
            7.584711720266235,
            4.567383315919419,
            3.231560743895278,
            2.222667223757738,
            4.201730509290128,
            0.5783267686065297,
            6.250379780448249,
            1.3016961852292075,
            4.648317347889783,
            5.2032782634280315,
            8.792088129930267,
            3.6429291815500053,
            0.05859653248686647,
            0.06535304793857444,
            5.205312847712326,
            8.95475489339197,
            7.768213298190078,
            2.1804282995048907,
            7.234156103397512,
            2.6629723422972917,
            3.631576444180775,
            1.2845272205283798,
            8.368522786599307,
            1.4118914002899918,
            9.331863249259337,
            2.8708963680292743,
            4.533800085690407,
            2.9325101566547906,
            7.9458094638321874,
            0.2305432785104844,
            8.996167409894374,
            0.5685975772968432,
            2.8319487123562737,
            3.487258535682909,
            7.572709527450273,
            0.10503366107516676,
            2.0301092601875292,
            6.1279179927072045,
            8.760897375242017,
            6.708767816074746,
            2.791608975903579,
            9.851122911220036,
            2.239890105284217,
            2.7712768650840056,
            8.603740660764803,
            6.692002770692175,
            8.346499464864682,
            3.2324157839264345,
            4.128769785929697,
            3.328273410297843,
            0.5915562259473128,
            3.569074562128166,
            1.8468156065088337,
            6.232401858669922,
            3.0578000714549267,
            7.04806348513091,
            7.873622124995169,
            7.907003867636319,
            4.608311148776137,
            2.0711736419838855,
            3.4183687637074636,
            3.825359650852186,
            2.2348898704390208,
            7.481365587521566,
            2.653310551745686,
            6.896200435937331,
            6.58868131313354,
            3.5525620001966174,
            7.386176520768294,
            1.2816827384852125,
            3.760732931237052,
            9.05153260686446,
            3.857898498102389,
            0.2197399340003492,
            8.718064789354887,
            0.2388751539639744,
            7.232381942615512,
            3.136752980203611,
            7.45122027914204,
            5.967113449603594,
            4.9016687196328785,
            5.783762903028439,
            6.8521578322524235,
            4.137445926532463,
            0.5989876966728702,
            3.3892007276378755,
            3.616734215181161,
            8.692700683013555,
            7.493823688410245,
            0.893385531789127,
            6.053920573255103,
            3.052556787978722,
            9.937279746709786,
            6.214797393677609,
            5.584282735029307,
            5.438122237076062,
            0.3326480727112968,
            2.1954118173269377,
            7.923147096839248,
            3.886459990928557,
            9.72524360499919,
            0.38212822717308637,
            7.46926834268134,
            3.9216802994175493,
            8.137196081204554,
            5.123611090467682,
            2.2576980316009276,
            9.827726355683176,
            3.417037801709254,
            9.596612304586797,
            0.9859527606216534,
            1.2589999900372162,
            5.430564102345631,
            6.323118771038425,
            8.085462615258951,
            0.5284853846143045,
            5.788583009216368,
            3.4994284417092683,
            7.585680118563824,
            2.3290517809123736,
            1.988814471368634,
            5.030528238758036,
            1.6061102265845462,
            6.87212031701244,
            8.843334684011126,
            6.547910832201131,
            4.711923028225419,
            9.468909427646626,
            9.369610274050542,
            1.8862319082396606,
            1.0816940967156474,
            4.432977749511845,
            1.9422966953092313,
            4.503977660929886,
            7.461210326414668,
            8.848804323509674,
            0.4346256040268792,
            6.155576488333571,
            1.2151706946247565,
            2.229660127142332,
            9.322341791382325,
            0.7571535931369155,
            9.4390020114811,
            1.3790421408536224,
            6.279790363271812,
            4.835394463059485,
            2.0558963816112232,
            5.873455142196006,
            0.6668991294354476,
            7.208749461909006,
            9.099574253226203,
            8.500340807609405,
            7.487437226270601,
            9.622473123572297,
            9.971163562988176,
            6.713473400697022,
            2.8930746455403633,
            6.9437632587395735,
            0.3589732212979002,
            5.709153970214688,
            9.114408888922087,
            0.22052579611350098,
            0.7228399434917465,
            1.0800498092728728,
            3.8192614357133756,
            8.417060757764842,
            8.770104113937718,
            9.169543833554258,
            1.3301904659842956,
            4.962225477805207,
            7.188173487111824,
            6.227975348349483,
            0.8773085323500951,
            3.3804030822098707,
            7.049017037830914,
            3.2631625373752438,
            0.11912806382730845,
            2.222864854686727,
            6.763823335110718,
            2.0811214644969556,
            0.5333066627161054,
            3.27688110825156,
            6.241450532992998,
            9.428906630310431,
            4.960797572742584,
            8.602205223530651,
            5.992182188655413,
            0.3800148816573301,
            6.581054679840198,
            1.3293173838168115,
            1.3186968521344777,
            9.14380834582619,
            2.902584333043867,
            2.3510759821583993,
            2.4490495902659095,
            5.3339340792837495,
            7.97226920131684,
            0.0492041747022931,
            2.4918506080011458,
            6.396723249339281,
            0.9385392838307927,
            3.019286837263114,
            6.431722877104047,
            2.3790806922682184,
            9.34760017778908,
            6.3927328766101175,
            6.033505121649167,
            7.506378482359681,
            1.108569193912261,
            0.8014174491979698,
            7.486562781613706,
            4.144394771007343,
            9.932514405077898,
            9.290482287898817,
            0.8959553284374555,
            8.635344376653375,
            5.232028148382655,
            1.5570450981157902,
            3.315101272100641,
            5.2265730424419345,
            8.96210977623503,
            6.777325973532911,
            9.91497418239979,
            3.8514913546022433,
            8.689771392120809,
            5.981972334203263,
            1.0977501733847828,
            9.875854118530853,
            8.29892266001428,
            2.449122415234357,
            6.888221192261774,
            9.088493324133097,
            5.16873117861843,
            3.9070195922381834,
            3.7741764464797045,
            9.194134188837538,
            5.407791127532562,
            3.4727492959062545,
            3.317348316131522,
            9.856944625293579,
            6.78308250543621,
            7.200942423533033,
            6.319404083042427,
            6.946971406742527,
            2.351835147016982,
            4.876045687837908,
            1.102863561762728,
            3.057645701876701,
            6.486056224025965,
            7.254038101975899,
            9.5547054388102,
            5.591046156744906,
            8.973620417216896,
            7.3442228222093755,
            9.980818040959646,
            5.748627460898181,
            2.1864646485022163,
            5.519003417900418,
            1.5323472516796421,
            6.618639063150498,
            0.9255616694650759,
            8.249308625014898,
            0.9903734722613544,
            6.217471504985097,
            4.4840016959556985,
            8.085604169664826,
            9.033249325767521,
            1.8206852104304083,
            5.713633971612019,
            8.139185877359488,
            9.761318786201281,
            5.210491804637489,
            2.4373799261774143,
            7.765723754607223,
            4.216622244695637,
            4.569972917552887,
            2.418451812625274,
            7.412787471614535,
            5.910594689924499,
            7.670338944229119,
            4.279367034926356,
            9.665346824402972,
            3.9606438090725984,
            2.726044120961789,
            3.2577783170959353,
            0.4080127192060201,
            7.336889257523325,
            7.8335894539709825,
            2.4927842431153002,
            8.720046077043616,
            1.933239890862739,
            7.263363292894276,
            7.654528168240253,
            6.628924679748335,
            0.7023669046466219,
            3.0398826283024984,
            5.161528018460723,
            0.1705323972941386,
            5.932029176097537,
            3.22431381235621,
            1.9743259180862804,
            1.3387916477334316,
            1.8905949313304171,
            4.104169056567491,
            8.010681520778961,
            6.454829786831259,
            5.532494284870105,
            2.0985878958840063,
            0.5966819968347958,
            0.24553790738887749,
            2.72972738390202,
            2.603630956759905,
            4.049771619539213,
            6.49363998978534,
            7.583445818119702,
            7.23052833485914,
            6.483851452845684,
            5.506162794095264,
            2.7700121335388084,
            7.930837867644137,
            0.23324167465160084,
            5.84545758433634,
            9.588836530234614,
            6.355988966067932,
            1.9114321473035845,
            2.776692055389418,
            7.404678761665478,
            9.46469995070326,
            3.0521957055834914,
            8.707998136406419,
            3.5149910593932585,
            7.870272494801865,
            2.524772724926202,
            1.5342236046819158,
            6.123275906485751,
            1.71078342579553,
            7.471481234055841,
            5.242503538860892,
            5.062314002607292,
            9.928711479603939,
            0.38524507311230916,
            3.397632480279227,
            7.186516033361377,
            2.7625464431647364,
            6.126170672243715,
            0.1974843755453315,
            5.940537371544043,
            3.981452066531798,
            4.170450622317401,
            5.673492259961578,
            7.511690248364115,
            8.935061331295074,
            8.191505337983093,
            1.3084647001691718,
            9.186570439609476,
            6.844005015444456,
            7.192418105780427,
            9.853782437572672,
            9.057719089834793,
            0.9227854720638418,
            9.84036278847196,
            8.450288249799806,
            0.06401502802573678,
            7.905126064874933,
            6.39785594740768,
            7.291936851203942,
            5.498898862537076,
            9.332941154747182,
            0.7023129695781383,
            7.018553494634262,
            7.583408421820655,
            0.5735868252343579,
            8.014526060151406,
            6.78380199887725,
            5.476405500432332,
            9.49707120310929,
            9.216269789041556,
            1.8960424802581743,
            5.539466684477698,
            6.253773939699753,
            5.7115789727893365};
        double alphaVals[] = new double[] {2.787301431908932,
            0.7433040883949205,
            3.3666341485968387,
            6.3541228797835805,
            2.7004737124589617,
            9.59108992130934,
            3.828164706580155,
            3.5214002064232695,
            2.668778485455743,
            5.860214302714439,
            5.933168840103922,
            1.8705843148626622,
            1.9427457667205117,
            3.5413382611207855,
            4.904641738331304,
            0.46333990582199336,
            3.796655238932968,
            1.701822542039969,
            9.225929311183934,
            8.604926097557241,
            3.709703942523852,
            3.384543846747122,
            8.595084998622218,
            1.6054848279252676};
        //readValues(betaVals, alphaVals);

        //runSimulation(betaVals, alphaVals, scale, ratio); //runs simulation 10 times
        //waitTime = network.simulate(7200, 4.339385779264273, 5.311728677228018, true);
        //System.out.println("Avg wait time: " + network.avgWaitTime);

        while (size != 60) {
            double percent = scale * 100;
            //System.out.println("Scale: " + scale);
            System.out.println("Size: " + size);
            runSimulation(betaVals, alphaVals, scale, size);
            //runSimulation(betaVals, alphaVals, scale, ratio);
            size += 5;

//            if (size != 1.0) {
//                scale += 10.0;
//            } else {
//                scale += 9.0;
//            }
        }
//        for (int i = 0; i < 1; i++) {
//
//            GeneticAlgorithm alg = new GeneticAlgorithm();
//
//            alg.setMutateValue(0.02);
//            alg.setFirstTerm(0.009);
//
//            if (alg.populationSize > 10) {
//                writeToFile = false;
//            }
//            alg.createPopulation();
//            alg.calculateArithmeticFactor();
//            alg.survivalOfFittest();
//            System.out.println();
//            System.out.println(Arrays.toString(alg.getPopulation().get(0).alphaValues));
//            System.out.println();
//            System.out.println(Arrays.toString(alg.getPopulation().get(0).betaValues));
//            System.out.println();
//            readValues(alg.getPopulation().get(0).betaValues, alg.getPopulation().get(0).alphaValues);
//        }


    }

    public static double runSimulation(double betaVal[], double alphaVal[], boolean child) throws IloException, IOException { // constructor for genetic algorithm
        Network network = Network.createNetwork();
        double waitTime = 0;
        int fleetSize = 25;
        createFleet(fleetSize, network);

        if (child || !writeToFile) waitTime = network.simulate(7200, betaVal, alphaVal, false);
        else if (writeToFile) waitTime = network.simulate(7200, betaVal, alphaVal, true);

        if (network.getTotalNumberOfPassengers() < 130) { // for a fleet size of 25, at least 130 passengers must be picked to use alpha/beta values
            waitTime = -1;
        }

        /*System.out.println("Waiting List after simulation (" + waitingList.size() + ")");
        System.out.println("-----------------------------");

        if (waitingList.size() == 0) {
            System.out.println("           [Empty]           ");
        }
        System.out.println();

        for (Passenger passenger : waitingList) {
            System.out.println(passenger);
        }
        System.out.println();*/
        return waitTime;
    }

    public static void runSimulation(double betaVal[], double alphaVal[], double scale, double ratio) throws IloException, IOException { // constructor for specific fleet to passenger ratio
        double[] waitTime = new double[10];
        double[] inVehicleTravelTime = new double[10];
        double[] totalVehicleTravelTime = new double[10];
        double[] totalMilesTravelled = new double[10];
        double sumOfWaitTimes = 0;
        double sumOfInVehicleTravelTimes = 0; // for each iteration's average (for fleet)
        double sumOfTotalTravelTimes = 0;
        double sumOfMilesTravelled = 0;

        double sumOfInVehicleSeconds = 0; // raw sum for all iterations
        double sumOfTotalTravelSeconds = 0;
        double sumOfTotalDistanceTravelled = 0;
        double totalPassengers = 0;
        double sumOfTotalNumberOfRequestsPerHour = 0;

        double standardDevWaitTime = 0;
        double standardDevInVehicleTravelTime = 0;
        double standardDevTotalVehicleTravelTime = 0;
        double standardDevMilesTravelled = 0;

        for (int i = 0; i < waitTime.length; i++) {
            Network network = Network.createNetwork(scale);
            int time = 7200;

            inVehicleTravelTime[i] = 0; // reset each iteration statistic to allow for correct addition for each vehicle in fleet
            totalVehicleTravelTime[i] = 0;
            totalMilesTravelled[i] = 0;

            int numberOfPassengers = network.getPassengers().size();
            for (Passenger passenger : network.getPassengers()) {   //number of passengers is approximately the number of people picked up + the rest on the waitinglist
                if (passenger.getDeparturetime() > time) {
                    numberOfPassengers--;
                }
            }
            int fleetSize = (int) (ratio * numberOfPassengers);
            createFleet(fleetSize, network);

            if (!writeToFile) waitTime[i] = network.simulate(time, betaVal, alphaVal, false);
            else waitTime[i] = network.simulate(7200, betaVal, alphaVal, true);

            sumOfWaitTimes += waitTime[i];
            sumOfTotalNumberOfRequestsPerHour += network.getTotalNumberOfRequests() / 2;

            for (Vehicle vehicle : network.getVehicleList()) {
                sumOfInVehicleSeconds += vehicle.getInVehicleTravelTime(); //sum for all iterations
                sumOfTotalTravelSeconds += vehicle.getTotalTravelTime();
                sumOfTotalDistanceTravelled += vehicle.getTotalDistanceTravelled();

                inVehicleTravelTime[i] += vehicle.getInVehicleTravelTime(); //sum per iteration
                totalVehicleTravelTime[i] += vehicle.getTotalTravelTime();
                totalMilesTravelled[i] += vehicle.getTotalDistanceTravelled();

            }
            inVehicleTravelTime[i] /= network.getTotalNumberOfPassengers(); //divide by number of passengers to get average per iteration
            totalVehicleTravelTime[i] /= network.getTotalNumberOfPassengers();
            totalMilesTravelled[i] /= network.getVehicleList().size();

            sumOfInVehicleTravelTimes += inVehicleTravelTime[i]; //sum the averages for each iteration to calculate standard deviation
            sumOfTotalTravelTimes += totalVehicleTravelTime[i];
            sumOfMilesTravelled += totalMilesTravelled[i];

            totalPassengers += network.getTotalNumberOfPassengers();
        }

        double avgWaitTime = sumOfWaitTimes / waitTime.length;

        double avgInVehicleTravelTime = sumOfInVehicleTravelTimes / waitTime.length; //average the averages from each iteration
        double avgTotalTravelTime = sumOfTotalTravelTimes / waitTime.length;
        double avgTotalMilesTravelled = sumOfMilesTravelled / waitTime.length;

        double avgTotalRequestsPerHour = sumOfTotalNumberOfRequestsPerHour / waitTime.length;

        for (int i = 0; i < waitTime.length; i++) {
            standardDevWaitTime += Math.pow(waitTime[i] - avgWaitTime, 2);
            standardDevInVehicleTravelTime += Math.pow(inVehicleTravelTime[i] - avgInVehicleTravelTime, 2);
            standardDevTotalVehicleTravelTime += Math.pow(totalVehicleTravelTime[i] - avgTotalTravelTime, 2);
            standardDevMilesTravelled += Math.pow(totalMilesTravelled[i] - avgTotalMilesTravelled, 2);
        }
        standardDevWaitTime = Math.sqrt(standardDevWaitTime / waitTime.length);
        standardDevInVehicleTravelTime = Math.sqrt(standardDevInVehicleTravelTime / waitTime.length);
        standardDevTotalVehicleTravelTime = Math.sqrt(standardDevTotalVehicleTravelTime / waitTime.length);
        standardDevMilesTravelled = Math.sqrt(standardDevMilesTravelled / waitTime.length);

        //System.out.println("Average Wait Time | Average in-vehicle travel time (per passenger) | Average total vehicle travel time (per passenger) | Total miles travelled in fleet);
        System.out.println(avgTotalRequestsPerHour + ": " + avgWaitTime + " " + (sumOfInVehicleSeconds / totalPassengers) + " " + (sumOfTotalTravelSeconds / totalPassengers) + " " + sumOfTotalDistanceTravelled);
        System.out.println(standardDevWaitTime + " " + standardDevInVehicleTravelTime + " " + standardDevTotalVehicleTravelTime + " " + standardDevMilesTravelled);
    }

    public static void runSimulation(double betaVal[], double alphaVal[], double scale, int size) throws IloException, IOException { // constructor for specific fleet size

        int time = 7200;
        double[] waitTime = new double[10];
        double[] inVehicleTravelTime = new double[10];
        double[] passengersPicked = new double[10];
        double[] passengersNotPicked = new double[10];
        double sumOfWaitTimes = 0;
        double sumOfInVehicleTravelTimes = 0;
        double waitTimeSum = 0;
        int vehicleSum = 0;
        double sumOfPassengersNotPicked = 0;
        double sumOfInVehicleSeconds = 0;
        double totalNumberOfPassengers = 0;
        double standardDevWaitTime = 0;
        double standardDevInVehicleTravelTime = 0;
        double standardDevPassengersPicked = 0;
        double standardDevPassengersNotPicked = 0;
        int fleetSize = size;

        for (int i = 0; i < waitTime.length; i++) {

            Network network = Network.createNetwork(scale);
            createFleet(fleetSize, network);

            inVehicleTravelTime[i] = 0;

            if (!writeToFile) waitTime[i] = network.simulate(time, betaVal, alphaVal, false);
            else waitTime[i] = network.simulate(7200, betaVal, alphaVal, true);

            for (Vehicle vehicle : network.getVehicleList()) {
                sumOfInVehicleSeconds += vehicle.getInVehicleTravelTime();
                inVehicleTravelTime[i] += vehicle.getInVehicleTravelTime();
            }
            inVehicleTravelTime[i] /= network.getTotalNumberOfPassengers();
            sumOfInVehicleTravelTimes += inVehicleTravelTime[i];
            passengersPicked[i] += network.getTotalNumberOfPassengers();
            passengersNotPicked[i] += network.getWaitingList().size();
            totalNumberOfPassengers += network.getTotalNumberOfPassengers();
            sumOfWaitTimes += waitTime[i];
            waitTimeSum += network.getSumOfWaitTimes();
            vehicleSum += network.getNumberOfUsedVehicles();
            sumOfPassengersNotPicked += network.getWaitingList().size();
        }

        double avgWaitTime = sumOfWaitTimes / waitTime.length;
        double avgInVehicleTravelTime = sumOfInVehicleTravelTimes / waitTime.length;
        double avgNumberPicked = totalNumberOfPassengers / waitTime.length;
        double avgNumberNotPicked = sumOfPassengersNotPicked / waitTime.length;

        for (int i = 0; i < waitTime.length; i++) {
            standardDevWaitTime += Math.pow(waitTime[i] - avgWaitTime, 2);
            standardDevInVehicleTravelTime += Math.pow(inVehicleTravelTime[i] - avgInVehicleTravelTime, 2);
            standardDevPassengersPicked += Math.pow(passengersPicked[i] - avgNumberPicked, 2);
            standardDevPassengersNotPicked += Math.pow(passengersNotPicked[i] - avgNumberNotPicked, 2);

        }
        standardDevWaitTime = Math.sqrt(standardDevWaitTime / waitTime.length);
        standardDevInVehicleTravelTime = Math.sqrt(standardDevInVehicleTravelTime / waitTime.length);
        standardDevPassengersPicked = Math.sqrt(standardDevPassengersPicked / waitTime.length);
        standardDevPassengersNotPicked = Math.sqrt(standardDevPassengersNotPicked / waitTime.length);


        System.out.println("Average Wait Time: " + avgWaitTime);
        System.out.println("Average in-vehicle travel time: " + (sumOfInVehicleSeconds / totalNumberOfPassengers));
        System.out.println("Average # of Passengers Picked: " + avgNumberPicked);
        System.out.println("Average # of Passengers Not Picked: " + avgNumberNotPicked);
        System.out.println(standardDevWaitTime + " " + standardDevInVehicleTravelTime + " " + standardDevPassengersPicked + " " + standardDevPassengersNotPicked);
    }

    private static void createFleet (int size, Network network) {
        ArrayList<Vehicle> fleet = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            fleet.add(network.makeVehicle(network));
        }

    }

    private static void readValues (double betaVals[], double alphaVals[]) throws IOException {
        Network network = new Network();
        network.createNetwork();
        System.out.println("Alpha Values");
        for (int i = 0; i < alphaVals.length; i++) {
            System.out.println(network.mainNodesList.get(i).getId() + ": " + alphaVals[i]);
        }
        System.out.println();

        System.out.println("Beta Values");
        int iterator = 0;
        for (int i = 0; i < network.mainNodesList.size(); i++) {
            for (int j = 0; j < network.mainNodesList.size(); j++) {
                System.out.println(network.mainNodesList.get(i).getId() + " to " + network.mainNodesList.get(j).getId() + ": " + betaVals[iterator]);
                iterator++;
            }
        }
    }

    public static void isolateNums(String vals) {
        String newString = "";
        int curr = 0;
        while (curr < vals.length() - 13) {
            if (vals.indexOf("\n", curr) == -1) {
                newString += vals.substring(curr + 13);
                break;
            }
           newString += vals.substring(curr + 13, vals.indexOf("\n", curr));
           curr = vals.indexOf("\n", curr) + 2;
        }
        newString = newString.replaceAll(" ", ",\n");
        System.out.println(newString);
    }
}
