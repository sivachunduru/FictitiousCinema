package com.oracle;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class UsersListDAO implements UsersDAO {

    List<User> eList = null;
    private final Connection conn = DBConnection.getConnection();

    public List<User> query(String sqlQueryStr) {
        List<User> resultList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sqlQueryStr)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultList.add(
                    new User(rs.getString("user_id"), rs.getString("user_name"),
                        rs.getString("last_active"))
                );
            }
        } catch (SQLException e) {
            System.out.println("SQL Query Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Query Error: " + e.getMessage());
        }
        return resultList;
    }

    @Override
    public List<User> getAllUsers(){
        String queryStr = "SELECT * FROM customers";
        List<User> resultList = this.query(queryStr);
        return resultList;
    }


    @Override
    public User getUser(String id){
        String queryStr = "SELECT * FROM customers WHERE user_id='"+ id +"'";
        List<User> resultList = this.query(queryStr);

        if (resultList.size() > 0) {
            return resultList.get(0);
        } else {
            return null;
        }
    }


    @Override
    public List<User> getUserByName(String name){
        String queryStr = "SELECT * FROM customers WHERE user_name LIKE '%" + name + "%'";
        List<User> resultList = this.query(queryStr);

        return resultList;
    }


    @Override
    public boolean add(User user){
        String insertTableSQL = "INSERT INTO customers "
                + "(user_id, user_name, last_active) "
                + "VALUES(?,?,?)";

        try (PreparedStatement preparedStatement = this.conn
                .prepareStatement(insertTableSQL)) {

            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL Add Error: " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.out.println("Add Error: " + e.getMessage());
            return false;
        }

    }


    @Override
    public boolean update(String id, User user){
        String updateTableSQL = "UPDATE customers SET user_id=?, user_name=?, last_active=?  WHERE user_id=?";
        try (PreparedStatement preparedStatement = this.conn
                .prepareStatement(updateTableSQL);) {
            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
            preparedStatement.setString(4, user.getUserId());

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL Update Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String id){
        String deleteRowSQL = "DELETE FROM customers WHERE user_id=?";
        try (PreparedStatement preparedStatement = this.conn
                .prepareStatement(deleteRowSQL)) {
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("SQL Delete Error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
            return false;
        }
    }

}