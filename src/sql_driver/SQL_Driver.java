package sql_driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sqlite.util.StringUtils;

import voyairbooking.Utils;

public class SQL_Driver {
	private HashMap<String, ArrayList<String>> schema = new HashMap<String, ArrayList<String>>();
	private Connection con;
	private Statement stmnt;
	public SQL_Driver(String filename) throws SQLException{
		this.con = DriverManager.getConnection("jdbc:sqlite:" + filename);
		this.stmnt = this.con.createStatement();
	}
	public SQL_Driver() throws SQLException{
		this("program.db");
	}

	public boolean execute(String sql){
		try{
			boolean res = this.stmnt.execute(sql);
			return res;
		} catch (SQLException e) {
			System.err.println("Failed to execute the SQL query: " + sql);
			e.printStackTrace();
			return false;
		}
	}
	public ResultSet executeQuery(String sql){
		try {
			ResultSet res = this.stmnt.executeQuery(sql);
			return res;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to execute the SQL query: " + sql);
			e.printStackTrace();
			return null;
		}
	}
	@SuppressWarnings("serial")
	public ArrayList<HashMap<String, String>> select(String table_name) throws SQLException{
		return select(table_name, new ArrayList<String>() {{ add("*");}});
	}
	public ArrayList<HashMap<String, String>> select(String table_name, ArrayList<String> select) throws SQLException{
		return select(table_name, select, "");
	}
	public ArrayList<HashMap<String, String>> select(String table_name, ArrayList<String> select, String where) throws SQLException{
		return select(table_name, select, where, "");
	}
	public ArrayList<HashMap<String, String>> select(String table_name, ArrayList<String> select, String where, String other) throws SQLException{
		ArrayList<HashMap<String, String>> toReturn = new ArrayList<HashMap<String, String>>();

		String sql = "Select " +  StringUtils.join(select, ",") + " FROM " + table_name;
		if(!where.isEmpty()){
			sql += " WHERE " + where;
		}
		if(!other.isEmpty()){
			sql += other;
		}
		ResultSet rs = executeQuery(sql);
		ArrayList<String> colNames= select;
		if(select.contains("*")){
			colNames = getColumnNames(rs);
		}
		while(rs.next()){
			HashMap<String, String> row = new HashMap<String, String>();

			for(String col : colNames){
				row.put(col, rs.getString(col));
			}
			toReturn.add(row);
		}
		return toReturn;
	}   
	@SuppressWarnings("serial")
	public HashMap<String, String> select_first(String table_name) throws SQLException{
		return select_first(table_name, new ArrayList<String>() {{ add("*"); }});
	}
	public HashMap<String, String> select_first(String table_name, ArrayList<String> select) throws SQLException{
		return select_first(table_name, select, "");
	}
	public HashMap<String, String> select_first(String table_name, ArrayList<String> select, String where) throws SQLException{
		return select_first(table_name, select, where, "");
	}
	public HashMap<String, String> select_first(String table_name, ArrayList<String> select, String where, String other) throws SQLException{
		HashMap<String, String> toReturn = new HashMap<String, String>();

		String sql = "Select " +  StringUtils.join(select, ",") + " FROM " + table_name;
		if(!where.isEmpty()){
			sql += " WHERE " + where;
		}
		sql += " LIMIT 1 ";
		if(!other.isEmpty()){
			sql += other;
		}
		ResultSet rs = executeQuery(sql);
		ArrayList<String> colNames = select;
		if(select.contains("*")){
			colNames = getColumnNames(rs);
		}
		while(rs.next()){
			for(String col : colNames){
				toReturn.put(col, rs.getString(col));
			}
		}
		return toReturn;
	}
	public ArrayList<String> getColumnNames(ResultSet rs){
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();

			String table_name = rsmd.getTableName(1);
			if(!this.schema.containsKey(table_name)){
				ArrayList<String> columnNames = new ArrayList<String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++){
					columnNames.add(rsmd.getColumnName(i));
				}
				this.schema.put(table_name, columnNames);
			}
			return this.schema.get(table_name);
		} catch (SQLException e) {
			System.out.println("Could not get column names.");
			e.printStackTrace();
		}
		return null;
	}
	public boolean create_table(String table_name, ArrayList<String> fields){
		String sql = "CREATE TABLE IF NOT EXISTS " + table_name + " (";
		for(String entry : fields){
			sql += entry + ",";
		}
		sql = Utils.replaceStringEnding(sql, ")");
		return this.execute(sql);
	}
	public boolean insert(String table_name, Map<String, String> fields){
		String sql = "INSERT INTO " + table_name + " (";
		String values = " VALUES (";
		for (Entry<String, String> entry : fields.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sql += key + ",";
			values += "'"+ value.replaceAll(",", "', '") + "',";
		}
		sql = Utils.replaceStringEnding(sql, ")");
		values = Utils.replaceStringEnding(values, ")");
		return this.execute(sql + values);
	}

	public boolean delete(String table_name, String where){
		String sql = "DELETE FROM " + table_name + " WHERE " + where;
		return this.execute(sql);
	}
	public boolean update(String table_name, HashMap<String, String> update_to, String where){
		String sql = "UPDATE " + table_name + " SET ";
		for(Entry<String, String> entry : update_to.entrySet() ){
			sql += entry.getKey() + " = " + entry.getValue() + ",";
		}
		sql = Utils.replaceStringEnding(sql, "");
		if(!where.contains("='") || !where.contains("= '")){
			String newWhere = "";
			for(String splits : where.split(",")){
				String[] res = splits.split("=");
				newWhere += res[0].trim().replace("'", "") + "= '" + res[1].trim().replace("'", "") + "'";
			}
			where = newWhere;
		}
		sql += " WHERE " + where;
		return this.execute(sql);
	}
	public boolean truncate(String table_name){
		// Vaccum is memory clean up or something since sqlite doesn't have a truncate.
		String sql = "DELETE FROM " + table_name + "; VACUUM"; 
		return this.execute(sql);
		
	}
}
