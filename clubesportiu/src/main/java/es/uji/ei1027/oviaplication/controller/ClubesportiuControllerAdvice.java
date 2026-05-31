package es.uji.ei1027.oviaplication.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ClubesportiuControllerAdvice {

    // 1. Mètode específic per a errors de base de dades / claus foranes
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ModelAndView handleDataIntegrityException(DataIntegrityViolationException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorName", "Error de restricció de dades");

        // AQUÍ és on escrius el text literal que vols que surta a la pantalla
        mav.addObject("message", "No es pot dur a terme l'operació perquè aquest registre està lligat a altres dades del sistema (clau forana).");

        return mav;
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public ModelAndView handleDuplicateKeyException(DuplicateKeyException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorName", "Dades Duplicades");

        String errorLog = ex.getMessage();
        String mensajePersonalizado;

        // Inspeccionem el log de l'error per saber quina restricció ha saltat
        if (errorLog.contains("uq_inscription_ext")) {
            mensajePersonalizado = "Aquest assistent ja es troba inscrit en aquesta activitat. No es permeten inscripcions duplicades.";
        } else if (errorLog.contains("ak_external_assistant_email")) {
            mensajePersonalizado = "El correu electrònic introduït ja està registrat al sistema per un altre assistent.";
        } else {
            mensajePersonalizado = "Ja existeix un registre a la base de dades amb aquestes mateixes dades clau.";
        }

        mav.addObject("message", mensajePersonalizado);
        return mav;
    }

    // 2. El teu mètode genèric (es queda com a fons de seguretat per a altres errors)
    @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("errorName", "Error inesperat");
        return mav;
    }
}