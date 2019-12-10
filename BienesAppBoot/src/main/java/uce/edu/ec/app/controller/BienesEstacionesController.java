package uce.edu.ec.app.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uce.edu.ec.app.model.Bien;
import uce.edu.ec.app.model.Bienes_Estaciones;
import uce.edu.ec.app.model.Estacion;
import uce.edu.ec.app.service.IBienService;
import uce.edu.ec.app.service.IBienes_Estaciones;
import uce.edu.ec.app.service.IEstacionService;

@Controller
@RequestMapping(value = "/asignaciones")
public class BienesEstacionesController {

	@Autowired
	private IBienes_Estaciones servicioBienesEstaciones;

	@Autowired
	private IBienService servicioBienes;

	@Autowired
	private IEstacionService servicioEstaciones;

	private String edicion = "";

	private String busqueda = "";

	private String token = "";

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	@GetMapping(value = "/index")
	public String mostrarIndex(Model model) {
		List<Bienes_Estaciones> listaAsignaciones = servicioBienesEstaciones.buscarTodos();
		model.addAttribute("asignaciones", listaAsignaciones);
		return "asignaciones/listAsignaciones";
	}

	@GetMapping(value = "/indexPaginate")
	public String mostrarIndexPaginado(Model model, Pageable page) {

		if (busqueda == "") {
			Page<Bienes_Estaciones> listaAsignaciones = servicioBienesEstaciones.buscarTodos(page);
			model.addAttribute("asignaciones", listaAsignaciones);

		} else {
			Page<Bienes_Estaciones> listaAsignaciones = servicioBienesEstaciones.buscarPorAltaBien(token, page);
			model.addAttribute("asignaciones", listaAsignaciones);
			busqueda = "";
		}
		return "asignaciones/listAsignaciones";
	}

	@GetMapping(value = "/create")
	public String crear(@ModelAttribute Bienes_Estaciones bienes_Estaciones, Model model) {
		return "asignaciones/formAsignaciones";
	}

	// Manejo de Errores
	@PostMapping(value = "/save")
	public String guardar(@ModelAttribute Bienes_Estaciones bienes_Estaciones, BindingResult result,
			RedirectAttributes attributes, @RequestParam("id") int id, @RequestParam("bien.id") String idBien,
			@RequestParam("estacion.id") String idEstacion, Model model) {

		if (result.hasErrors()) {
			System.out.println("Existen errores");
			return "asignaciones/formAsignaciones";
		}

		if (edicion == "") {
			// Guadar nuevo

			if (servicioBienesEstaciones.existeRegistroPorIdBienIdEstacion(Integer.parseInt(idBien),
					Integer.parseInt(idEstacion))) {
				model.addAttribute("alerta",
						"Ya existe un registro con id Bien: " + idBien + " y id Estacion: " + idEstacion);
				return "asignaciones/formAsignaciones";
			} else {
				System.out.println("Id bien asignado: " + idBien);
				Bien bien = servicioBienes.buscarPorId(Integer.parseInt(idBien));
				bien.setControl("Inactivo");
				servicioBienes.insertar(bien);
				bienes_Estaciones.setRegistro(bien.getFecha_ingreso());
				bienes_Estaciones.setActualizacion(bien.getFecha_ingreso());

				System.out.println(bien.toString());
				servicioBienesEstaciones.insertar(bienes_Estaciones);
				attributes.addFlashAttribute("mensaje", "El registro fue guardado");
				// redireccionamos a un nuevo formmulario
				return "redirect:/asignaciones/indexPaginate";
			}

		} else {

			// edicion
			bienes_Estaciones = servicioBienesEstaciones.buscarPorId(id);
			Estacion lugar = servicioEstaciones.buscarPorId(Integer.parseInt(idEstacion));
			System.out.println("Antes:" + bienes_Estaciones);

			if (bienes_Estaciones.getActualizacion() == bienes_Estaciones.getRegistro()) {
				bienes_Estaciones.setEstacion(lugar);
				bienes_Estaciones.setActualizacion(new Date());
				servicioBienesEstaciones.insertar(bienes_Estaciones);
				System.out.println("Despues:" + bienes_Estaciones);
				attributes.addFlashAttribute("mensaje", "El registro fue editado");

			} else {
				bienes_Estaciones.setRegistro(bienes_Estaciones.getActualizacion());
				bienes_Estaciones.setEstacion(lugar);
				bienes_Estaciones.setActualizacion(new Date());
				servicioBienesEstaciones.insertar(bienes_Estaciones);
				System.out.println("Despues:" + bienes_Estaciones);
				attributes.addFlashAttribute("mensaje", "El registro fue editado");
			}

			edicion = "";
		}
		return "redirect:/asignaciones/indexPaginate";

	}

	// editar por ID
	@GetMapping(value = "edit/{id}")
	public String Editar(@PathVariable("id") int id, Model model) {
		Bienes_Estaciones bienes_Estaciones = servicioBienesEstaciones.buscarPorId(id);
		model.addAttribute("bienes_Estaciones", bienes_Estaciones);
		edicion = "si";
		return "asignaciones/editAsignaciones";
	}

	// Eliminar por id
	@GetMapping(value = "delete/{id}")
	public String eliminar(@PathVariable("id") int id, RedirectAttributes attributes) {
		Bienes_Estaciones bienEstacion = servicioBienesEstaciones.buscarPorId(id);
		Bien bien = servicioBienes.buscarPorId(bienEstacion.getBien().getId());
		bien.setControl("Activo");
		servicioBienes.insertar(bien);
		servicioBienesEstaciones.eliminar(id);
		attributes.addFlashAttribute("mensaje", "Registro eliminado");
		return "redirect:/asignaciones/indexPaginate";
	}

	@ModelAttribute("bienesControl")
	public List<Bien> getBienesActivo() {
		return servicioBienes.sinAsignacion();
	}

	@ModelAttribute("bienes")
	public List<Bien> getBienes() {
		return servicioBienes.buscarTodas();
	}

	@ModelAttribute("estaciones")
	public List<Estacion> getEstaciones() {
		return servicioEstaciones.busacarTodas();
	}

	@RequestMapping(value = "/cancel")
	public String mostrarAcerca() {
		return "redirect:/asignaciones/indexPaginate";
	}

	// Busqueda por alta
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String buscar(@RequestParam("campo") String campo) {
		System.out.println("alta: " + campo);
		busqueda = "si";
		token = campo;
		return "redirect:/asignaciones/indexPaginate";
	}

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

}
