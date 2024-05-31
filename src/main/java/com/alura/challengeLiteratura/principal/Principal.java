package com.alura.challengeLiteratura.principal;

import com.alura.challengeLiteratura.model.DatosLibro;
import com.alura.challengeLiteratura.model.LibroRepo;
import com.alura.challengeLiteratura.model.Libros;
import com.alura.challengeLiteratura.repository.LibroRepository;
import com.alura.challengeLiteratura.service.ConsumoApi;
import com.alura.challengeLiteratura.service.ConvierteDatos;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    //Para que el usuario pueda ingresar datos desde su teclado
    private Scanner teclado = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repository;

    public Principal(LibroRepository repository) {
        this.repository = repository;
    }


    public void mostrarMenu() throws IOException, InterruptedException {
        var opcion = -1;
        while (opcion != 0){
            var menu = """
                    
                    1. Buscar libro por título
                    2. Listar libros registrados
                    3. Listar autores registrados
                    4. Listar autores vivos en un determinado año
                    5. Listar libros por idioma
                    0. Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();
            
            switch (opcion){
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2: 
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4: 
                    listarAutoresVivos();
                    break;
                case 5:
                    ListarLibrosPorIdioma();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("opción inválida");
            }
        }
    }

    private void ListarLibrosPorIdioma() {
        System.out.println("1. español\n" +
                "2. inglés\n3. frances\n4. portugués");
        var opcion = teclado.nextInt();
        teclado.nextLine();
        String idioma="";
        switch (opcion){
            case 1:
                idioma = "es";
                break;
            case 2:
                idioma = "en";
                break;
            case 3:
                idioma = "fr";
                break;
            case 4:
                idioma = "pt";
                break;
            default:
                System.out.println("opción inválida");
        }
        if (idioma != ""){
            List<LibroRepo> libroLenguaje = repository.findByLenguaje(idioma);
            System.out.println(libroLenguaje.size());
            if(libroLenguaje.size() != 0){
                libroLenguaje.forEach(s ->
                        System.out.println("------ Libro ------\n" +
                                "Título: " + s.getNombreLibro() +"\n" +
                                "Autor: " + s.getNombreAutor() + "\n" +
                                "Idioma: " + s.getLenguaje() + "\n" +
                                "Número de descargas: " + s.getNumeroDescargas() + "\n" +
                                "------ ----- ------"));
            }else {
                System.out.println("No se encuentran registros de libros con el lenguaje: " + idioma);
            }
        }

    }

    private void listarAutoresVivos() {
        List<LibroRepo> librosRegistrados = repository.findAll();
        System.out.println("Ingresa una fecha");
        Integer fecha = teclado.nextInt();
        Set<LibroRepo> autoresUnicos = librosRegistrados.stream()
                .filter(s -> (s.getFechaNacimiento() <= fecha) && (s.getFechaFallecido() == null || fecha < s.getFechaFallecido()))
                .collect(Collectors.toSet());

        if (autoresUnicos.size()!=0){
            autoresUnicos.forEach(e ->
                    System.out.println("--------------------\n" +
                            "Nombre Autor: " + e.getNombreAutor() + "\n" +
                            "Fecha de nacimiento: " + e.getFechaNacimiento() + "\n" +
                            "Fecha de fallecimiento: " + e.getFechaFallecido()));
            System.out.println("--------------------");
        }else {
            System.out.println("No se encuentra registro de personas vivas en el año: " + fecha);
        }


    }

    private void listarAutoresRegistrados() {
        List<LibroRepo> librosRegistrados = repository.findAll();
        // Agrupar los libros por autor utilizando un Map
        Map<String, List<LibroRepo>> librosPorAutor = librosRegistrados.stream()
                .collect(Collectors.groupingBy(LibroRepo::getNombreAutor));

        // Imprimir los autores junto y sus libros
        librosPorAutor.forEach((autor, libros) -> {
            System.out.println("------ Lista Autores y sus obras ------");
            LibroRepo primerLibro= libros.get(0);
            System.out.println("Autor: " + autor);
            libros.forEach(libro -> System.out.println("\tLibro: " + libro.getNombreLibro()));
            System.out.println("Fecha de nacimiento: " + primerLibro.getFechaNacimiento());
            System.out.println("Fecha de fallecimiento: " + primerLibro.getFechaFallecido());

        });
        System.out.println("------ ----- ------- - --- ----- ------");
    }

    private void listarLibrosRegistrados() {
        List<LibroRepo> librosRegistrados = repository.findAll();
        if(librosRegistrados.size() != 0){
            librosRegistrados.forEach(s ->
                    System.out.println("------ Libro ------\n" +
                            "Título: " + s.getNombreLibro() +"\n" +
                            "Autor: " + s.getNombreAutor() + "\n" +
                            "Idioma: " + s.getLenguaje() + "\n" +
                            "Número de descargas: " + s.getNumeroDescargas() + "\n" +
                            "------ ----- ------"));
        }else {
            System.out.println("No se encuentran libros registrados por el momento");
        }

    }

    private DatosLibro buscarLibroPorTitulo() throws IOException, InterruptedException {
        System.out.println("Escriba el nombre del libro que desea buscar");
        var nombreLibro= teclado.nextLine();
        nombreLibro= nombreLibro.replaceAll(" ", "%20");
        nombreLibro=nombreLibro.toLowerCase();
        var json = consumoApi.obtenerDatos(nombreLibro);
        DatosLibro datos = conversor.obtenerDatos(json,DatosLibro.class);
        if (datos.resultados().size()!= 0){
            String nombreLibroObtenido = datos.resultados().get(0).nombreLibro();
            Optional<LibroRepo> libroExistente = repository.findByNombreLibro(nombreLibroObtenido);
            if (libroExistente.isPresent()){
                System.out.println("*******************");
                System.out.println("El libro ya existe en la base de datos");
            }else{
                LibroRepo libro = new LibroRepo(datos.resultados().get(0).nombreLibro(),
                        datos.resultados().get(0).autor().get(0).nombre(),datos.resultados().get(0).autor().get(0).nacimiento(),
                        datos.resultados().get(0).autor().get(0).fallecido(),
                        datos.resultados().get(0).lenguaje().get(0),datos.resultados().get(0).numeroDescargas());
                repository.save(libro);
            }
            System.out.println("------ LIBRO ------\n" +
                    "Título: " + datos.resultados().get(0).nombreLibro() + "\n" +
                    "Autor: " + datos.resultados().get(0).autor().get(0).nombre() + "\n" +
                    "Idioma: " + datos.resultados().get(0).lenguaje().get(0) + "\n" +
                    "Número de descargas: " + datos.resultados().get(0).numeroDescargas() + "\n" +
                    "------ ----- ------");
            return datos;
        }else {
            System.out.println("Libro no encontrado");
        }
         return datos;
    }

}
