package prototype.database;

import java.util.ArrayList;
import java.util.HashMap;

public class DBTester {


    public static void oldCode(){
        DatabaseManager manager = new DatabaseManager("jdbc:sqlite:test.db");
        ArrayList<DatabaseManager.Column> cols = new ArrayList<DatabaseManager.Column>();
        cols.add(new DatabaseManager.Column("ID", DatabaseManager.Type.INTEGER, true, true, true));
        cols.add(new DatabaseManager.Column("Love", DatabaseManager.Type.REAL, false, true, true));
        cols.add(new DatabaseManager.Column("Target", DatabaseManager.Type.TEXT, false, true, true));
        cols.add(new DatabaseManager.Column("Description", DatabaseManager.Type.TEXT));
        manager.createTable("Amor", cols);

        DatabaseManager.Column progression = new DatabaseManager.Column("Progression", DatabaseManager.Type.REAL, false, false, false);
        manager.addColumn("Amor", progression);
        manager.addColumn("Amor", "Importance", DatabaseManager.Type.INTEGER);

        ArrayList<String> columns = manager.getColumns("Amor");
        for (String name : columns){
            ;System.out.println(name);
        }

        HashMap<String, Object> newRow = new HashMap<String, Object>();
        newRow.put("ID", 1);
        newRow.put("Love", 7.5);
        newRow.put("Target", "Food");
        newRow.put("Description", "Who does not love food?");
        newRow.put("Progression", 3.337);
        newRow.put("Importance", 6);
        manager.addRow("Amor", newRow);

        newRow.put("ID", 2);
        newRow.put("Love", 7);
        newRow.put("Target", "Dogs");
        newRow.put("Description", "The Best Friend of Man");
        newRow.put("Progression", 8.5);
        manager.addRow("Amor", newRow);

        HashMap<String, Object>  newVals = new HashMap<String, Object>();
        newVals.put("Love", 9);
        newVals.put("Importance", 5);
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("Target", "Food");
        manager.modifyRows("Amor", newVals, conditions, true);

        newVals.put("Progression", 5);
        conditions.put("ID", 9);
        manager.modifyRows("Amor", newVals, conditions, true);

        newVals.put("Progression", 1.337);
        conditions.put("Description", "Allah");
        manager.modifyRows("Amor", newVals, conditions, false);

        newVals.put("Love", 7.2);
        newVals.remove("Importance");
        newVals.remove("Progression");
        manager.modifyRows("Amor", newVals, "Target", "Dogs");

        newVals = new HashMap<String, Object>();
        newVals.put("Importance", 5);
        manager.modifyRows("Amor", newVals);

        ArrayList<String> queryCols = new ArrayList<String>();
        queryCols.add("Target");
        queryCols.add("Love");
        queryCols.add("Importance");
        ArrayList<HashMap<String, Object>> results = manager.queryElements("Amor", queryCols);

        newRow = new HashMap<String, Object>();
        newRow.put("ID", 3);
        newRow.put("Love", 6.5);
        newRow.put("Target", "League");
        newRow.put("Description", "A pastime of old. Just how much money have I spent on this game?");
        newRow.put("Progression", 0);
        newRow.put("Importance", 2);
        manager.addRow("Amor", newRow);

        queryCols = new ArrayList<String>();
        queryCols.add("Target");
        queryCols.add("Love");
        queryCols.add("Importance");
        conditions = new HashMap<String, Object>();
        conditions.put("Love", 6.5);
        results = manager.queryElements("Amor", queryCols, conditions, true);
        conditions.put("Importance", 5);
        results = manager.queryElements("Amor", queryCols, conditions, false);
        results = manager.queryElements("Amor", queryCols, conditions, true);
    }

