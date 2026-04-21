package es.uji.ei1027.oviaplication.dao;

import es.uji.ei1027.oviaplication.model.PAP_PATI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TecnicoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<PAP_PATI> getPAP_PATIsPorEstado(String estado) {
        try {
            return jdbcTemplate.query("SELECT * FROM pap_pati WHERE estado = ?::estado_enum",
                    new PAP_PATIRowMapper(),
                    estado);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<PAP_PATI> updateEstadoPAP_PATI(String idNumber, String estado) {
        jdbcTemplate.update("UPDATE pap_pati SET estado=?::estado_enum WHERE idNumber=?",
                estado,
                idNumber
        );
        return getPAP_PATIsPorEstado(estado);
    }
}
