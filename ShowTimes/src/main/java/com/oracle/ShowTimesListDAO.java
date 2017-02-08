package com.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShowTimesListDAO implements ShowTimesDAO {

    List<ShowTime> eList = null;
    private final Connection conn = DBConnection.getConnection();

    public List<ShowTime> query(String sqlQueryStr) {
        List<ShowTime> resultList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sqlQueryStr)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                resultList.add(
                    new ShowTime(rs.getString("show_date"), rs.getString("show_time"),
                        rs.getString("cinema_id"))
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
	public List getAllShowTimes() {
        String queryStr = "SELECT * FROM timings order by cinema_id, show_date, show_time";
        List<ShowTime> resultList = this.query(queryStr);
        return resultList;
	}

	@Override
	public List getShowsByDate(String show_date) {
        String queryStr = "SELECT * FROM timings WHERE DATE_FORMAT(show_date, '%Y-%m-%d')='"+ show_date +"' order by cinema_id, show_time";
        List<ShowTime> resultList = this.query(queryStr);
        if (resultList.size() > 0) {
            return resultList;
        } else {
            return null;
        }
	}

	@Override
	public List getShowsByTimes(String show_time) {
        String queryStr = "SELECT * FROM timings WHERE TIME_FORMAT(show_time, '%H:%i:%s')='"+ show_time +"' order by cinema_id, show_date";
        List<ShowTime> resultList = this.query(queryStr);
        if (resultList.size() > 0) {
            return resultList;
        } else {
            return null;
        }
	}

	@Override
	public List getShowsByCinemaId(String cinema_id) {
        String queryStr = "SELECT * FROM timings WHERE cinema_id='"+ cinema_id +"' order by show_date, show_time";
        List<ShowTime> resultList = this.query(queryStr);
        if (resultList.size() > 0) {
            return resultList;
        } else {
            return null;
        }
	}

	@Override
	public boolean add(ShowTime show_time) {
        String insertTableSQL = "INSERT INTO timings "
                + "(show_date, show_time, cinema_id) "
                + "VALUES(DATE_FORMAT(?, '%Y-%m-%d'),TIME_FORMAT(?, '%H:%i:%s'),?)";
        try (PreparedStatement preparedStatement = this.conn
                .prepareStatement(insertTableSQL)) {

			preparedStatement.setString(1, show_time.getShow_date());
            preparedStatement.setString(2, show_time.getShow_time());
            preparedStatement.setString(3, show_time.getCinema_id());
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
	public boolean update(String sdate, String stime, ShowTime show_time) {
        String updateTableSQL = "UPDATE timings SET show_date=DATE_FORMAT(?, '%Y-%m-%d'), show_time=TIME_FORMAT(?, '%H:%i:%s'), cinema_id=?  WHERE DATE_FORMAT(show_date, '%Y-%m-%d')=? AND TIME_FORMAT(show_time, '%H:%i:%s')=?";
        try (PreparedStatement preparedStatement = this.conn
                .prepareStatement(updateTableSQL);) {
            preparedStatement.setString(1, show_time.getShow_date());
            preparedStatement.setString(2, show_time.getShow_time());
            preparedStatement.setString(3, show_time.getCinema_id());
            preparedStatement.setString(4, sdate);
            preparedStatement.setString(5, stime);
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
	public boolean delete(String sdate, String stime) {
        String deleteRowSQL = "DELETE FROM timings WHERE DATE_FORMAT(show_date, '%Y-%m-%d')=? AND TIME_FORMAT(show_time, '%H:%i:%s')=?";
        try (PreparedStatement preparedStatement = this.conn
                .prepareStatement(deleteRowSQL)) {
            preparedStatement.setString(1, sdate);
            preparedStatement.setString(2, stime);
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
