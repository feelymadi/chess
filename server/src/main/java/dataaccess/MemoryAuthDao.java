package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDao implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }
}
