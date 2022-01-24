package Objects;

import org.javatuples.Pair;

import java.util.*;

public class Controller {
    boolean running = true;
    Road road;
    Random rand = new Random();
    int lightIsGreenFor;
    String lastDirectoryWithGreenLight;

    public Controller() {
        this.road = new Road();
        lightIsGreenFor = 0;
    }

    public void printRoad() {
        road.printRoad();
    }

    //główna funkcja odpowiedzialna za wezwanie metody zmieniającej pozycje samochodów, zarządzanie sygnalizacją oraz kontrolę czasu
    public void run() {
        changeCarPositions();
        menageTrafficLights();
        lightIsGreenFor += 3;
    }

    //funkcja zarządzająca sygnalizacją
    //kiedy zmienna "directory" posiada wartość null jej zadaniem jest wywołanie funkcji zmieniającej światła z koloru żółtego
    //w przeciwnym wypadku na podstawie
    // - zliczonej ilości samochodów w kolejce do skrzyżowania,
    // - czasu przez który dla danego kierunku ustawione jest zielone światło,
    // - informacji czy na pierwszym miejscu przed światłami znajduje się samochód
    //podejmowana zostaje decyzja o zmianie koloru sygnalizacji
    private void menageTrafficLights() {
        String directory = directoryWithGreenLight();

        if (directory == null) {
            changeLightFromYellow();
        } else {
            int numberOfCars = numberOfCars();
            boolean polePosition = carIsOnFirstPositionToLights();
            if (lightIsGreenFor >= 9 && lightIsGreenFor <= 15) {
                if (numberOfCars < 3 && !polePosition) {
                    changeLightsToYellow(directory);
                }
            } else if (lightIsGreenFor > 15 && lightIsGreenFor <= 21) {
                if (numberOfCars < 5 && !polePosition) {
                    changeLightsToYellow(directory);
                }
            } else if (lightIsGreenFor > 21 &&  lightIsGreenFor == 27) {
                changeLightsToYellow(directory);
            }
        }
    }

    private boolean carIsOnFirstPositionToLights() {
        String directory = directoryWithGreenLight();
        boolean numberOfCars = false;
        switch (directory) {
            case "N":
                numberOfCars = road.getNin().get(0);
                break;
            case "S":
                numberOfCars = road.getSin().get(0);
                break;
            case "E":
                numberOfCars = road.getEin().get(0);
                break;
            case "W":
                numberOfCars = road.getWin().get(0);
                break;
        }

        return numberOfCars;
    }

