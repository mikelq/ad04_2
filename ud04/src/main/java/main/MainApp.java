package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;

import hibernate.ConexionHibernate;

public class MainApp {

    private static Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern EMAIL =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public static int menu() {
        System.out.println("=== ACTIVA · Gestión de actividades ===");
        System.out.println("1. Crear monitor");
        System.out.println("2. Crear actividad");
        System.out.println("3. Buscar actividades disponibles");
        System.out.println("4. Crear cliente");
        System.out.println("5. Crear reserva");
        System.out.println("6. Cancelar reserva");
        System.out.println("7. Listar actividades");
        System.out.println("8. Salir");
        System.out.print("Introduce opción: ");

        try {
            int opcion = Integer.parseInt(sc.nextLine().trim());
            return opcion;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static void main(String[] args) {
        int opcion;
        do {
            opcion = menu();
            try {
                switch (opcion) {
                    case 1: {
                        System.out.print("Introduce nombre del monitor: ");
                        String nombre = sc.nextLine();
                        System.out.print("Introduce especialidad: ");
                        String especialidad = sc.nextLine();

                        if (nombre.trim().isEmpty() || especialidad.trim().isEmpty()) {
                            System.out.println("Nombre y especialidad son obligatorios.");
                        } else {
                            ConexionHibernate.guardarMonitor(nombre, especialidad);
                        }
                        break;
                    }
                    case 2: {
                        System.out.print("Introduce nombre de la actividad: ");
                        String nombre = sc.nextLine();
                        System.out.print("Introduce localidad: ");
                        String localidad = sc.nextLine();

                        LocalDateTime fechaHora = pedirFechaHora();
                        int plazas = pedirEnteroPositivo("Introduce plazas totales: ");
                        int monitorId = pedirEnteroPositivo("Introduce ID del monitor: ");

                        if (!nombre.trim().isEmpty() && !localidad.trim().isEmpty()) {
                            ConexionHibernate.guardarActividad(nombre, localidad, fechaHora, plazas, monitorId);
                        } else {
                            System.out.println("El nombre y la localidad son obligatorios.");
                        }
                        break;
                    }
                    case 3: {
                        System.out.print("Introduce localidad: ");
                        String localidad = sc.nextLine();
                        LocalDate dia = pedirDia();
                        int plazas = pedirEnteroPositivo("Introduce numero de plazas necesarias: ");
                        ConexionHibernate.buscarActividadesDisponibles(localidad, dia, plazas);
                        break;
                    }
                    case 4: {
                        System.out.print("Introduce nombre del cliente: ");
                        String nombre = sc.nextLine();
                        System.out.print("Introduce email del cliente: ");
                        String email = sc.nextLine().trim();

                        if (nombre.trim().isEmpty()) {
                            System.out.println("El nombre no puede estar vacío.");
                        } else if (!EMAIL.matcher(email).matches()) {
                            System.out.println("El email no tiene un formato válido.");
                        } else {
                            ConexionHibernate.guardarCliente(nombre, email);
                        }
                        break;
                    }
                    case 5: {
                        int clienteId = pedirEnteroPositivo("Introduce ID del cliente: ");
                        int actividadId = pedirEnteroPositivo("Introduce ID de la actividad: ");
                        int numeroPlazas = pedirEnteroPositivo("Introduce numero de plazas: ");
                        ConexionHibernate.guardarReserva(clienteId, actividadId, numeroPlazas);
                        break;
                    }
                    case 6: {
                        int reservaId = pedirEnteroPositivo("Introduce ID de la reserva: ");
                        ConexionHibernate.borrarReserva(reservaId);
                        break;
                    }
                    case 7: {
                        ConexionHibernate.mostrarActividades();
                        break;
                    }
                    case 8:
                        System.out.println("Saliendo de ACTIVA...");
                        break;
                    default:
                        System.out.println("Opción incorrecta. Selecciona una opción del 1 al 8.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
            System.out.println();
        } while (opcion != 8);

        ConexionHibernate.cerrarHibernate();
        sc.close();
    }

    private static int pedirEnteroPositivo(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                int numero = Integer.parseInt(sc.nextLine().trim());
                if (numero > 0) return numero;
            } catch (NumberFormatException ex) {
                // Se vuelve a solicitar el dato.
            }
            System.out.println("Introduce un número entero positivo.");
        }
    }

    private static LocalDateTime pedirFechaHora() {
        while (true) {
            System.out.print("Introduce fecha y hora (dd/MM/yyyy HH:mm): ");
            try {
                return LocalDateTime.parse(sc.nextLine().trim(), FORMATO_FECHA_HORA);
            } catch (DateTimeParseException ex) {
                System.out.println("Formato incorrecto. Ejemplo: 15/11/2026 10:00");
            }
        }
    }

    private static LocalDate pedirDia() {
        while (true) {
            System.out.print("Introduce día (dd/MM/yyyy): ");
            try {
                return LocalDate.parse(sc.nextLine().trim(), FORMATO_FECHA);
            } catch (DateTimeParseException ex) {
                System.out.println("Fecha incorrecta. Ejemplo: 15/11/2026");
            }
        }
    }
}
