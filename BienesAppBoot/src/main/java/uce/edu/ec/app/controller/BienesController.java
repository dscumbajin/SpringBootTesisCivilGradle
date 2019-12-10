package uce.edu.ec.app.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			model.addAttribute("bienes", lista);
			busqueda = "";
		}

		return "bienes/listBienes";
	}

	// Redireccion formulario de busqueda por periodo
	@GetMapping(value = "/periodPaginate")
	public String mostrarPeriodoPaginado(Model model, Pageable page) {

		if (busqueda == "") {
			// Lista vacia
		} else if (paginado == "si") {
			Page<Bien> lista = serviceBienes.buscarPeriodo(inicio, fin, page);
			model.addAttribute("bienes", lista);
			bienesPorPeriodo = lista.getContent();
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
			@RequestParam("alta") String alta, @RequestParam("anterior") String anterior,
			@RequestParam("serie") String serie) {

		if (result.hasErrors()) {
			System.out.println("Existen errores");
			return "bienes/formBien";
		}

		if (edicion == "") {
			if (serviceBienes.exiteRegistroPorAltaAnteriorSerie(alta, anterior, serie)) {
				model.addAttribute("alerta", "Ya existe un registro con Alta Nueva: " + alta + " Alta Anterior: "
						+ anterior + " Serie: " + serie);
				return "bienes/formBien";
			} else {

				serviceDetalles.insertar(bien.getDetalle());
				serviceBienes.insertar(bien);
				attributes.addFlashAttribute("mensaje", "El registro fue guardado");
				return "redirect:/bienes/indexPaginate";
			}

		} else {
			serviceDetalles.insertar(bien.getDetalle());
			serviceBienes.insertar(bien);
			attributes.addFlashAttribute("mensaje", "El registro fue editado");
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
		return "bienes/formBien";
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
			attributes.addFlashAttribute("mensaje", "Registro eliminado");
			System.out.println("entra");
		} else {
			System.out.println("no entra");
			serviceBienes.eliminar(idBien);
			serviceDetalles.eliminar(bien.getDetalle().getId());
			attributes.addFlashAttribute("mensaje", "Registro eliminado");
		}

		return "redirect:/bienes/indexPaginate";
	}

	@RequestMapping(value = "/cancel")
	public String mostrarAcerca() {
		busqueda = "";
		paginado = "";
		return "redirect:/bienes/indexPaginate";
	}

	@RequestMapping(value = "/personalizado")
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