    public static void Test2(){
        DatabaseManager manager = new DatabaseManager("jdbc:sqlite:test.db");
        manager.deleteTable("Characters");

        //Creating the Table
        ArrayList<DatabaseManager.Column> columns = new ArrayList<DatabaseManager.Column>();
        columns.add(new DatabaseManager.Column("Name", DatabaseManager.Type.TEXT, true, true, true));
        manager.createTable("Characters", columns);

        //Adding fresh columns
        manager.addColumn("Characters", "Faction", DatabaseManager.Type.TEXT);
        manager.addColumn("Characters", "Role", DatabaseManager.Type.TEXT);
        manager.addColumn("Characters", "Importance", DatabaseManager.Type.INTEGER);
        manager.addColumn("Characters", new DatabaseManager.Column("Loyalty", DatabaseManager.Type.REAL, false, false, true));

        //Adding Data
        HashMap<String, Object> rowData = new HashMap<String, Object>();

        //Albedo
        rowData.put("Name", "Albedo");
        rowData.put("Role", "Guardian Overseer");
        rowData.put("Faction", "Ainz Ooal Gown");
        rowData.put("Importance", 9);
        rowData.put("Loyalty", 10.1);
        manager.addRow("Characters", rowData);

        //Sejuani
        rowData.put("Name", "Sejuani");
        rowData.put("Role", "The Wrath of Winter");
        rowData.put("Faction", "Freljord");
        rowData.put("Importance", 8);
        rowData.put("Loyalty", 3.12);
        manager.addRow("Characters", rowData);

        //Lycindria
        rowData.put("Name", "Lycindria");
        rowData.put("Role", "Apostle of Armadyl");
        rowData.put("Faction", "Gielinor");
        rowData.put("Importance", 2);
        rowData.put("Loyalty", 6);
        manager.addRow("Characters", rowData);

        //Climb
        rowData.put("Name", "Climb");
        rowData.remove("Role");
        rowData.put("Faction", "Re Estize");
        rowData.put("Importance", 4);
        rowData.put("Loyalty", 9.8);
        manager.addRow("Characters", rowData);

        //Holo
        rowData.put("Name", "Holo");
        rowData.put("Role", "Wise Wolf");
        rowData.put("Faction", "Rowen Trading Guild");
        rowData.put("Importance", 7);
        rowData.put("Loyalty", 3);
        manager.addRow("Characters", rowData);

        //Lawrence
        rowData.put("Name", "Lawrence");
        rowData.put("Role", "Wandering Merchant");
        rowData.put("Faction", "Rowen Trading Guild");
        rowData.put("Importance", 4);
        rowData.put("Loyalty", 5.4);
        manager.addRow("Characters", rowData);

        //Fermi Amarti
        rowData.put("Name", "Fermi Amarti");
        rowData.remove("Role");
        rowData.put("Faction", "Rowen Trading Guild");
        rowData.remove("Importance");
        rowData.remove("Loyalty");
        manager.addRow("Characters", rowData);

        //Full Query and print of data in table
        printAll(manager.queryElements("Characters", manager.getColumns("Characters")));

        //Remove Row element
        manager.removeRow("Characters", "Faction", "Re Estize");
        printAll(manager.queryElements("Characters", manager.getColumns("Characters")));

        //Selection Queries
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("Faction", "Rowen Trading Guild");
        printAll(manager.queryElements("Characters", manager.getColumns("Characters"), conditions, true));

        conditions.put("Name", "Albedo");
        printAll(manager.queryElements("Characters", manager.getColumns("Characters"), conditions, true));
        printAll(manager.queryElements("Characters", manager.getColumns("Characters"), conditions, false));

        conditions.put("Faction", "Ainz Ooal Gown");
        conditions.put("Name", "Climb");
        conditions.put("Loyalty", 3);
        conditions.put("Importance", 4);
        conditions.put("Role", "Apostle of Armadyl");
        conditions.put("Role", "Queen of Freljord");
        printAll(manager.queryElements("Characters", manager.getColumns("Characters"), conditions, false));
    }

    private static void printAll( ArrayList<HashMap<String, Object>> results){
        for (HashMap<String, Object> row : results){
            for (String col : row.keySet()){
                System.out.print(col + ": " + row.get(col) + ", \t");
            }
            System.out.println();
        }
        System.out.println("End of Query\n\n\n");
    }

    public static void main(String[]args){
        //oldCode();
        Test2();

        DatabaseManager manager = new DatabaseManager("jdbc:sqlite:test.db");

    }
}
