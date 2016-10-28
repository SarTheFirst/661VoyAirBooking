package sql_driver;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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

public class SQL_Driver {
	private HashMap<String, ArrayList<String>> schema = new HashMap<String, ArrayList<String>>();
	private String db_name;
	private Connection con;
	private Statement stmnt;
	public Boolean debug;
	PrintWriter sql_out;
	public boolean close(){
		try {
			this.stmnt.close();
			this.con.close();

		} catch (SQLException e) {
			return false;
		}
		return true;
	}
	public boolean open(){
		try {
			this.con = DriverManager.getConnection("jdbc:sqlite:" + this.db_name);
			this.stmnt = this.con.createStatement();
			return true;
		} catch (SQLException e) {
			System.err.println("Could not connect to database");
			return false;
		}
	}
	public SQL_Driver(String filename, boolean debug) throws SQLException{
		this.debug = debug;
		this.db_name = filename;
	}
	public SQL_Driver() throws SQLException{
		this("program.db", false);
	}
	public SQL_Driver(boolean debug) throws SQLException{
		this("program.db", debug);
	}
	public boolean execute(String sql){
		try{
			if(this.open()){
				boolean res = this.stmnt.execute(sql);
				if(this.debug){
					System.out.println("DEBUG: " + sql);
				}
				return res;
			}
			else{
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Failed to execute the SQL query: " + sql);
			this.sql_out.println("ERROR: " + sql);
			e.printStackTrace();
			return false;
		}
	}
	public ResultSet executeQuery(String sql){
		try {
			if(this.open()){
				ResultSet res = this.stmnt.executeQuery(sql);
				if(this.debug){
					System.out.println("DEBUG: " + sql);
				}
				return res;
			}
			else{
				return null;
			}
		} catch (SQLException e) {
			System.err.println("Failed to execute the SQL query: " + sql);
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<HashMap<String, String>> select(String table_name, ArrayList<String> select) throws SQLException{
		return select(table_name, select, "");
	}
	@SuppressWarnings("serial")
	public ArrayList<HashMap<String, String>> select(String table_name, String select, String where) throws SQLException{
		return select(table_name, new ArrayList<String>() {{ add(select);}}, where);
	}
	public ArrayList<HashMap<String, String>> select(String table_name, ArrayList<String> select, String where) throws SQLException{
		return select(table_name, select, where, "", false);
	}
	@SuppressWarnings("serial")
	public ArrayList<HashMap<String, String>> select(String table_name, String select, String where, boolean distinct) throws SQLException{
		return select(table_name, new ArrayList<String>() {{ add(select);}}, where, "", distinct);
	}

	public ArrayList<HashMap<String, String>> select(String table_name, ArrayList<String> select, String where, String other, boolean distinct) throws SQLException{
		ArrayList<HashMap<String, String>> toReturn = new ArrayList<HashMap<String, String>>();

		String sql = "SELECT ";
		if(distinct){
			sql += "DISTINCT ";
		}
		sql += StringUtils.join(select, ",") + " FROM " + sqlProof(table_name);
		if(!where.isEmpty()){
			sql += " WHERE " + fixWhere(where);
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
	@SuppressWarnings("serial")
	public HashMap<String, String> select_first(String table_name, String select, String where) throws SQLException{
		return select_first(table_name, new ArrayList<String>() {{ add(select);}}, where);
	}
	public HashMap<String, String> select_first(String table_name, ArrayList<String> select) throws SQLException{
		return select_first(table_name, select, "");
	}
	public HashMap<String, String> select_first(String table_name, ArrayList<String> select, String where) throws SQLException{
		return select_first(table_name, select, where, "");
	}
	public HashMap<String, String> select_first(String table_name, ArrayList<String> select, String where, String other) throws SQLException{
		HashMap<String, String> toReturn = new HashMap<String, String>();
		String sel = StringUtils.join(select,",");
		if(!sel.contains("count(*)")){
			sel = "'" + sqlProof(sel) + "'";
		}
		String sql = "Select " +  sel + " FROM " + sqlProof(table_name) + " ";
		if(!where.isEmpty()){
			sql += " WHERE " + fixWhere(where);
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
	public boolean create_table(String table_name, HashMap<String, String> fields){
		String sql = "CREATE TABLE IF NOT EXISTS " + sqlProof(table_name) + " (";
		for (Map.Entry<String, String> entry : fields.entrySet()){
			sql += sqlProof(entry.getKey()) + " " + entry.getValue() + ",";
		}
		sql = replaceStringEnding(sql, ")");
		return this.execute(sql);
	}
	public boolean batch_insert(String table_name, ArrayList<Map<String, String>> fields){
		String sql = "INSERT INTO " + sqlProof(table_name) + " (";
		String values = " VALUES ";
		boolean first = true;
		for (Map<String, String> row_entry : fields){
			values += "(";
			for (Entry<String, String> entry : row_entry.entrySet()) {
				String value = sqlProof(entry.getValue());
				values += value + ",";
				if(first){
					String key = sqlProof(entry.getKey());
					sql += key + ",";
				}
			}
			values = replaceStringEnding(values, "");
			first = false;
			values += "),";
		}

		sql = replaceStringEnding(sql, ")");
		values = replaceStringEnding(values, "");
		return this.execute(sql + values);
	}
	public boolean insert(String table_name, Map<String, String> fields){
		String sql = "INSERT INTO " + sqlProof(table_name) + " (";
		String values = " VALUES (";
		for (Entry<String, String> entry : fields.entrySet()) {
			String key = sqlProof(entry.getKey());
			String value = sqlProof(entry.getValue());
			sql += key + ",";
			values += value + ",";
		}
		sql = replaceStringEnding(sql, ")");
		values = replaceStringEnding(values, ")");
		return this.execute(sql + values);
	}

	public boolean delete(String table_name, String where){
		String sql = "DELETE FROM " + sqlProof(table_name) + " WHERE " + where;
		return this.execute(sql);
	}
	public boolean update(String table_name, HashMap<String, String> update_to, String where){
		String sql = "UPDATE " + sqlProof(table_name) + " SET ";
		for(Entry<String, String> entry : update_to.entrySet() ){
			sql += sqlProof(entry.getKey()) + " = " + sqlProof(entry.getValue()) + ",";
		}
		sql = replaceStringEnding(sql, "");
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
		String sql = "DELETE FROM " + sqlProof(table_name) + "; VACUUM"; 
		return this.execute(sql);
	}
	public int count_rows(String table_name) throws SQLException{
		ArrayList<String> count = new ArrayList<String>();
		count.add("count(*)");
		HashMap<String, String> res = this.select_first(table_name, count);
		return Integer.parseInt(res.get("count(*)"));
	}
	public boolean drop(String table_name){
		String sql = "DROP TABLE IF EXISTS " + sqlProof(table_name);
		return this.execute(sql);
	}
	public boolean drop_all() throws SQLException{
		this.open();
		DatabaseMetaData md = this.con.getMetaData(); 
		ResultSet rs = md.getTables(null,  null, "%", null);
		ArrayList<String> tables = new ArrayList<String>();
		while(rs.next()){
			if(!rs.getString(3).contains("sqlite_sequence")){
				tables.add(rs.getString(3));
			}
		}
		this.close();

		for(String table : tables){
			this.open();
			if(drop(table)){
				return false;
			}
			this.close();
		}
		return true;
	}
	public String fixWhere(String where){
		String[] res = where.split(" ");
		String newWhere = "";
		for(String s :res){
			if(s.contains("=") && !( s.contains("true") || s.contains("false"))){
				String[] parts = s.split("=");
				String newParts = String.join("", parts[0], "=") + "'" + parts[1]+"'";
				newWhere += newParts;
			}
			else if(s.equalsIgnoreCase("AND") || s.equalsIgnoreCase("or")){
				newWhere += " " + s + " ";
			}
			else {
				newWhere += s + " ";
			}
		}
		return newWhere + " COLLATE NOCASE";

	}
	public static String sqlProof(String str){
		str = str.replace("'", "''");
		str = "'" + str + "'";
		str = str.replaceAll("/", "\\/");
		return str;

	}
	public String replaceStringEnding(String str, String suffix){
		return str.substring(0, str.length()-1) + suffix;
	}

}
