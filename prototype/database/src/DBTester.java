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
        newRow.put("Description", "A pastime of old. Just how much money hav e I spent on this game?");
        newRow.put("Progression", 0);
        newRow.put("Importance", 2);
        manager.addRow("Amor", newRow);
    }

    public static void main(String[]args){
        oldCode();
        DatabaseManager manager = new DatabaseManager("jdbc:sqlite:test.db");

        HashMap<String, Object> newVals;

        ArrayList<String> queryCols = new ArrayList<String>();
        queryCols.add("Target");
        queryCols.add("Love");
        queryCols.add("Importance");
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put("Love", 6.5);
        ArrayList<HashMap<String, Object>> results = manager.queryElements("Amor", queryCols, conditions, true);
        conditions.put("Importance", 5);
        results = manager.queryElements("Amor", queryCols, conditions, false);
        results = manager.queryElements("Amor", queryCols, conditions, true);

        //Code that works but I don't want to run
        //manager.removeRow("Amor", "ID", 1);
        //manager.deleteTable("Amor");
    }
}
