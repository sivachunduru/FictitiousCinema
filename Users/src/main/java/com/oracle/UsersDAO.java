package com.oracle;

import java.util.List;

public interface UsersDAO {
    public List getAllUsers();
    public User getUser(String id);
    public List getUserByName(String name);
    public boolean add(User user);
    public boolean update(String id, User user); // False equals fail
    public boolean delete(String id); // False equals fail   
}
