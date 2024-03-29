package com.MigraEmprende.MigraEmprende.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.MigraEmprende.MigraEmprende.entities.Foto;
import com.MigraEmprende.MigraEmprende.entities.Usuario;
import com.MigraEmprende.MigraEmprende.repositories.UsuarioRepository;
import com.MigraEmprende.MigraEmprende.services.FotoService;
import com.MigraEmprende.MigraEmprende.services.MailService;
import com.MigraEmprende.MigraEmprende.services.UsuarioService;
import com.MigraEmprende.MigraEmprende.services.ValidationsService;

@Controller
@RequestMapping("/user")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private MailService mailService;

	@Autowired
	private ValidationsService validationsService;

	@Autowired
	private FotoService fotoService;

	@GetMapping("/") // devuelve el perfil del usuario
	public String indexProfile(@PathVariable String id) {
		return "redirect:/profile/{id}";
	}

	@GetMapping("/register") // Devuelve el form para registrarse y/o cambiar datos
	public String register(ModelMap model) {
		model.put("tipoForm", "register");
		model.put("usuario", new Usuario()); // nueva linea
		return "user-form";
	}

	@GetMapping("/modificar/{id}")
	public String modificar(ModelMap model, @PathVariable String id) {
		model.put("tipoForm", "modificar/" + id);
		model.put("usuario", usuarioRepository.getById(id));
		return "user-form";
	}

	@GetMapping("/login") // Devuelve el form para loguearse
	public String login() {
		return "login";
	}

	// prueba para borrar
	@GetMapping("/userform")
	public String userform() {
		return "user-form";
	}

	@GetMapping("/user") // PERFIL DE USUARIO, DEVUELVE LOS DATOS DEL USUARIO LOGEADO(LOGIN)
	public String userProfile(Model model, Principal principal) throws Exception {
		try {
			Usuario user = usuarioService.findByUsername(principal.getName());
			model.addAttribute("userName", user.getUsername());
			model.addAttribute("nombreyapellido", user.getNombreYApellido());
			model.addAttribute("email", user.getEmail());

			return "profile";
		} catch (Exception e) {
			throw e;
		}
	}

	@GetMapping("/profile/{id}") // Devuelve el perfil del usuario
	public String profile(ModelMap model, @PathVariable String id) throws Exception {
		model.addAttribute("usuario", usuarioService.buscarPorId(id));
		return "profile";
	}

	@PostMapping("/register") // Envía los datos del formulario de registro acá
	public String register(MultipartFile archivo, String name, String user, String email, String pass, String pass2,
			ModelMap modelo) throws Exception {
		Usuario usuario = new Usuario();
		validationsService.ValidarNombre(name);
		validationsService.ValidarUsername(user);
		// validationsService.ValidarUsernameNoRepetido(user);
		validationsService.ValidarEmail(email);
		validationsService.ValidarPassword(pass);
		validationsService.ValidarPasswordsSonIguales(pass, pass2);
		usuario.setNombreYApellido(name);
		usuario.setUsername(user);
		usuario.setEmail(email);
		Foto foto = fotoService.guardar(archivo);
		usuario.setFoto(foto);
		if (usuarioService.buscarPorUsername(user) != null) {
			modelo.put("errorUsuario", "El nombre de usuario ya existe!");
			modelo.put("tipoForm", "register");
			modelo.put("usuario", usuario);
			return "user-form";
		} else if (usuarioService.buscarPorEmail(email) != null) {
			modelo.put("errorEmail", "El Email ingresado ya se encuentra registrado!");
			modelo.put("tipoForm", "register");
			modelo.put("usuario", usuario);
			return "user-form";
		} else {
			usuarioService.crear(archivo, name, user, email, pass);
			return "redirect:/";
		}
	}

	@PostMapping("/modificar/{id}") // Envía los datos del formulario de edicion acá
	public String modificar(@PathVariable String id, MultipartFile archivo, String name, String user, String email,
			String pass) throws Exception {
		String password = usuarioRepository.getById(id).getPassword();
		System.out.println("Valores actuales:" + " " + name + " " + user + " " + email + " " + password);
		usuarioService.modificar(archivo, name, user, email, password, id);
		return "redirect:/";
	}

	@PostMapping("/delete/{id}") // Elimina un usuario
	public String delete(@PathVariable String id) throws Exception {
		usuarioRepository.deleteById(id);
		return "redirect:/";
	}

	@PostMapping("/deshabilitar/{id}") // Se da de baja a un usuario
	public String deshabilitar(@PathVariable String id) throws Exception {
		usuarioService.baja(id);
		return "redirect:/";
	}

	// CHANGE PASS
	@GetMapping("/resetpassword")
	public String resetpassForm() {
		return "resetpass-form";
	}

	@PostMapping("/resetpassword")
	public String resetpass(ModelMap modelo, @RequestParam String email) throws Exception {

		try {

			if (email == null || email.trim().isEmpty()) {
				modelo.put("error", "Indique el mail por favor.");
				return "resetpass-form.html";

			} else if (usuarioService.buscarPorEmail(email) == null) {
				modelo.put("error", "El mail indicado no se encuentra registrado.");
				return "resetpass-form.html";

			} else {
				mailService.resetPass(email);
				return "redirect:../";
			}

		} catch (Exception e) {
			throw e;
		}
	}

	@GetMapping("/resetpassword/{id}")
	public String resetpassEndForm(ModelMap modelo, @PathVariable String id) throws Exception {
		modelo.put("id", id);
		return "resetpass-end";
	}

	@PostMapping("/resetpassword/{id}")
	public String resetpassEnd(ModelMap modelo, @RequestParam String password1, @RequestParam String password2,
			@PathVariable String id) throws Exception {
		if (password1.equals(password2)) {
			usuarioService.changePass(id, password1);
			return "redirect:../login";
		} else {
			modelo.put("error", "Las contraseñas no coinciden! Intentelo nuevamente");
			return "resetpass-end";
		}

	}
}
