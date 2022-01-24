package Objects;

import org.javatuples.Pair;

import java.util.*;

public class Road {

    //index 0  in list stands for first position in queue to traffic lights
    List<Boolean> Nin;
    List<Boolean> Sin;
    List<Boolean> Ein;
    List<Boolean> Win;
    //index 0  in list stands for first car that will left crossing
    List<Boolean> Nout;
    List<Boolean> Sout;
    List<Boolean> Eout;
    List<Boolean> Wout;

    //___ ___ index in list stands for position on crossing shown below
    // 1 | 0
    //-------
    // 2 | 3
    List<Pair<Boolean, String>> centreOfCrossing = new ArrayList<>();
    Map<String, LightsColour> lightsColourMap;


    public Road() {
        Nin = setInitialValues();
        Sin = setInitialValues();
        Ein = setInitialValues();
        Win = setInitialValues();
        Nout = setInitialValues();
        Sout = setInitialValues();
        Eout = setInitialValues();
        Wout = setInitialValues();
        for (int i = 0; i < 4; i++) {
            Pair<Boolean, String> pair = new Pair<>(false, "x");
            centreOfCrossing.add(pair);
        }
        lightsColourMap = prepareLightsMap(LightsColour.GREEN, LightsColour.RED, LightsColour.RED, LightsColour.RED);
    }


    public void printRoad() {
        System.out.printf("%10s %s", "Północ:", lightsColourMap.get("N"));
        System.out.printf("\n%10s %s", "Południe:", lightsColourMap.get("S"));
        System.out.printf("\n%10s %s", "Wschód:", lightsColourMap.get("E"));
        System.out.printf("\n%10s %s", "Zachód:", lightsColourMap.get("W"));
        printVerticalRoad(Nin, Nout);
        printHorizontalRoad();
        printVerticalRoad(Sout, Sin);
    }


    private void printVerticalRoad(List<Boolean> x, List<Boolean> y) {
        System.out.printf("\n%32s %s | %s ||\n", "||", returnXifCarExists(x.get(9)), returnXifCarExists(y.get(0)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(8)), returnXifCarExists(y.get(1)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(7)), returnXifCarExists(y.get(2)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(6)), returnXifCarExists(y.get(3)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(5)), returnXifCarExists(y.get(4)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(4)), returnXifCarExists(y.get(5)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(3)), returnXifCarExists(y.get(6)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(2)), returnXifCarExists(y.get(7)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(1)), returnXifCarExists(y.get(8)));
        System.out.printf("%32s %s | %s ||\n", "||", returnXifCarExists(x.get(0)), returnXifCarExists(y.get(9)));
    }

