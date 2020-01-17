package uce.edu.ec.app.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uce.edu.ec.app.model.Bien;
import uce.edu.ec.app.model.Bienes_Estaciones;
import uce.edu.ec.app.service.IBienService;
import uce.edu.ec.app.service.IBienes_Estaciones;
import uce.edu.ec.app.service.IDetallesService;
import uce.edu.ec.app.util.ExcelBuilder;
import uce.edu.ec.app.util.PDFBuilder;

@Controller
@RequestMapping(value = "/bienes")
public class BienesController {

	@Value("${bien.guardar}")
	private String mensajeGuardar;

	@Value("${bien.eliminar}")
	private String mensajeEliminar;

	@Value("${bien.repetido.altanueva}")
	private String mensajeRepetidoAltaNueva;

	@Value("${bien.repetido.altaanterior}")
	private String mensajeRepetidoAltaAnterior;

	@Value("${bien.noexiste.bien.altanueva}")
	private String mensajeNoExiste;

	@Autowired
	private IBienService serviceBienes;

	@Autowired
	private IDetallesService serviceDetalles;

	@Autowired
	private IBienes_Estaciones serviceAsignacion;

	private String edicion = "";

	private String busqueda = "";

	private String paginado = "";

	private String token = "";

	private Date inicio = null;

	private Date fin = null;

	private List<Bien> bienesPorPeriodo;
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	/**
	 * Metodo que muestra la lista de bienes
	 * 
	 * @param model
	 * @return
	 */

	@GetMapping(value = "/index")
	public String mostarIndex(Model model) {
		List<Bien> lista = serviceBienes.buscarTodas();
		model.addAttribute("bienes", lista);
		return "bienes/listBienes";
	}

	/**
	 * Metodo que muestra la lista de bienes con paginacion
	 * 
	 * @param model
	 * @param page
	 * @return
	 */

	@GetMapping(value = "/indexPaginate")
	public String mostrarIndexPaginado(Model model, Pageable page) {
		if (busqueda == "") {
			// Todos los registros
			Page<Bien> lista = serviceBienes.buscarTodas(page);
			model.addAttribute("bienes", lista);
		} else {
			// Formulario con registro buscado
			Page<Bien> lista = serviceBienes.search(token, page);
			if (lista.isEmpty()) {
				model.addAttribute("alerta", mensajeNoExiste + token);
				busqueda = "";
			} else {
				model.addAttribute("bienes", lista);
				busqueda = "";
			}

		}

		return "bienes/listBienes";
	}

	// Redireccion formulario de busqueda por periodo
	@GetMapping(value = "/periodPaginate")
	public String mostrarPeriodoPaginado(Model model, Pageable page) {

		if (busqueda == "") {
			// Lista vacia
		} else if (paginado == "si") {
			Page<Bien> lista = serviceBienes.buscarPeriodoPaginado(inicio, fin, page);
			if (lista.isEmpty()) {
				// Mensaje de no encontrado
				model.addAttribute("alerta", "No existen Bienes registrados para el período comprendido entre: "
						+ dateFormat.format(inicio) + " & " + dateFormat.format(fin));
				busqueda = "";

			} else {
				List<Bien> bienesLista = serviceBienes.buscarPeriodo(inicio, fin);
				model.addAttribute("bienes", lista);
				bienesPorPeriodo = bienesLista;
			}

		}
		return "bienes/listPeriodo";
	}

	// Busqueda por alta
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String buscar(@RequestParam("campo") String campo) {
		System.out.println("alta: " + campo);
		busqueda = "si";
		token = campo;
		return "redirect:/bienes/indexPaginate";
	}

	// Busqueda por alta
	@PostMapping(value = "/busquedaPrevia")
	public String busquedaPrevia(@RequestParam("campo") String campo, RedirectAttributes attributes) {
		System.out.println("alta: " + campo);

		if (serviceBienes.existeRegistroPorALta(campo)) {
			Bien bienNuevo = serviceBienes.buscarPorAlta(campo);
			try {
				if (bienNuevo.getControl() == "Activo") {
					attributes.addFlashAttribute("alerta",
							"Ya existe un Bien con Alta Nueva: " + campo + " y no se encuentra asignado a un lugar");
				} else {

					Bienes_Estaciones bienes_Estaciones = serviceAsignacion.buscarIdPorIdBien(bienNuevo.getId());
					attributes.addFlashAttribute("alerta", mensajeRepetidoAltaNueva + campo + " ubicado en: "
							+ bienes_Estaciones.getEstacion().getLugar());

				}
			} catch (Exception e) {
				attributes.addFlashAttribute("alerta",
						"Ya existe un Bien con Alta Nueva: " + campo + " y no se encuentra asignado a un lugar");
			}
			return "redirect:/bienes/create";
		} else if (serviceBienes.existeRegistroPorAnterior(campo)) {
			Bien bienAnterior = serviceBienes.buscarPorAltaAnterior(campo);
			try {
				if (bienAnterior.getControl() == "Activo") {
					attributes.addFlashAttribute("alerta",
							"Ya existe un Bien con Alta Anterior: " + campo + " y no se encuentra asignado a un lugar");
				} else {

					Bienes_Estaciones bienes_Estaciones = serviceAsignacion.buscarIdPorIdBien(bienAnterior.getId());
					attributes.addFlashAttribute("alerta", mensajeRepetidoAltaAnterior + campo + " ubicado en: "
							+ bienes_Estaciones.getEstacion().getLugar());
				}
			} catch (Exception e) {
				attributes.addFlashAttribute("alerta",
						"Ya existe un Bien con Alta Anterior: " + campo + " y no se encuentra asignado a un lugar");
			}

			return "redirect:/bienes/create";
		} else {
			attributes.addFlashAttribute("mensaje",
					"No existe el Bien con Alta Nueva: " + campo + " - ni Alta Anterior: " + campo);
			return "redirect:/bienes/create";
		}
	}

