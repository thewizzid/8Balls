import nl.saxion.app.CsvReader;
import nl.saxion.app.SaxionApp;

import java.awt.*;
import java.util.ArrayList;


public class Application implements Runnable {

    public static void main(String[] args) {
        SaxionApp.start(new Application(), 1200, 600);
    }
    //48 to 57 are accepted integer ASCII's
    Player player = new Player();
    int dayCount = 1;//Season change every 30 days
    int season = 1;//1 Summer, 2 Autumn, 3 Winter, 4 Spring
    int level = 1;// level of player game 1-3
    Tile[] tiles;
    Crop[] crops;

    public void run() {
        initialization();
    }
    public void initialization (){
        SaxionApp.setFill(Color.black);
        crops = cropSetup();
        tiles = tileSetup();
        drawBoard();
        menu();
    }
    public void drawBoard () {
        SaxionApp.clear();
        SaxionApp.drawImage("resources/background.jpeg", 0, 0,1200,600);
        //starting pos x+400 and y+100
        SaxionApp.setBorderSize(2);
        SaxionApp.setBorderColor(Color.white);

        int[] position = {850,60}; // 0 = X, 1 = Y
        int[] increments = {64,32}; // 0 = X, 1 = Y

        /*SaxionApp.drawLine(position[0],position[1], position[0]+(increments[0]*5), position[1]+(increments[1]*5)); //top line going right
        SaxionApp.drawLine(position[0]-(increments[0]),position[1]+(increments[1]),position[0]+(increments[0]*4), position[1]+(increments[1]*6)); //first divider line, forward slash
        SaxionApp.drawLine(position[0]-(increments[0]*2),position[1]+(increments[1]*2), position[0]+(increments[0]*3), position[1]+(increments[1]*7)); //second divider line, forward slash
        SaxionApp.drawLine(position[0]-(increments[0]*3),position[1]+(increments[1]*3), position[0]+(increments[0]*2), position[1]+(increments[1]*8)); //third divider line, forward slash
        SaxionApp.drawLine(position[0]-(increments[0]*4),position[1]+(increments[1]*4), position[0]+(increments[0]), position[1]+(increments[1]*9)); //fourth divider line, forward slash

        SaxionApp.drawLine(position[0],position[1], position[0]-(increments[0]*5), position[1]+(increments[1]*5)); //top line going left
        SaxionApp.drawLine(position[0]+(increments[0]),position[1]+(increments[1]), position[0]-(increments[0]*4), position[1]+(increments[1]*6)); //first divider line, back slash
        SaxionApp.drawLine(position[0]+(increments[0]*2),position[1]+(increments[1]*2), position[0]-(increments[0]*3), position[1]+(increments[1]*7)); //second divider line, back slash
        SaxionApp.drawLine(position[0]+(increments[0]*3),position[1]+(increments[1]*3), position[0]-(increments[0]*2), position[1]+(increments[1]*8)); //third divider line, back slash
        SaxionApp.drawLine(position[0]+(increments[0]*4),position[1]+(increments[1]*4), position[0]-(increments[0]), position[1]+(increments[1]*9)); //fourth divider line, back slash

        SaxionApp.drawLine(position[0]-(increments[0]*5),position[1]+(increments[1]*5), position[0], position[1]+(increments[1]*10)); //bottom left line going down
        SaxionApp.drawLine(position[0],position[1]+(increments[1]*10), position[0]+(increments[0]*5), position[1]+(increments[1]*5));//bottom right line going down
        */
        SaxionApp.setBorderSize(0);
        drawTiles();
        messageBox(String.valueOf(player.foodCount));
    }
    public void drawTiles(){
        for (Tile tile: tiles) {
            tile.drawTile(level);
        }
    }
    public void menu () {
        drawBoard();
        SaxionApp.printLine("0. Shop");
        SaxionApp.printLine("1. Sell Food or Stock");
        int selection = SaxionApp.readInt();

        switch (selection) {
            case 0 -> shop();
            case 1 -> sell();
        }
    }
    public void sell (){
        drawBoard();
        char selection;
        do {
            int[] counters = {0,0};
            ArrayList<Integer> keyPairs = new ArrayList<>();
            SaxionApp.printLine("0. Back");
            SaxionApp.printLine();
            SaxionApp.printLine(player.name + "'s Stock");
            SaxionApp.printLine();
            for (Crop crop : crops) {
                if (player.cropStock[counters[1]] > 0) {
                    SaxionApp.print((counters[0] + 1) + ". " + crop.cropName + " - x" + player.cropStock[counters[1]] +
                            " - Gives ");
                    SaxionApp.print("▲" + crop.foodPayout, Color.green);
                    SaxionApp.print(" food each");
                    SaxionApp.printLine();
                    keyPairs.add(counters[1]);
                    counters[0]++;
                }
                counters[1]++;
            }
            if (counters[0] > 0) {
                SaxionApp.printLine((counters[0] + 1) + ". Sell all");
            }
            selection = SaxionApp.readChar();
            if ((selection-48) < 0 || (selection-48) > counters[0]){
                SaxionApp.printLine("Invalid selection",Color.red);
                SaxionApp.sleep(1);
                SaxionApp.removeLastPrint();
            }
            else {
                if (selection - 48 == 0) {
                    menu();
                } else {
                    SaxionApp.printLine();
                    SaxionApp.printLine("Quantity?");
                    int quantity = SaxionApp.readInt();
                    if (player.cropStock[keyPairs.get(selection - 49)] < quantity) {
                        SaxionApp.printLine("You do not have this many to sell", Color.red);
                        SaxionApp.sleep(1);
                        for (int i = 0; i < 2; i++) {
                            SaxionApp.removeLastPrint();
                        }
                    } else {
                        SaxionApp.printLine();
                        SaxionApp.printLine("Confirm sale of " + quantity + " " + crops[keyPairs.get(selection - 49)].cropName + "? [Y/N]");
                        char entry = SaxionApp.readChar();
                        do {
                            entry = Character.toUpperCase(entry);
                            switch (entry) {
                                case 'Y' -> {
                                    player.foodCount += quantity * crops[keyPairs.get(selection - 49)].foodPayout;
                                    player.cropStock[keyPairs.get(selection - 49)] -= quantity;
                                    SaxionApp.printLine("Successfully sold " + quantity + "x " + crops[keyPairs.get(selection - 49)].cropName, Color.green);
                                    SaxionApp.sleep(1);
                                    sell();
                                }
                                case 'N' -> sell();
                                default -> {
                                    SaxionApp.printLine("Invalid selection, try again (Y/N)", Color.red);
                                    SaxionApp.sleep(1);
                                    SaxionApp.removeLastPrint();
                                    entry = SaxionApp.readChar();

                                }
                            }
                        } while (entry != 'Y' && entry != 'N');
                    }
                }
            }
        } while (selection-48 != 0);
        menu();
    }
    public void shop(){
        drawBoard();
        SaxionApp.printLine("0. Back");
        SaxionApp.printLine("1. Crops");
        int selection = SaxionApp.readInt();

        switch (selection) {
            case 0 -> menu();
            case 1 -> cropsMenu();
        }
    }
    public void cropsMenu(){
        drawBoard();
        int i = 0;
        SaxionApp.printLine("0. Back");
        for (Crop crop: crops) {
            String month = String.valueOf(crop.season);
            switch (month){
                case "1" -> month = "Summer";
                case "2" -> month = "Autumn";
                case "3" -> month = "Spring";
                case "4" -> month = "Winter";
            }
            SaxionApp.printLine((i+1)+". "+crop.cropName+" - $"+crop.cost+" - "
                    +crop.dayCountdown+" Day/s - ▲"+crop.foodPayout+" - "+month+" ");
            i++;
        }

        boolean[] accepted = {false,false}; //[0]0-8 Crop Selection, [1] QuantityVSCost Selection
        char selection;
        do {
            selection = SaxionApp.readChar();
            if (selection < 48 || selection > 56){
                SaxionApp.printLine("Must be between 0 and 8",Color.red);
                SaxionApp.sleep(1);
                SaxionApp.removeLastPrint();
            }
                else { accepted[0] = true; }
            }
        while (!accepted[0]);

        if (selection == '0'){
                shop();
            }
            else {
                while (!accepted[1]) {
                    SaxionApp.printLine();
                    SaxionApp.printLine("Quantity?");
                    int quantity = SaxionApp.readInt();
                    if (quantity * crops[selection - 49].cost > player.cashCount) {
                        SaxionApp.printLine("You do not have enough cash for this",Color.red);
                        SaxionApp.sleep(1);
                        SaxionApp.clear();
                        cropsMenu();
                    }
                    else {
                        int counter = 0;
                        for (Tile tile: tiles) {
                            if (!tile.occupied[0] && !tile.occupied[1]){
                               counter++;
                            }
                        }
                        if (counter < quantity) {
                            SaxionApp.printLine("Not enough empty tiles");
                            SaxionApp.pause();
                            SaxionApp.removeLastPrint();
                            SaxionApp.removeLastPrint();
                        }
                        else {
                            SaxionApp.printLine();
                            SaxionApp.printLine("Confirm? (Y/N)");
                            SaxionApp.printLine(crops[selection - 49].cropName + " x" + quantity + " for $"
                                    + quantity * crops[selection - 49].cost + "?", Color.green);
                            char entry = SaxionApp.readChar();
                            do {
                            entry = Character.toUpperCase(entry);
                            switch (entry) {
                                case 'Y' -> {
                                    accepted[1] = true;
                                    int identifier = 1;
                                    assignToTile(selection, quantity, identifier);
                                }
                                case 'N' -> cropsMenu();
                                default -> {
                                    SaxionApp.printLine("Invalid selection, try again (Y/N)", Color.red);
                                    SaxionApp.sleep(1);
                                    SaxionApp.removeLastPrint();
                                    entry = SaxionApp.readChar();

                                }
                            }
                            } while (entry != 'Y' && entry != 'N');
                        }
                    }
                }
            }
        }
    public void assignToTile (char indexNumber, int quantity, int identifier) {
        int count = quantity;
        for (int i = 0; i < 5; i++) {
            SaxionApp.removeLastPrint();
        }
        if (!player.automaticPopulation){
            boolean accepted = false;
            while (!accepted) {
                SaxionApp.printLine("Enter tile coordinates (number then letter)");
                String selection = SaxionApp.readString();
                if (selection.length() != 2){
                    SaxionApp.printLine("Only enter two characters (e.g 3C)", Color.red);
                    SaxionApp.sleep(1);
                    for (int i = 0; i < 3; i++) {
                        SaxionApp.removeLastPrint();
                    }
                }
                else {
                    String letterUppercase = selection.substring(1,2).toUpperCase();
                    String[] acceptedValues = {"12345","ABCDE"};
                        if (!acceptedValues[0].contains(selection.substring(0,1)) ||
                                !acceptedValues[1].contains(letterUppercase)){
                            SaxionApp.printLine("Invalid selection", Color.red);
                            SaxionApp.sleep(1);
                            for (int i = 0; i < 3; i++) {
                                SaxionApp.removeLastPrint();
                            }
                        }
                        else {
                            String ID = selection.charAt(0)+letterUppercase;
                            for (Tile tile: tiles) {
                                if (tile.tileID.equals(ID)){
                                    if (tile.occupied[0] || tile.occupied[1]){
                                        SaxionApp.printLine("Tile is already occupied, or locked", Color.red);
                                        SaxionApp.sleep(1);
                                        for (int i = 0; i < 3; i++) {
                                            SaxionApp.removeLastPrint();
                                        }
                                    }
                                    else {
                                        tile.occupied[1] = true;
                                        tile.tileResourceName = crops[indexNumber-49].cropName;
                                        tile.dayCountdown = crops[indexNumber-49].dayCountdown;
                                        tile.season = crops[indexNumber-49].season;
                                        tile.tilePicture = "resources/tile_images/"+tile.tileResourceName+".png";
                                        drawTiles();
                                        SaxionApp.printLine(crops[indexNumber-49].cropName+" successfully planted",Color.green);
                                        SaxionApp.sleep(1);
                                        for (int i = 0; i < 3; i++) {
                                            SaxionApp.removeLastPrint();
                                        }
                                        player.cashCount -= crops[indexNumber-49].cost;
                                        if (count == 1) {
                                            accepted = true;
                                            menu();
                                        }
                                        else {count--;}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        else {
            for (Tile tile: tiles) {
                if (!tile.occupied[0] && !tile.occupied[1] && count > 0){
                    tile.occupied[1] = true;
                    tile.tileResourceName = crops[indexNumber-49].cropName;
                    tile.dayCountdown = crops[indexNumber-49].dayCountdown;
                    tile.season = crops[indexNumber-49].season;
                    tile.tilePicture = "resources/tile_images/"+tile.tileResourceName+".png";
                    drawTiles();
                    SaxionApp.printLine(crops[indexNumber-49].cropName+" successfully planted",Color.green);
                    SaxionApp.sleep(1);
                    player.cashCount -= crops[indexNumber-49].cost;
                    count--;
                }
            }
            menu();
        }
        }
    public Tile[] tileSetup(){
        Tile[] tiles = new Tile[25];
        CsvReader reader = new CsvReader("resources/tileindex.csv");
        reader.skipRow();
        reader.setSeparator(',');
        int topXPos = 784;
        int topYPos = 58;
        int i = 0;
        while (reader.loadRow()){
            Tile tile = new Tile();
            tile.tileID = reader.getString(0);
            tile.tilePosition[0] = topXPos - (i%5)*64;
            tile.tilePosition[1] = topYPos + (i%5)*32;
            tiles[reader.getInt(1)] = tile;
            tile.level = reader.getInt(2);
            if (tile.level > 1){
                tile.occupied[0] = true;
            }
            i++;
            if (i%5 == 0){
                topYPos = topYPos + 32;
                topXPos = topXPos + 64;
            }
        }
        return tiles;
    }
    public Crop[] cropSetup(){
        Crop[] crops = new Crop[8];
        CsvReader reader = new CsvReader("resources/crops.csv");
        reader.skipRow();
        reader.setSeparator(',');
        while (reader.loadRow()){
            Crop crop = new Crop();
            crop.cropName = reader.getString(0);
            crop.cost = reader.getInt(2);
            crop.dayCountdown = reader.getInt(3);
            crop.foodPayout = reader.getInt(4);
            crop.season = reader.getInt(5);
            crops[reader.getInt(1)] = crop;
        }
        return crops;
    }
    public void messageBox (String message){
        SaxionApp.setFill(Color.white);
        SaxionApp.drawBorderedText(message, 800, 500, 18);
    }
}