    private void printHorizontalRoad() {
        System.out.print("=============================      |      =============================\n");
        System.out.printf("%2s %2s %2s %2s %2s %2s %2s %2s %2s %2s", returnXifCarExists(Wout.get(0)), returnXifCarExists(Wout.get(1)), returnXifCarExists(Wout.get(2)), returnXifCarExists(Wout.get(3)), returnXifCarExists(Wout.get(4)), returnXifCarExists(Wout.get(5)), returnXifCarExists(Wout.get(6)), returnXifCarExists(Wout.get(7)), returnXifCarExists(Wout.get(8)), returnXifCarExists(Wout.get(9)));
        System.out.printf("%5s | %2s", returnXifCarExists(centreOfCrossing.get(1).getValue0()), returnXifCarExists(centreOfCrossing.get(0).getValue0()));
        System.out.printf("%4s %2s %2s %2s %2s %2s %2s %2s %2s %2s", returnXifCarExists(Ein.get(0)), returnXifCarExists(Ein.get(1)), returnXifCarExists(Ein.get(2)), returnXifCarExists(Ein.get(3)), returnXifCarExists(Ein.get(4)), returnXifCarExists(Ein.get(5)), returnXifCarExists(Ein.get(6)), returnXifCarExists(Ein.get(7)), returnXifCarExists(Ein.get(8)), returnXifCarExists(Ein.get(9)));
        System.out.print("\n-----------------------------------|----------------------------------- \n");
        System.out.printf("%2s %2s %2s %2s %2s %2s %2s %2s %2s %2s", returnXifCarExists(Win.get(9)), returnXifCarExists(Win.get(8)), returnXifCarExists(Win.get(7)), returnXifCarExists(Win.get(6)), returnXifCarExists(Win.get(5)), returnXifCarExists(Win.get(4)), returnXifCarExists(Win.get(3)), returnXifCarExists(Win.get(2)), returnXifCarExists(Win.get(1)), returnXifCarExists(Win.get(0)));
        System.out.printf("%5s | %2s", returnXifCarExists(centreOfCrossing.get(2).getValue0()), returnXifCarExists(centreOfCrossing.get(3).getValue0()));
        System.out.printf("%4s %2s %2s %2s %2s %2s %2s %2s %2s %2s", returnXifCarExists(Eout.get(9)), returnXifCarExists(Eout.get(8)), returnXifCarExists(Eout.get(7)), returnXifCarExists(Eout.get(6)), returnXifCarExists(Eout.get(5)), returnXifCarExists(Eout.get(4)), returnXifCarExists(Eout.get(3)), returnXifCarExists(Eout.get(2)), returnXifCarExists(Eout.get(1)), returnXifCarExists(Eout.get(0)));
        System.out.print("\n=============================      |      =============================");
    }

    private String returnXifCarExists(boolean car) {
        if (car) {
            return "x";
        } else return " ";
    }

    //przygotowanie pustej listy reprezentującej pozycje w kolejce do/ze skrzyżowania
    private List<Boolean> setInitialValues() {
        List<Boolean> newArrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            newArrayList.add(false);
        }
        return newArrayList;
    }

    //przygotowanie mapy przechowującej kolory sygnalizacji świetlnej
    public Map<String, LightsColour> prepareLightsMap(LightsColour n, LightsColour e ,LightsColour s ,LightsColour w) {

        return new HashMap<String, LightsColour>() {{
            put("N", n);
            put("S", s);
            put("E", e);
            put("W", w);
        }};
    }

    public Map<String, LightsColour> getLightsColourMap() {
        return lightsColourMap;
    }

    public void setLightsColourMap(Map<String, LightsColour> lightsColourMap) {
        this.lightsColourMap = lightsColourMap;
    }

    public void setNin(List<Boolean> nin) {
        Nin = nin;
    }

    public void setSin(List<Boolean> sin) {
        Sin = sin;
    }

    public void setEin(List<Boolean> ein) {
        Ein = ein;
    }

    public void setWin(List<Boolean> win) {
        Win = win;
    }

    public void setNout(List<Boolean> nout) {
        Nout = nout;
    }

    public void setSout(List<Boolean> sout) {
        Sout = sout;
    }

    public void setEout(List<Boolean> eout) {
        Eout = eout;
    }

    public void setWout(List<Boolean> wout) {
        Wout = wout;
    }

    public List<Boolean> getNin() {
        return Nin;
    }

    public List<Boolean> getSin() {
        return Sin;
    }

    public List<Boolean> getEin() {
        return Ein;
    }

    public List<Boolean> getWin() {
        return Win;
    }

    public List<Boolean> getNout() {
        return Nout;
    }

    public List<Boolean> getSout() {
        return Sout;
    }

    public List<Boolean> getEout() {
        return Eout;
    }

    public List<Boolean> getWout() {
        return Wout;
    }

    public List<Pair<Boolean, String>> getCentreOfCrossing() {
        return centreOfCrossing;
    }

    public void setCentreOfCrossing(List<Pair<Boolean, String>> centreOfCrossing) {
        this.centreOfCrossing = centreOfCrossing;
    }
}
