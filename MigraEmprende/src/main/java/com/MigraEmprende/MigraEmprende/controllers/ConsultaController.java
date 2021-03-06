package com.MigraEmprende.MigraEmprende.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.MigraEmprende.MigraEmprende.services.MailService;

@Controller
@RequestMapping("/consulta")
public class ConsultaController {

	@Autowired
	private MailService mailService;
	
	@GetMapping("")
	public String form() {
		return "mail-contact";
	}

	@PostMapping("")
	public String enviar(ModelMap modelo, @RequestParam String nombre, @RequestParam String email,
			@RequestParam String mensaje) throws Exception {
		try {
			mailService.enviarMail(nombre, email, mensaje);
			modelo.put("exito", "Consulta enviada con éxito!");
			return "redirect:/";
		} catch (Exception e) {
			modelo.put("error", e.getMessage());
			return "redirect:/";
		}
	}

}