	// Busqueda por fecha inicio and fin
	@RequestMapping(value = "/buscar", method = RequestMethod.POST)
	public String buscarPeriodo(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate)
			throws ParseException {
		System.out.println("fecha inicio:" + startDate + " fecha fin:" + endDate);
		inicio = dateFormat.parse(startDate);
		fin = dateFormat.parse(endDate);
		busqueda = "si";
		paginado = "si";
		return "redirect:/bienes/periodPaginate";
	}

	/**
	 * Metodo para mostrar el formulario para crear
	 * 
	 * @return
	 */
	@GetMapping(value = "/create")
	public String crear(@ModelAttribute Bien bien) {
		return "bienes/formBien";
	}

	// Manejo de Errores
	@PostMapping(value = "/save")
	public String guardar(@ModelAttribute Bien bien, Model model, BindingResult result, RedirectAttributes attributes,
			@RequestParam(name = "alta", required = false) String alta,
			@RequestParam(name = "anterior", required = false) String anterior,
			@RequestParam(name = "serie", required = false) String serie) {

		if (result.hasErrors()) {
			System.out.println("Existen errores");
			return "bienes/formBien";
		}

		if (edicion == "") {
			// Nuevo Bien
			if (serviceBienes.existeRegistroPorALta(alta)) {
				model.addAttribute("alerta", mensajeRepetidoAltaNueva + alta);
				return "bienes/formBien";
			} else if (serviceBienes.existeRegistroPorAnterior(anterior)) {
				model.addAttribute("alerta", mensajeRepetidoAltaAnterior + anterior);
				return "bienes/formBien";
			} else {
				serviceDetalles.insertar(bien.getDetalle());
				serviceBienes.insertar(bien);
				attributes.addFlashAttribute("mensaje", mensajeGuardar);
				return "redirect:/bienes/indexPaginate";
			}

		} else {
			// Edicion
			serviceDetalles.insertar(bien.getDetalle());
			serviceBienes.insertar(bien);
			attributes.addFlashAttribute("mensaje",
					"El Bien con: Alta nueva: " + alta + " y Alta Anterior: " + anterior + " fue editado");
			edicion = "";
			return "redirect:/bienes/indexPaginate";

		}

	}

	// editar por ID
	@GetMapping(value = "edit/{id}")
	public String Editar(@PathVariable("id") int idBien, Model model) {
		Bien bien = serviceBienes.buscarPorId(idBien);
		model.addAttribute("bien", bien);
		edicion = "si";
		return "bienes/editBien";
	}

	// Eliminar por id
	@GetMapping(value = "delete/{id}")
	public String eliminar(@PathVariable("id") int idBien, RedirectAttributes attributes) {

		Bien bien = serviceBienes.buscarPorId(idBien);
		int a = 0;
		Bienes_Estaciones asignacion = serviceAsignacion.buscarIdPorIdBien(idBien);
		if (asignacion == null) {
			a = 0;
		} else {
			a = asignacion.getId();
		}
		System.out.println(asignacion + " paso 1 ");
		System.out.println(a);

		if (serviceAsignacion.existe(a)) {
			serviceAsignacion.eliminar(a);
			serviceBienes.eliminar(idBien);
			serviceDetalles.eliminar(bien.getDetalle().getId());
			attributes.addFlashAttribute("mensaje", mensajeEliminar);
			System.out.println("entra");
		} else {
			System.out.println("no entra");
			serviceBienes.eliminar(idBien);
			serviceDetalles.eliminar(bien.getDetalle().getId());
			attributes.addFlashAttribute("mensaje", mensajeEliminar);
		}

		return "redirect:/bienes/indexPaginate";
	}

	@GetMapping(value = "/cancel")
	public String mostrarAcerca() {
		busqueda = "";
		paginado = "";
		bienesPorPeriodo = null;
		return "redirect:/admin/index";
	}

	@GetMapping(value = "/personalizado")
	public String mostrarPeriodo() {

		return "redirect:/bienes/periodPaginate";
	}

	/**
	 * Personalizamos el Data Binding p ara todas las propiedades de tipo Date
	 * 
	 * @param webDataBinder
	 */
	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	// Reportes por periodo
	@GetMapping(value = "/periodo")
	public ModelAndView getPeriodo(HttpServletRequest request, HttpServletResponse response) {
		String reportType = request.getParameter("type");
		List<Bien> bienes = bienesPorPeriodo;
		if (reportType != null && reportType.equals("excel")) {
			return new ModelAndView(new ExcelBuilder(), "bienes", bienes);

		} else if (reportType != null && reportType.equals("pdf")) {
			return new ModelAndView(new PDFBuilder(), "bienes", bienes);
		}
		return new ModelAndView("listBienes", "bienes", bienes);
	}

	// Reporte Todos los bienes
	@GetMapping(value = "/downloadTotal")
	public ModelAndView getReport(HttpServletRequest request, HttpServletResponse response) {
		String reportType = request.getParameter("type");
		List<Bien> bienes = serviceBienes.buscarTodas();
		if (reportType != null && reportType.equals("excel")) {
			return new ModelAndView(new ExcelBuilder(), "bienes", bienes);

		} else if (reportType != null && reportType.equals("pdf")) {
			return new ModelAndView(new PDFBuilder(), "bienes", bienes);
		}
		return new ModelAndView("listBienes", "bienes", bienes);
	}

}