    //funkcja zmieniająca kolor świateł na skrzyżowaniu zachowująca odpowiednią sekwencję (światła zmieniają się zgodnie z ruchem wskazówek zegara)
    //na podstawie zapisanej informacji o kierunku z ostatnio przydzielonym zielonym światłem
    private void changeLightFromYellow() {
        switch (lastDirectoryWithGreenLight) {
            case "N":
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.RED, LightsColour.GREEN, LightsColour.RED, LightsColour.RED));
                break;
            case "E":
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.RED, LightsColour.RED, LightsColour.GREEN, LightsColour.RED));
                break;
            case "S":
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.RED, LightsColour.RED, LightsColour.RED, LightsColour.GREEN));
                break;
            default:
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.GREEN, LightsColour.RED, LightsColour.RED, LightsColour.RED));
                break;
        }
        lightIsGreenFor = 0;
    }

    //funkcja zmieniająca kolor świateł w na zółty w dla kierunku który właśnie kończy ruch, oraz dla kierunku który będzie go zaczynał
    private void changeLightsToYellow(String directory) {
        switch (directory) {
            case "N":
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.YELLOW, LightsColour.YELLOW, LightsColour.RED, LightsColour.RED));
                break;
            case "E":
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.RED, LightsColour.YELLOW, LightsColour.YELLOW, LightsColour.RED));
                break;
            case "S":
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.RED, LightsColour.RED, LightsColour.YELLOW, LightsColour.YELLOW));
                break;
            default:
                road.setLightsColourMap(road.prepareLightsMap(LightsColour.YELLOW, LightsColour.RED, LightsColour.RED, LightsColour.YELLOW));
                break;
        }
        lastDirectoryWithGreenLight = directory;
    }

    //zliczanie samochodów w kolejce dla kierunku z aktualnie włączonym zielonym światłem
    private Integer numberOfCars() {
        int numberOfCars = 0;
        String directory = directoryWithGreenLight();
        switch (directory) {
            case "N":
                numberOfCars = calculateNumberOfCars(road.getNin());
                break;
            case "S":
                numberOfCars = calculateNumberOfCars(road.getSin());
                break;
            case "E":
                numberOfCars = calculateNumberOfCars(road.getEin());
                break;
            case "W":
                numberOfCars = calculateNumberOfCars(road.getWin());
                break;
        }

        return numberOfCars;
    }

    private Integer calculateNumberOfCars(List<Boolean> cars) {
        int i = 0;
        for (Boolean car : cars) {
            if (car) {
                i++;
            }
        }
        return i;
    }

    public boolean isRunning() {
        return running;
    }

    //funkcja zwracająca kierunek dla którego aktualnie zostało przydzielone zielone światło lub null jeżeli żaden z kierunków nie ma zielonego światła
    private String directoryWithGreenLight() {
        Map<String, LightsColour> map = road.getLightsColourMap();
        String directory = null;

        for (String key : map.keySet()) {
            if (map.get(key).equals(LightsColour.GREEN)) {
                directory = key;
            }
        }
        return directory;
    }

    //funkcja odpowiedzialna za zmianę pozycji samochodów
    //w pierwszej kolejności przesuwane są samochody znajdujące się na drogach wyjazdowych ze skrzyżowania
    //następnie wywoływana jest funkcja odpowiedziala za przesunięcie samochodów z centrum skrzyzowania na drogę wyjazdową oraz w jego obrębie
    //później, jeżeli któreś ze świateł jest zielone wywołana zostaje funkcja przenosząca pojazd z drogi prowadzącej do skrzyżowania na jego centrum
    //następnie przenoszone są saochody znajdujące się w kolejce do skrzyżowania
    //ostatnim etapem jest wygenerowanie nowych samochodów
    private void changeCarPositions() {
        String directory = directoryWithGreenLight();
        road.setNout(moveCarsOnePositionInQueue(road.getNout(), true));
        road.setSout(moveCarsOnePositionInQueue(road.getSout(), true));
        road.setEout(moveCarsOnePositionInQueue(road.getEout(), true));
        road.setWout(moveCarsOnePositionInQueue(road.getWout(), true));
        menageCentreOfCrossing(directory);
        if(directory != null){
            moveCarToTheCrossing(directory);
        }
        road.setNin(moveCarsOnePositionInQueue(road.getNin(), false));
        road.setSin(moveCarsOnePositionInQueue(road.getSin(), false));
        road.setEin(moveCarsOnePositionInQueue(road.getEin(), false));
        road.setWin(moveCarsOnePositionInQueue(road.getWin(), false));
        generateNewCars();
    }

    //funkcja na podstawie liczby pseudolosowej generuje na każdej z dróg nowy pojazd z prawdopodobieństwem 1/6
    private void generateNewCars() {
        List<Boolean> tmpList;
        if (rand.nextInt(6) == 0) {
            tmpList = road.getNin();
            tmpList.set(9, true);
            road.setNin(tmpList);
        }
        if (rand.nextInt(6) == 0) {
            tmpList = road.getSin();
            tmpList.set(9, true);
            road.setSin(tmpList);
        }
        if (rand.nextInt(6) == 0) {
            tmpList = road.getEin();
            tmpList.set(9, true);
            road.setEin(tmpList);
        }
        if (rand.nextInt(6) == 0) {
            tmpList = road.getWin();
            tmpList.set(9, true);
            road.setWin(tmpList);
        }

    }

    //funkcja przenosząca pojazd z pozycji 0 w kolejce do skrzyżowania na samo skrzyżowanie przy okazji losując i zapisując dla niego kierunek jazdy
    private void moveCarToTheCrossing(String directory) {
        List<Pair<Boolean, String>> centreOfCrossing = road.getCentreOfCrossing();
        int randInt = rand.nextInt(3) + 1;
        String directoryToGo;
        List<Boolean> tmpList;
        switch (directory) {
            case "N":
                if(road.getNin().get(0)){
                    if (randInt == 1) {
                        directoryToGo = "S";
                    } else if (randInt == 2) {
                        directoryToGo = "E";
                    } else {
                        directoryToGo = "W";
                    }
                    centreOfCrossing.set(1, new Pair<>(true, directoryToGo));
                    tmpList = road.getNin();
                    tmpList.set(0, false);
                    road.setNin(tmpList);
                }
                break;
            case "S":
                if(road.getSin().get(0)){
                    if (randInt == 1) {
                        directoryToGo = "N";
                    } else if (randInt == 2) {
                        directoryToGo = "E";
                    } else {
                        directoryToGo = "W";
                    }
                    centreOfCrossing.set(3, new Pair<>(true, directoryToGo));
                    tmpList = road.getSin();
                    tmpList.set(0, false);
                    road.setSin(tmpList);
                    break;
                }
            case "E":
                if(road.getEin().get(0)){
                    if (randInt == 1) {
                        directoryToGo = "S";
                    } else if (randInt == 2) {
                        directoryToGo = "N";
                    } else {
                        directoryToGo = "W";
                    }
                    centreOfCrossing.set(0, new Pair<>(true, directoryToGo));
                    tmpList = road.getEin();
                    tmpList.set(0, false);
                    road.setEin(tmpList);
                    break;
                }
            case "W":
                if(road.getWin().get(0)){
                    if (randInt == 1) {
                        directoryToGo = "S";
                    } else if (randInt == 2) {
                        directoryToGo = "E";
                    } else {
                        directoryToGo = "N";
                    }
                    centreOfCrossing.set(2, new Pair<>(true, directoryToGo));
                    tmpList = road.getWin();
                    tmpList.set(0, false);
                    road.setWin(tmpList);
                    break;
                }
        }
    }

    //zarządzanie centrum skrzyżowania
    private void menageCentreOfCrossing(String directory) {
        if (directory == null) {
            actionWhenLightIsYellow(road.getCentreOfCrossing());
        } else {
            actionWhenAnyOfTheLightsIsGreen(directory, road.getCentreOfCrossing());
        }


    }

    //zadaniem jest wywołanie odpowiedniejmetod w zależnosci od aktualnego kierunku z zielonym światłem
    private void actionWhenAnyOfTheLightsIsGreen(String directory, List<Pair<Boolean, String>> centreOfCrossing) {
        switch (directory) {
            case "N":
                greenLightAndDirectoryN(centreOfCrossing);
                break;
            case "S":
                greenLightAndDirectoryS(centreOfCrossing);
                break;
            case "E":
                greenLightAndDirectoryE(centreOfCrossing);
                break;
            default:
                greenLightAndDirectoryW(centreOfCrossing);
                break;
        }

    }

    //jeżeli aktualnym kierunkiem z zielonym śiwatłem jest zachód oraz

    //1.  w centrum skrzyżowania na pozycji 3(pozycje opisane w klasie Road) znajduje się pojazd
    //zostaje on przeniesiony na drogę wyjazdową na wschód ze skrzyżowania a jego poprzednie miejsce usunięte

    //2.  w centrum skrzyżowania na pozycji 0(pozycje opisane w klasie Road) znajduje się pojazd
    //zostaje on przeniesiony na drogę wyjazdową na północ ze skrzyżowania a jego poprzednie miejsce usunięte

    //2.  w centrum skrzyżowania na pozycji 2(pozycje opisane w klasie Road) znajduje się pojazd
    // na podstawie wcześniej wylosowanego kierunku jazdy zostanie mu przydzielona pozycja 0, 3,
    // lub zostanie przeniesiony na drogę wyjazdową prowadzącą na zachód od skrzyżowania

    private void greenLightAndDirectoryW(List<Pair<Boolean, String>> centreOfCrossing) {
        List<Boolean> tmpList;
        if (centreOfCrossing.get(3).getValue0()) {
            tmpList = road.getEout();
            tmpList.set(9, true);
            road.setEout(tmpList);
            centreOfCrossing.set(3, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        } else if (centreOfCrossing.get(0).getValue0()) {
            tmpList = road.getNout();
            tmpList.set(9, true);
            road.setNout(tmpList);
            centreOfCrossing.set(0, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
        if (centreOfCrossing.get(2).getValue0()) {
            String directoryToGo = centreOfCrossing.get(2).getValue1();
            if (directoryToGo.equals("N")) {
                centreOfCrossing.set(0, new Pair<>(true, "N"));
            } else if (directoryToGo.equals("E")) {
                centreOfCrossing.set(3, new Pair<>(true, "E"));
            } else {
                tmpList = road.getSout();
                tmpList.set(9, true);
                road.setSout(tmpList);
            }
            centreOfCrossing.set(2, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
    }

    private void greenLightAndDirectoryE(List<Pair<Boolean, String>> centreOfCrossing) {
        List<Boolean> tmpList;
        if (centreOfCrossing.get(1).getValue0()) {
            tmpList = road.getWout();
            tmpList.set(9, true);
            road.setWout(tmpList);
            centreOfCrossing.set(1, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        } else if (centreOfCrossing.get(2).getValue0()) {
            tmpList = road.getSout();
            tmpList.set(9, true);
            road.setSout(tmpList);
            centreOfCrossing.set(2, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
        if (centreOfCrossing.get(0).getValue0()) {
            String directoryToGo = centreOfCrossing.get(0).getValue1();
            if (directoryToGo.equals("W")) {
                centreOfCrossing.set(1, new Pair<>(true, "W"));
            } else if (directoryToGo.equals("S")) {
                centreOfCrossing.set(2, new Pair<>(true, "S"));
            } else {
                tmpList = road.getNout();
                tmpList.set(9, true);
                road.setNout(tmpList);
            }
            centreOfCrossing.set(0, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
    }

    private void greenLightAndDirectoryN(List<Pair<Boolean, String>> centreOfCrossing) {
        List<Boolean> tmpList;
        if (centreOfCrossing.get(2).getValue0()) {
            tmpList = road.getSout();
            tmpList.set(9, true);
            road.setSout(tmpList);
            centreOfCrossing.set(2, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        } else if (centreOfCrossing.get(3).getValue0()) {
            tmpList = road.getEout();
            tmpList.set(9, true);
            road.setEout(tmpList);
            centreOfCrossing.set(3, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
        if (centreOfCrossing.get(1).getValue0()) {
            String directoryToGo = centreOfCrossing.get(1).getValue1();
            if (directoryToGo.equals("S")) {
                centreOfCrossing.set(2, new Pair<>(true, "S"));
            } else if (directoryToGo.equals("E")) {
                centreOfCrossing.set(3, new Pair<>(true, "E"));
            } else {
                tmpList = road.getWout();
                tmpList.set(9, true);
                road.setWout(tmpList);
            }
            centreOfCrossing.set(1, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
    }

    private void greenLightAndDirectoryS(List<Pair<Boolean, String>> centreOfCrossing) {
        List<Boolean> tmpList;
        if (centreOfCrossing.get(0).getValue0()) {
            tmpList = road.getNout();
            tmpList.set(9, true);
            road.setNout(tmpList);
            centreOfCrossing.set(0, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        } else if (centreOfCrossing.get(1).getValue0()) {
            tmpList = road.getWout();
            tmpList.set(9, true);
            road.setWout(tmpList);
            centreOfCrossing.set(1, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
        if (centreOfCrossing.get(3).getValue0()) {
            String directoryToGo = centreOfCrossing.get(3).getValue1();
            if (directoryToGo.equals("N")) {
                centreOfCrossing.set(0, new Pair<>(true, "N"));
            } else if (directoryToGo.equals("W")) {
                centreOfCrossing.set(1, new Pair<>(true, "W"));
            } else {
                tmpList = road.getEout();
                tmpList.set(9, true);
                road.setEout(tmpList);
            }
            centreOfCrossing.set(3, new Pair<>(false, "null"));
            road.setCentreOfCrossing(centreOfCrossing);
        }
    }

    //kiedy światło jest żółte samochody znajdują się już na pozycjach najbliższych do wyjazdu zostają zatem przeniesione na odpowiednie drogi wyjazdowe
    private void actionWhenLightIsYellow(List<Pair<Boolean, String>> centreOfCrossing) {
        for (int i = 0; i < 4; i++) {
            Pair<Boolean, String> pair = centreOfCrossing.get(i);
            if (pair.getValue0()) {
                List<Boolean> tmpList;
                switch (i) {
                    case 0:
                        tmpList = road.getNout();
                        tmpList.set(9, true);
                        road.setNout(tmpList);
                        break;
                    case 2:
                        tmpList = road.getSout();
                        tmpList.set(9, true);
                        road.setSout(tmpList);
                        break;
                    case 3:
                        tmpList = road.getEout();
                        tmpList.set(9, true);
                        road.setEout(tmpList);
                        break;
                    case 1:
                        tmpList = road.getWout();
                        tmpList.set(9, true);
                        road.setWout(tmpList);
                        break;
                }
                i++;
            }
        }
        List<Pair<Boolean, String>> emptyCentreOfCrossing = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Pair<Boolean, String> pair = new Pair<>(false, "x");
            emptyCentreOfCrossing.add(pair);
        }
        road.setCentreOfCrossing(emptyCentreOfCrossing);
    }

    //funkcja odpowiedzialna za przenoszenie samochodów w kolejkach
    private List<Boolean> moveCarsOnePositionInQueue(List<Boolean> x, Boolean isLeavingTheCrossing) {
        int totalNumberOfCarsInQueue = 0;
        for (int i = 0; i < x.size(); i++) {
            if(x.get(i)){
                totalNumberOfCarsInQueue++;
            }
        }
        for (int i = 0; i < x.size() - 1; i++) {
            if(i == 0 && isLeavingTheCrossing){
                x.set(i+1, false);
            }
            if(!x.get(i)){
                x.set(i, x.get(i + 1));
                x.set(i+1, false);
            }
        }
        if(totalNumberOfCarsInQueue<10){
            x.set(9, false);
        }
        return x;
    }
}
