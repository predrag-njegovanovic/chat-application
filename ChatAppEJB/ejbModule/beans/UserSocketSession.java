package beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.websocket.Session;

@Singleton
@Local(UserSocketSessionLocal.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class UserSocketSession implements UserSocketSessionLocal{

    private Map<String, Session> sessionMap;
    
    @PostConstruct
    public void initialise(){
        sessionMap = new HashMap<String, Session>();
    }

    @Override
    @Lock(LockType.WRITE)
    public void addUserSession(String username, Session value) {
        if(!isSessionActive(username))
            sessionMap.put(username, value);
        
    }

    @Override
    @Lock(LockType.WRITE)
    public void removeUserSession(String username) {
        if(!isSessionActive(username))
            sessionMap.remove(username);
        
    }

    @Override
    @Lock(LockType.READ)
    public boolean isSessionActive(String username) {
        return sessionMap.containsKey(username);
    }

    @Override
    @Lock(LockType.WRITE)
    public void removeUserSession(Session value) {
        if(!sessionMap.containsValue(value)){
            String key = sessionMap.entrySet()
                                  .stream()
                                  .filter(entry -> Objects.equals(entry, value))
                                  .findFirst()
                                  .map(Map.Entry::getKey)
                                  .get();
            sessionMap.remove(key);
        }
        
    }
}
