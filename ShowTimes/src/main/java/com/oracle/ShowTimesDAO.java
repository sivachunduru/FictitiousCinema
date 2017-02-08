package com.oracle;

import java.util.List;

public interface ShowTimesDAO {
    public List<ShowTime> getAllShowTimes();
    public List<ShowTime> getShowsByDate(String show_date);
    public List<ShowTime> getShowsByTimes(String show_time);
    public List<ShowTime> getShowsByCinemaId(String cinema_id);
    public boolean add(ShowTime show_time);
    public boolean update(String sdate, String stime, ShowTime show_time);
    public boolean delete(String sdate, String stime);   
}
