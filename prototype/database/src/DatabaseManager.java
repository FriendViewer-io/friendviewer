package prototype.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseManager {

    /* Complete
           - Create Table
           - Delete Table
           - Add columns
           - Remove columns (Not supported in sqlite)
           - Add rows
           - Delete rows
           - Modify rows
           - Query Data
     */

    /* To Do
           - Improve Query Data (Pass Lambdas as the conditions)
     */

    enum Type {
        INTEGER, TEXT, REAL;
    }

    static class Column {
        public String name;
        //The name of the column is used as a key of sorts. Currently, adding _Hash to the end of the name indicates
        //that columns as a 'hash column', where all of its values are salted and hashed before stored in the database
        public Type dataType;

        public boolean key = false;
        public boolean unique = false;
        public boolean notNull = false;

        private boolean hash = false;

        public Column(String name, Type type) {
            this.name = name;
            this.dataType = type;

            if (this.name.endsWith("_Hash")){
                hash = true;
            }
        }

        public Column(String name, Type type, boolean key, boolean unique, boolean notNull) {
            this.name = name;
            this.dataType = type;
            this.key = key;
            this.unique = unique;
            this.notNull = notNull;

            if (this.name.endsWith("_Hash")){
                hash = true;
            }
        }


        public String getBasicInsertStatement() {
            String statement = name;
            statement += " " + dataType;

            return statement;
        }

        public String getInsertStatement() {
            String statement = name;
            statement += " " + dataType;
            if (notNull) {
                statement += " NOT NULL";
            }
            if (unique) {
                statement += " UNIQUE";
            }
            return statement;
        }
    }

    private Connection c;

    //Initiates the class by specifying the database to look at
    public DatabaseManager(String database) {
        try {
            Class.forName("org.sqlite.JDBC");
            //A call to Class.forName("X") causes the class named X to be dynamically loaded (at runtime).

            c = DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Deletes the table with the specified name
    public boolean deleteTable(String name){
        Statement stmt = null;
        try{
            stmt = c.createStatement();
            stmt.executeUpdate("DROP TABLE " + name);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Creates a table with the specified columns.
    public boolean createTable(String name, ArrayList<Column> columns) {
        if (columns.isEmpty()) {     //Need at least 1 entry to create a table
            return false;
        }

        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE " + name + " (";
            String primaryKey = "PRIMARY KEY(";
            for (Column col : columns) {
                sql += col.getInsertStatement() + ", ";
                if (col.key) {
                    primaryKey += col.name + ",";
                }

                //Is the column a hash column?
                if (col.hash){
                    sql += new Column(col.name.substring(0, col.name.length() - 5) + "_Salt", Type.TEXT).getInsertStatement() + ", ";
                }
            }
            if (primaryKey.length() > 12) {
                sql += primaryKey.substring(0, primaryKey.length() - 1) + ")";
            } else {
                sql = sql.substring(0, sql.length() - 2);
            }
            sql += ");";

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //Add a new column using the column class as input
    //Note that the unique/not null/primary key descriptions cannot be applied to an existing table.
    //A new column must be added with no descriptors, its values filled up, only then can descriptors be added
    //However, sqlite only support limited 'ALTER' commands. Some features, such as removing columns, are not supported
    public boolean addColumn(String table, Column column) {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "ALTER TABLE " + table
                    + " ADD " + column.getBasicInsertStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            if (column.hash){
                addColumn(table, column.name.substring(0, column.name.length() - 5) + "_Salt", Type.TEXT);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Add a new column using manual inputs
    public boolean addColumn(String table, String name, Type type) {
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "ALTER TABLE " + table
                    + " ADD " + name + " " + type;
            stmt.executeUpdate(sql);
            stmt.close();
            if (name.endsWith("_Hash")){
                addColumn(table, name.substring(0, name.length() - 5) + "_Salt", Type.TEXT);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //NOT SUPPORTED IN SQLITE, but leaving here just in case
    public boolean removeColumn(String table, String colID){
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "ALTER TABLE " + table
                    + " DROP COLUMN " + colID;
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Returns an Arraylist containing all the column names in the specified table
    public ArrayList<String> getColumns(String table){
        Statement stmt = null;
        ArrayList<String> columns = new ArrayList<String>();

        ResultSet rs = null;
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + table);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                columns.add(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columns;
    }

    //Adds the row to the specified table. Column values are stored in a hashmap. The method does not require
    //all columns to be filled, but will fail and return false if the "Not Null" columns are not included
    public boolean addRow(String table, HashMap<String, Object> colVals){
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            String sql = "INSERT INTO " + table;
            String columns = "(";
            String row_values = "(";
            for (String col_name : colVals.keySet()){
                if (col_name.endsWith("_Hash")){
                    SaltHasher.HashResult result = SaltHasher.hash((String) colVals.get(col_name));

                    columns += col_name + ", ";
                    row_values += "'" + result.hash + "', ";

                    columns += col_name.substring(0, col_name.length() - 5) + "_Salt, ";
                    row_values += "'" + result.salt + "', ";
                }else {
                    columns += col_name + ", ";
                    if (colVals.get(col_name).getClass() == String.class){
                        row_values += "'" + colVals.get(col_name) + "', ";
                    }else{
                        row_values += colVals.get(col_name) + ", ";
                    }
                }
            }
            sql += " " + columns.substring(0, columns.length() - 2) + ")";
            sql += " VALUES " + row_values.substring(0, row_values.length() - 2) + ");";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //Removes row(s) from the table that match a specific column value (Ex: ID = X)
    public boolean removeRow(String table, String colID, Object colValue){
        Statement stmt = null;

        try{
            stmt = c.createStatement();
            String sql = "DELETE FROM " + table + " WHERE " + colID + " = ";
            if (colValue.getClass() == String.class){
                sql += "'" + colValue + "'";
            }else{
                sql += colValue;
            }
            stmt.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //Modifies row elements that match specific column value(s) given in the conditions Hashmap to those given in colVals
    //The 'all' parameter represents the AND/OR for the WHERE clause. True = and, False = or
    public boolean modifyRows(String table, HashMap<String, Object> colVals, HashMap<String, Object> conditions, boolean all) {
        Statement stmt = null;
        try{
            stmt = c.createStatement();
            String sql = "UPDATE " + table;

            String newVals = " SET ";
            for (String col : colVals.keySet()){
                if (col.endsWith("_Hash")){
                    SaltHasher.HashResult result = SaltHasher.hash((String) colVals.get(col));

                    newVals += col + " = " + "'" + result.hash + "', ";
                    newVals += col.substring(0, col.length() - 5) + "_Salt = " + "'" + result.salt + "', ";
                }else {
                    if (colVals.get(col).getClass() == String.class){
                        newVals += col + " = " + "'" + colVals.get(col) + "', ";
                    }else{
                        newVals += col + " = " + colVals.get(col) + ", ";
                    }
                }
            }
            sql += newVals.substring(0, newVals.length() - 2);

            String conditionVals = " WHERE ";
            for (String col : conditions.keySet()){
                if (conditions.get(col).getClass() == String.class){
                    conditionVals += col + " = " + "'" + conditions.get(col) + "'";
                }else{
                    conditionVals += col + " = " + conditions.get(col);
                }

                if (all){
                    conditionVals += " AND ";
                }else{
                    conditionVals += " OR ";
                }
            }

            if (conditionVals.length() > 7){
                if (all){
                    sql += conditionVals.substring(0, conditionVals.length() - 5);
                }else{
                    sql += conditionVals.substring(0, conditionVals.length() - 4);
                }
            }

            stmt.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    //Modifies row elements that matches the specified condition
    public boolean modifyRows(String table, HashMap<String, Object> colVals, String colID, Object colVal){
        HashMap<String, Object> conditions = new HashMap<String, Object>();
        conditions.put(colID, colVal);
        return modifyRows(table, colVals, conditions, true);
    }

    //Modifies all row elements with the new column values
    public boolean modifyRows(String table, HashMap<String, Object> colVals){
        return modifyRows(table, colVals, new HashMap<String, Object>(), true);
    }

    //Returns all elements in the target columns
    //Return format is an Arraylist whose values represents the rows.
    //Inside in the Hashmap are the column values that were specifically requested for
    public ArrayList<HashMap<String, Object>> queryElements(String table, ArrayList<String> targetCols){
        Statement stmt = null;
        ResultSet rs = null;

        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        try{
            stmt = c.createStatement();
            String sql = "SELECT  ";
            String columns = "";
            for (String col : targetCols){
                if (getColumns(table).contains(col)){
                    columns += col + ", ";
                }else{
                    columns += col + "_Hash, ";
                }
            }
            sql += columns.substring(0, columns.length() - 2) + " FROM " + table;

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                HashMap<String, Object> rowData = new HashMap<String, Object>();
                for (String col : targetCols){
                    rowData.put(col, rs.getObject(col));
                }
                result.add(rowData);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return result;
    }

    //Returns all elements in the target columns that match the specifed conditions
    //The 'all' parameter represents the AND/OR for the WHERE clause. True = and, False = or
    public ArrayList<HashMap<String, Object>> queryElements(String table, ArrayList<String> targetCols, HashMap<String, Object> conditions, boolean all){
        Statement stmt = null;
        ResultSet rs = null;

        ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        try{
            stmt = c.createStatement();
            String sql = "SELECT  ";
            String columns = "";
            for (String col : targetCols){
                columns += col + ", ";
            }
            sql += columns.substring(0, columns.length() - 2) + " FROM " + table;

            String conditionVals = " WHERE ";
            for (String col : conditions.keySet()){
                if (conditions.get(col).getClass() == String.class){
                    conditionVals += col + " = " + "'" + conditions.get(col) + "'";
                }else{
                    conditionVals += col + " = " + conditions.get(col);
                }

                if (all){
                    conditionVals += " AND ";
                }else{
                    conditionVals += " OR ";
                }
            }
            if (all){
                sql += conditionVals.substring(0, conditionVals.length() - 5);
            }else{
                sql += conditionVals.substring(0, conditionVals.length() - 4);
            }

            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                HashMap<String, Object> rowData = new HashMap<String, Object>();
                for (String col : targetCols){
                    rowData.put(col, rs.getObject(col));
                }
                result.add(rowData);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return result;
    }
}