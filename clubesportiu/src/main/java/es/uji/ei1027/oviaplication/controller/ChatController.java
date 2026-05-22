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

    // Entrar al chat usando el ID del Match
    @GetMapping("/room/{idMatch}")
    public String openChatRoom(@PathVariable("idMatch") int idMatch, @RequestParam(value = "name", defaultValue = "Usuario") String nombreContacto, Model model, HttpSession session) {
        UserDetails currentUser = (UserDetails) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";

        // Calculamos el tipo de usuario (Ajusta según tu Enum)
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

    // Enviar mensaje mediante POST (Redirect automático al terminar)
    @PostMapping("/room/{idMatch}/send")
    public String sendMessage(@PathVariable("idMatch") int idMatch,
                              @RequestParam(value = "name", defaultValue = "Usuario") String name,
                              @ModelAttribute("newChat") Chat chat,
                              HttpSession session) {
        UserDetails currentUser = (UserDetails) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";

        String currentSenderType = "";
        if (currentUser.getTipoUsuario() == TipoUsuario.OVIUser) currentSenderType = "OVI";
        else if (currentUser.getTipoUsuario() == TipoUsuario.PAP_PATI) currentSenderType = "PAP";

        if (chat.getMessageContent() != null && !chat.getMessageContent().trim().isEmpty()) {
            chat.setIdMatch(idMatch);
            chat.setSenderType(currentSenderType);
            chat.setTimestamp(LocalDateTime.now());

            chatDao.addMessage(chat);
        }

        return "redirect:/chat/room/" + idMatch + "?name=" + name;
    }

    @GetMapping("/list")
    public String listMyChats(HttpSession session, Model model) {
        UserDetails currentUser = (UserDetails) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";

        List<ChatDetails> misChats = new ArrayList<>();

        // Usamos tu Enum TipoUsuario
        if (currentUser.getTipoUsuario() == TipoUsuario.OVIUser) {
            misChats = chatDao.getChatsForOviUser(currentUser.getUserName());
        } else if (currentUser.getTipoUsuario() == TipoUsuario.PAP_PATI) {
            misChats = chatDao.getChatsForPapPati(currentUser.getUserName());
        }

        model.addAttribute("chats", misChats);
        return "chat/list"; // Devuelve la vista HTML de la lista
    }
}
