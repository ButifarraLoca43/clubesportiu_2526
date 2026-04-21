package es.uji.ei1027.oviaplication.dao;


import es.uji.ei1027.oviaplication.model.Estado;
import es.uji.ei1027.oviaplication.model.PAP_PATI;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PAP_PATIRowMapper implements RowMapper<PAP_PATI>
{
    @Override
    public PAP_PATI mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        PAP_PATI pap_pati = new PAP_PATI();
        pap_pati.setName(rs.getString("name"));
        pap_pati.setSurname(rs.getString("surname"));
        pap_pati.setEmail(rs.getString("email"));
        pap_pati.setDateBirth(rs.getObject("datebirth", LocalDate.class));
        pap_pati.setIdNumber(rs.getString("idnumber"));
        pap_pati.setAddress(rs.getString("address"));
        pap_pati.setPhoneNumber(rs.getString("phonenumber"));
        pap_pati.setExperience(rs.getString("experience"));
        pap_pati.setCurriculumVitae(rs.getString("curriculumvitae"));
        pap_pati.setUserPassword(rs.getString("userpassword"));
        pap_pati.setUserName(rs.getString("username"));
        String estado = rs.getString("estado");
        if (estado != null) {
            pap_pati.setEstado(Estado.valueOf(estado));
        } else {
            pap_pati.setEstado(Estado.pendiente);
        }

        return pap_pati;
    }
}
