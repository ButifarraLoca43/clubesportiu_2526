package es.uji.ei1027.oviaplication.controller;

import es.uji.ei1027.oviaplication.dao.ChatDao;
import es.uji.ei1027.oviaplication.model.Chat;
import es.uji.ei1027.oviaplication.model.ChatDetails;
import es.uji.ei1027.oviaplication.model.TipoUsuario;
import es.uji.ei1027.oviaplication.model.UserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatDao chatDao;

    // ==========================================
    // MÉTODO AUXILIAR DE SEGURIDAD (Capa de Identidad)
    // ==========================================
    // Comprueba si el usuario logueado realmente forma parte del emparejamiento (idMatch)
    private boolean isUserInMatch(UserDetails currentUser, int idMatch) {
        List<ChatDetails> misChats = new ArrayList<>();
        if (currentUser.getTipoUsuario() == TipoUsuario.OVIUser) {
            misChats = chatDao.getChatsForOviUser(currentUser.getUserName());
        } else if (currentUser.getTipoUsuario() == TipoUsuario.PAP_PATI) {
            misChats = chatDao.getChatsForPapPati(currentUser.getUserName());
        }

        // Recorremos los chats del usuario para ver si el idMatch solicitado está en su lista
        for (ChatDetails c : misChats) {
            if (c.getIdMatch() == idMatch) {
                return true; // Sí le pertenece
            }
        }
        return false; // Intento de espionaje o acceso a chat ajeno
    }

    // ==========================================
    // LISTADO DE CHATS
    // ==========================================

    @GetMapping("/list")
    public String listMyChats(HttpSession session, Model model) {
        UserDetails currentUser = (UserDetails) session.getAttribute("user");
        if (currentUser == null) {
            session.setAttribute("nextUrl", "/chat/list");
            return "redirect:/login";
        }

        // CONTROL DE ROL: Solo OVI o PAP pueden tener chats (Técnicos e Instructores fuera)
        if (currentUser.getTipoUsuario() != TipoUsuario.OVIUser && currentUser.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        List<ChatDetails> misChats = new ArrayList<>();
        if (currentUser.getTipoUsuario() == TipoUsuario.OVIUser) {
            misChats = chatDao.getChatsForOviUser(currentUser.getUserName());
        } else if (currentUser.getTipoUsuario() == TipoUsuario.PAP_PATI) {
            misChats = chatDao.getChatsForPapPati(currentUser.getUserName());
        }

        model.addAttribute("chats", misChats);
        return "chat/list";
    }

    // ==========================================
    // SALA DE CHAT (Lectura)
    // ==========================================

    @GetMapping("/room/{idMatch}")
    public String openChatRoom(@PathVariable("idMatch") int idMatch,
                               @RequestParam(value = "name", defaultValue = "Usuario") String nombreContacto,
                               Model model, HttpSession session) {
        UserDetails currentUser = (UserDetails) session.getAttribute("user");
        if (currentUser == null) {
            // Guardamos la ruta con los parámetros GET incluidos para no perder el nombre
            session.setAttribute("nextUrl", "/chat/room/" + idMatch + "?name=" + nombreContacto);
            return "redirect:/login";
        }

        // 1. Control de Rol
        if (currentUser.getTipoUsuario() != TipoUsuario.OVIUser && currentUser.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        // 2. Control de Identidad (Antiespionaje)
        if (!isUserInMatch(currentUser, idMatch)) {
            return "/auth/acceso-denegado"; // El chat existe, pero tú no participas en él
        }

        String currentSenderType = switch (currentUser.getTipoUsuario()) {
            case OVIUser -> "OVI";
            case PAP_PATI -> "PAP";
            default -> "OTH";
        };

        model.addAttribute("nombreContacto", nombreContacto);
        model.addAttribute("messages", chatDao.getMessagesByMatch(idMatch));
        model.addAttribute("currentUserType", currentSenderType);
        model.addAttribute("idMatch", idMatch);
        model.addAttribute("newChat", new Chat());

        return "chat/room";
    }

    // ==========================================
    // ENVÍO DE MENSAJES (Escritura)
    // ==========================================

    @PostMapping("/room/{idMatch}/send")
    public String sendMessage(@PathVariable("idMatch") int idMatch,
                              @RequestParam(value = "name", defaultValue = "Usuario") String name,
                              @ModelAttribute("newChat") Chat chat,
                              HttpSession session) {
        UserDetails currentUser = (UserDetails) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";

        // 1. Control de Rol
        if (currentUser.getTipoUsuario() != TipoUsuario.OVIUser && currentUser.getTipoUsuario() != TipoUsuario.PAP_PATI) {
            return "/auth/acceso-denegado";
        }

        // 2. Control de Identidad en Escritura (Evitar falsificación de mensajes)
        if (!isUserInMatch(currentUser, idMatch)) {
            return "/auth/acceso-denegado";
        }

        String currentSenderType = "";
        if (currentUser.getTipoUsuario() == TipoUsuario.OVIUser) currentSenderType = "OVI";
        else if (currentUser.getTipoUsuario() == TipoUsuario.PAP_PATI) currentSenderType = "PAP";

        if (chat.getMessageContent() != null && !chat.getMessageContent().trim().isEmpty()) {
            // Sobrescribimos en el backend los datos críticos para que no puedan manipularse desde el HTML
            chat.setIdMatch(idMatch);
            chat.setSenderType(currentSenderType);
            chat.setTimestamp(LocalDateTime.now());

            chatDao.addMessage(chat);
        }

        return "redirect:/chat/room/" + idMatch + "?name=" + name;
    }
}