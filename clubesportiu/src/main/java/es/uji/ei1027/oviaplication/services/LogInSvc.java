package es.uji.ei1027.oviaplication.services;

import es.uji.ei1027.oviaplication.dao.InstructorDao;
import es.uji.ei1027.oviaplication.dao.OVIUserDao;
import es.uji.ei1027.oviaplication.dao.PAP_PATIDao;
import es.uji.ei1027.oviaplication.dao.TecnicoDao;
import es.uji.ei1027.oviaplication.model.OVIUser;
import es.uji.ei1027.oviaplication.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogInSvc {
    @Autowired
    private OVIUserDao oviUserDao;
    @Autowired
    private TecnicoDao tecnicoDao;
    @Autowired
    private PAP_PATIDao papPatiDao;
    @Autowired
    private InstructorDao instructorDao;

    public UserDetails login(String username, String password) {
        UserDetails user = oviUserDao.loadUserByUsername(username, password);

        if (user != null){
            user.setUserPassword(null);
            return user;
        }


        user = tecnicoDao.loadUserByUsername(username, password);

        if (user != null){
            user.setUserPassword(null);
            return user;
        }

        user = papPatiDao.loadUserByUsername(username, password);

        if(user != null){
            user.setUserPassword(null);
            return user;
        }

        user = instructorDao.loadUserByUsername(username, password);

        if(user != null){
            user.setUserPassword(null);
            return user;
        }

        return user;
    }
}
