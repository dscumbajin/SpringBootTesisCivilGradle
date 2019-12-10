package uce.edu.ec.app.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uce.edu.ec.app.model.Bien;
import uce.edu.ec.app.model.Bienes_Estaciones;
import uce.edu.ec.app.model.Estacion;
import uce.edu.ec.app.service.IBienService;
import uce.edu.ec.app.service.IBienes_Estaciones;
import uce.edu.ec.app.service.IEstacionService;
import uce.edu.ec.app.util.Utileria;

@Controller
@RequestMapping(value = "/estaciones")
public class EstacionController {

	@Autowired
	private IEstacionService servicioEstaciones;

	@Autowired
	private IBienes_Estaciones serviceAsignacion;

	@Autowired
	private IBienService servicioBienes;

	private String edicion = "";

	private String busqueda = "";
	private String token = "";

	// Crear un objeto tipo Estacion
	@GetMapping(value = "/create")
	public String crear(@ModelAttribute Estacion estacion, Model model) {
		return "estaciones/formEstaciones";
	}

	@GetMapping(value = "/index")
	public String mostarIndex(Model model) {
		List<Estacion> lista = servicioEstaciones.busacarTodas();
		model.addAttribute("estaciones", lista);
		return "estaciones/listEstaciones";
	}

	@GetMapping(value = "/indexPaginate")
	public String mostrarIndexPaginado(Model model, Pageable page) {
		if (busqueda == "") {
			Page<Estacion> lista = servicioEstaciones.buscarTodos(page);
			model.addAttribute("estaciones", lista);
		} else {
			Page<Estacion> lista = servicioEstaciones.buscarPorLugar(token, page);
			model.addAttribute("estaciones", lista);
			busqueda="";
		}
		return "estaciones/listEstaciones";
	}

	// Guardar en base de datos
	@PostMapping(value = "/save")
	public String guardar(@ModelAttribute Estacion estacion, BindingResult result, RedirectAttributes attributes,
			Model model, @RequestParam("archivoImagen") MultipartFile multiPart, HttpServletRequest request,
			@RequestParam("ubicacion") String ubicacion, @RequestParam("lugar") String lugar) {

		if (result.hasErrors()) {
			System.out.println("Existen errores");
			return "estaciones/formEstaciones";
		}

		if (edicion == "") {
			// Nuevo
			if (servicioEstaciones.existeEstacion(ubicacion, lugar)) {
				model.addAttribute("alerta", "Ya existe una: " + lugar + " en el: " + ubicacion);
				return "estaciones/formEstaciones";
			} else {
				if (!multiPart.isEmpty()) {
					String nombreImagen = Utileria.guardarImagen(multiPart, request);
					if (nombreImagen != null) { // La imagen si se subio
						estacion.setImagen(nombreImagen); // Asignamos el nombre de la imagen
					}
				}
				servicioEstaciones.insertar(estacion);
				attributes.addFlashAttribute("mensaje", "El registro fue guardado");
				return "redirect:/estaciones/indexPaginate";// redireccionamos a un nuevo formmulario

			}

		} else {
			// Edición
			if (!multiPart.isEmpty()) {
				String nombreImagen = Utileria.guardarImagen(multiPart, request);
				if (nombreImagen != null) { // La imagen si se subio
					estacion.setImagen(nombreImagen); // Asignamos el nombre de la imagen
				}
			}
			servicioEstaciones.insertar(estacion);
			attributes.addFlashAttribute("mensaje", "El registro fue editado");
			edicion = "";
			return "redirect:/estaciones/indexPaginate";// redireccionamos a un nuevo formmulario

		}

	}

	// editar por ID
	@GetMapping(value = "edit/{id}")
	public String Editar(@PathVariable("id") int idEstacion, Model model) {
		Estacion estacion = servicioEstaciones.buscarPorId(idEstacion);
		model.addAttribute("estacion", estacion);
		edicion = "si";
		return "estaciones/formEstaciones";
	}

	// Eliminar por id
	@GetMapping(value = "delete/{id}")
	public String eliminar(@PathVariable("id") int idEstacion, RedirectAttributes attributes) {

		int id = 0;
		int idBien = 0;
		List<Bienes_Estaciones> listaEstaciones = serviceAsignacion.buscarIdPorIdEstacion(idEstacion);

		if (listaEstaciones.size() == 0) {
			servicioEstaciones.eliminar(idEstacion);
			attributes.addFlashAttribute("mensaje", "Registro eliminado");
		} else {
			for (Bienes_Estaciones b : listaEstaciones) {
				System.out.println(b.toString());
				id = b.getId();
				idBien = b.getBien().getId();
				Bien bien = servicioBienes.buscarPorId(idBien);
				System.out.println(bien);
				bien.setControl("Activo");
				servicioBienes.insertar(bien);
				serviceAsignacion.eliminar(id);
			}
			servicioEstaciones.eliminar(idEstacion);
			attributes.addFlashAttribute("mensaje", "Registro eliminado");
		}
		return "redirect:/estaciones/indexPaginate";
	}

	@RequestMapping(value = "/cancel")
	public String mostrarAcerca() {
		return "redirect:/estaciones/indexPaginate";
	}

	// Busqueda por alta
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String buscar(@RequestParam("inputLugar") String inputLugar) {
		System.out.println("alta: " + inputLugar);
		busqueda = "si";
		token = inputLugar;
		return "redirect:/estaciones/indexPaginate";
	}
}
