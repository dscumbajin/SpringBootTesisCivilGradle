package uce.edu.ec.app.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uce.edu.ec.app.model.Banner;
import uce.edu.ec.app.model.Bienes_Estaciones;
import uce.edu.ec.app.model.Estacion;
import uce.edu.ec.app.model.Noticia;
import uce.edu.ec.app.service.IBannersService;
import uce.edu.ec.app.service.IBienes_Estaciones;
import uce.edu.ec.app.service.IEstacionService;
import uce.edu.ec.app.service.INoticiasService;
import uce.edu.ec.app.util.ExcelBuilderDetalle;
import uce.edu.ec.app.util.PDFBuilderDetalle;

@Controller
public class HomeController {

	@Autowired
	private IBannersService serviceBanners; // Ejercicio : Solucion

	@Autowired
	private IEstacionService serviceEstaciones;

	@Autowired
	private INoticiasService serviceNoticias;

	@Autowired
	private IBienes_Estaciones serviceAsignaciones;

	private int idEstacionB = 0;
	private int numEquipos = 0;

	private String busqueda = "";

	private String paginado = "";

	private String token = "";

	private Date inicio = null;

	private Date fin = null;

	private List<Bienes_Estaciones> cambioPeriodoDetalle;

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String goHome() {
		return "home";
	}

	// regresa la lista de Estaciones a la pantalla home
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String mostrarPrincipal(Model model) {
		return "home";
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String mostrarDetalle(Model model, @RequestParam("idEstacion") int idEstacion, Pageable page) {

		Page<Bienes_Estaciones> bienes_Estaciones = serviceAsignaciones.buscarPorIdEstacion(idEstacion, page);
		numEquipos = bienes_Estaciones.getContent().size();
		model.addAttribute("numEquipo", numEquipos);
		model.addAttribute("bienes_Estaciones", bienes_Estaciones);
		model.addAttribute("estacion", serviceEstaciones.buscarPorId(idEstacion));
		idEstacionB = idEstacion;
		return "detalle";// Buscar en la base de datos la conformacion de los numero de equipos con los
		// bienes asignados
	}

	// Paginacion de los bienes buscados por id de estacion
	@GetMapping(value = "/detailPaginate")
	public String mostrarDetallePaginado(Model model, Pageable page) {
		model.addAttribute("numEquipo", numEquipos);
		model.addAttribute("estacion", serviceEstaciones.buscarPorId(idEstacionB));
		if (busqueda == "") {
			Page<Bienes_Estaciones> bienes_Estaciones = serviceAsignaciones.buscarPorIdEstacion(idEstacionB, page);
			model.addAttribute("bienes_Estaciones", bienes_Estaciones);

		} else {
			Page<Bienes_Estaciones> bienes_Estaciones = serviceAsignaciones
					.buscarPorEstacion_IdAndBien_Alta(idEstacionB, token, page);
			model.addAttribute("bienes_Estaciones", bienes_Estaciones);
			busqueda = "";
		}
		return "detalle";
	}

	@RequestMapping(value = "/personalizado")
	public String mostrarPeriodo() {
		return "redirect:/detallePeriodoPaginate";
	}

	// Redireccion formulario de busqueda por periodo
	@GetMapping(value = "/detallePeriodoPaginate")
	public String mostrarPeriodoPaginado(Model model, Pageable page) {

		if (busqueda == "") {

			System.out.println("Inicio: " + busqueda);
			// Formulario en blanco
			model.addAttribute("numEquipo", numEquipos);
			model.addAttribute("estacion", serviceEstaciones.buscarPorId(idEstacionB));

		} else if (paginado == "si") {
			// Formulario con busqueda personalizada
			model.addAttribute("numEquipo", numEquipos);
			model.addAttribute("estacion", serviceEstaciones.buscarPorId(idEstacionB));
			Page<Bienes_Estaciones> bienes_Estaciones = serviceAsignaciones
					.buscarCambiosPorPeriodoAndIdEstacion(idEstacionB, inicio, fin, page);
			cambioPeriodoDetalle = bienes_Estaciones.getContent();
			model.addAttribute("bienes_Estaciones", bienes_Estaciones);
			System.out.println("Primer paginado: " + busqueda + paginado);
		}
		return "detalleCambioPeriodo";
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
		return "redirect:/detallePeriodoPaginate";
	}

	// Reporte Todos los bienes por salas
	@GetMapping(value = "/downloadTotalDetalle")
	public ModelAndView getReport(HttpServletRequest request, HttpServletResponse response) {
		String reportType = request.getParameter("type");
		// Todos los bienes pero por id de estacion
		List<Bienes_Estaciones> bienes_Estaciones = serviceAsignaciones.buscarIdPorIdEstacion(idEstacionB);
		if (reportType != null && reportType.equals("excel")) {
			return new ModelAndView(new ExcelBuilderDetalle(), "bienes_Estaciones", bienes_Estaciones);

		} else if (reportType != null && reportType.equals("pdf")) {
			return new ModelAndView(new PDFBuilderDetalle(), "bienes_Estaciones", bienes_Estaciones);
		}
		return new ModelAndView("detalle", "bienes_Estaciones", bienes_Estaciones);
	}

	@GetMapping(value = "/cambioDetalle")
	public ModelAndView getDetallePeriodo(HttpServletRequest request, HttpServletResponse response) {
		String reportType = request.getParameter("type");
		// Todos los bienes pero por id de estacion
		List<Bienes_Estaciones> bienes_Estaciones = cambioPeriodoDetalle;
		if (reportType != null && reportType.equals("excel")) {
			return new ModelAndView(new ExcelBuilderDetalle(), "bienes_Estaciones", bienes_Estaciones);

		} else if (reportType != null && reportType.equals("pdf")) {
			return new ModelAndView(new PDFBuilderDetalle(), "bienes_Estaciones", bienes_Estaciones);
		}
		return new ModelAndView("detalle", "bienes_Estaciones", bienes_Estaciones);

	}

	@RequestMapping(value = "/cancel")
	public String Cancelar() {
		busqueda = "";
		paginado = "";
		return "redirect:/detailPaginate";
	}

	// Busqueda por alta
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String buscar(@RequestParam("campo") String campo) {
		System.out.println("alta: " + campo);
		busqueda = "si";
		token = campo;
		return "redirect:/detailPaginate";
	}

	@ModelAttribute("banners")
	public List<Banner> getBanners() {
		return serviceBanners.buscarActivos();
	}

	@ModelAttribute("estaciones")
	public List<Estacion> getEstaciones() {
		return serviceEstaciones.busacarTodas();
	}

	@ModelAttribute("noticias")
	public List<Noticia> getNoticias() {
		return serviceNoticias.buscarUltimas();
	}

	@RequestMapping(value = "/about")
	public String mostrarAcerca() {
		return "acerca";
	}

	@RequestMapping(value = "/formLogin", method = RequestMethod.GET)
	public String mostrarLogin() {
		return "formLogin";
	}

}
