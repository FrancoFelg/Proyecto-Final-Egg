package com.MigraEmprende.MigraEmprende.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.MigraEmprende.MigraEmprende.entities.Comentario;
import com.MigraEmprende.MigraEmprende.entities.Usuario;
import com.MigraEmprende.MigraEmprende.repositories.ComentarioRepository;

@Service
public class ComentarioService {

	// INSTANCIAMOS EL REPOSITORIO
	@Autowired
	private ComentarioRepository comentRepository;

	@Autowired
	private ValidationsService validationsService;

	// CREAR COMENTARIO
	@Transactional
	public Comentario crearComentario(String titulo, String contenido, Usuario usuario) throws Exception {

		try {

			// validations

			validationsService.ValidarTitulo(titulo);
			validationsService.ValidarContenido(contenido);
			validationsService.ValidarUsuario(usuario);

			// create

			Comentario entidad = new Comentario();

			entidad.setTitulo(titulo);
			entidad.setContenido(contenido);
			entidad.setFecha(new Date());
			entidad.setUsuario(usuario);
			entidad.setAlta(true);

			comentRepository.save(entidad);

			return entidad;
		} catch (Exception e) {
			System.out.println("No se pudo crear el comentario");
			throw e;
		}
	}

	// LISTAR TODOS LOS COMENTARIOS
	@Transactional(readOnly = true)
	public List<Comentario> listarComentarios() throws Exception {
		try {
			List<Comentario> listaComentarios;
			listaComentarios = comentRepository.findAll();
			return listaComentarios;
		} catch (Exception e) {
			System.out.println("No se pudo listar los comentarios");
			throw e;
		}
	}

	// BORRAR COMENTARIO
	@Transactional
	public void borrarComentario(String id) throws Exception {
		try {

			// validations

			validationsService.ValidarId(id);
			validationsService.ValidarIdExiste(id);

			// method
			Comentario entidad = comentRepository.findById(id).get();
			comentRepository.delete(entidad);
		} catch (Exception e) {
			System.out.println("No se pudo eliminar el cometario");
			throw e;
		}
	}

	// BAJA COMENTARIO
	public Comentario bajaComentario(String id) throws Exception {
		try {

			// validations

			validationsService.ValidarId(id);
			validationsService.ValidarIdExiste(id);

			// method
			Comentario entidad = comentRepository.findById(id).get();
			entidad.setAlta(false);
			return comentRepository.save(entidad);
		} catch (Exception e) {
			System.out.println("No se pudo dar de baja el comentario");
			throw e;
		}
	}

	// ALTA COMENTARIO
	public Comentario altaComentario(String id) throws Exception {
		try {

			// validations

			validationsService.ValidarId(id);
			validationsService.ValidarIdExiste(id);

			// method
			Comentario entidad = comentRepository.findById(id).get();
			entidad.setAlta(true);
			return comentRepository.save(entidad);
		} catch (Exception e) {
			System.out.println("No se pudo dar de alta el comentario");
			throw e;
		}
	}

	// LISTAR COMENTARIOS POR USUARIO
	public List<Comentario> listarComentariosUsuario(Usuario usuario) throws Exception {
		try {

			// validations

			validationsService.ValidarUsuario(usuario);

			// method
			List<Comentario> listaComentario = comentRepository.retornarComentarioPorUsername(usuario.getUsername());
			return listaComentario;
		} catch (Exception e) {
			System.out.println("No se pudo listar los comentarios por Usuario");
			throw e;
		}
	}

}
