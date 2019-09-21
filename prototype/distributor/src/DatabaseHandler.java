package prototype.distributor;

import prototype.database.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHandler {

    private DatabaseManager manager;

    public DatabaseHandler(String database){
        manager = new DatabaseManager(database);

        ArrayList<DatabaseManager.Column> cols = new ArrayList<DatabaseManager.Column>();
        cols.add(new DatabaseManager.Column("Username", DatabaseManager.Type.TEXT, true, true, true));

        //Will fail if the table already exists
        if (manager.createTable("User_List", cols)){
            //Creates the users table, should only run once. The table should only be manually deleted/reset
            manager.addColumn("User_List", new DatabaseManager.Column("Password_Hash", DatabaseManager.Type.TEXT, false, false, true));
        }
    }

    public boolean addUser(String username, String password){
        HashMap<String, Object> newRow = new HashMap<>();
        newRow.put("Username", username);
        newRow.put("Password_Hash", password);
        return  manager.addRow("User_List", newRow);
    }

    public boolean removeUser(String username){
        return manager.removeRow("User_List", "Username", username);
    }

    public boolean hasUser(String username){
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("Username", username)
        ;
        ArrayList<HashMap<String, Object>> rs = manager.queryElements("User_List", manager.getColumns("User_List"), conditions, true);
        if (rs == null){
            return false;
        }
        return rs.size() > 0;    }

    public boolean checkCredentials(String username, String password){
        HashMap<String, Object> conditions = new HashMap<>();
        conditions.put("Username", username);
        conditions.put("Password_Hash", password);

        ArrayList<HashMap<String, Object>> rs = manager.queryElements("User_List", manager.getColumns("User_List"), conditions, true);
        if (rs == null){
            return false;
        }
        return rs.size() > 0;
    }

}
