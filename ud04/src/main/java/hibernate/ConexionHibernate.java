package hibernate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import modelo.Monitor;
import modelo.Actividad;
import modelo.Cliente;
import modelo.Reserva;

public class ConexionHibernate {

    // Creamos una unica SessionFactory para toda la aplicacion.
    private static final SessionFactory sessionFactory = crearSessionFactory();

    private static SessionFactory crearSessionFactory() {
        Configuration cfg = new Configuration().configure();
        cfg.addAnnotatedClass(Monitor.class);
        cfg.addAnnotatedClass(Actividad.class);
        cfg.addAnnotatedClass(Cliente.class);
        cfg.addAnnotatedClass(Reserva.class);
        return cfg.buildSessionFactory();
    }

    public static Session conectarHibernate() {
        return sessionFactory.openSession();
    }
    //metodo que cierra la sesion.
    public static void cerrarSesion(Session sesion) {
            sesion.close();
    }
    
    public static void cerrarHibernate() {
        if (sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }

    public static void guardarMonitor(String nombre, String especialidad) {
        Transaction transaccion = null;
        Session sesion = null;
        try {
            sesion = conectarHibernate();
            transaccion = sesion.beginTransaction();

            Monitor monitor = new Monitor(nombre, especialidad);
            sesion.persist(monitor);

            transaccion.commit();
            System.out.println("Monitor insertado correctamente. ID: " + monitor.getId());
        } catch (Exception ex) {
            transaccion.rollback();
            System.out.println("Error al guardar el monitor: " + ex.getMessage());
        } finally {
            cerrarSesion(sesion);
        }
    }

    public static void guardarActividad(String nombre, String localidad, LocalDateTime fechaHora,
                                       int plazas, long monitorId) {
        Transaction transaccion = null;
        Session sesion = null;
        try {
            sesion = conectarHibernate();

            Monitor monitor = sesion.get(Monitor.class, monitorId);
            //comporbamos antes de guardar la actividad, si el monitorId existe, si no existe salimos.
            if (monitor == null) {
                System.out.println("No existe el monitor seleccionado.");
                return;
            }
            
            transaccion = sesion.beginTransaction();
            Actividad actividad = new Actividad(nombre, localidad,
                    fechaHora, plazas, plazas, monitor);
            sesion.persist(actividad);

            transaccion.commit();
            System.out.println("Actividad insertada correctamente. ID: " + actividad.getId());
        } catch (Exception ex) {
            transaccion.rollback();
            System.out.println("Error al guardar la actividad: " + ex.getMessage());
        } finally {
            cerrarSesion(sesion);
        }
    }

    public static void buscarActividadesDisponibles(String localidad, LocalDate dia, int plazas) {
        Session sesion = null;
        try {
            sesion = conectarHibernate();
            LocalDateTime inicio = dia.atStartOfDay();
            LocalDateTime fin = dia.plusDays(1).atStartOfDay();

            // Consulta HQL parametrizada: la fecha se busca como dia completo.
            String hql = "FROM Actividad a WHERE lower(a.localidad) = :localidad "
                    + "AND a.fechaHora >= :inicio AND a.fechaHora < :fin "
                    + "AND a.plazasDisponibles >= :plazas ORDER BY a.fechaHora";

            List<Actividad> lista = sesion.createQuery(hql, Actividad.class)
                    .setParameter("localidad", localidad.trim().toLowerCase(Locale.ROOT))
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .setParameter("plazas", plazas)
                    .getResultList();

            if (lista.isEmpty()) {
                System.out.println("No hay actividades disponibles con esos criterios.");
            } else {
            	//por cada actovidad en la lista obtenida, utilizamos el metodo toString() de la actividad para mostrarla
                for (Actividad actividad : lista) {
                    System.out.println("--------------------------------------------");
                    System.out.println(actividad);
                }
                System.out.println("--------------------------------------------");
            }
        } catch (Exception ex) {
            System.out.println("Error al buscar actividades: " + ex.getMessage());
        } finally {
            cerrarSesion(sesion);
        }
    }

    public static void guardarCliente(String nombre, String email) {
        Transaction transaccion = null;
        Session sesion = null;
        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        try {
            sesion = conectarHibernate();

            // Comprobamos duplicados sin distinguir mayusculas y minusculas.
            Long existe = sesion.createQuery("SELECT count(c) FROM Cliente c WHERE lower(c.email) = :email", Long.class).setParameter("email", emailNormalizado).uniqueResult();
            //si existe no es null y existe es mayor a 0
            if (existe != null && existe > 0) {
                System.out.println("Ya existe un cliente con ese email.");
                return;
            }

            transaccion = sesion.beginTransaction();
            Cliente cliente = new Cliente(nombre, emailNormalizado);
            sesion.persist(cliente);
            transaccion.commit();

            System.out.println("Cliente insertado correctamente. ID: " + cliente.getId());
        } catch (Exception ex) {
        	//si salta error, realizamos un rollback para que no quede ningun dato a medio guardar.
            transaccion.rollback();
            System.out.println("Error al guardar el cliente: " + ex.getMessage());
        } finally {
            cerrarSesion(sesion);
        }
    }

    public static void guardarReserva(int clienteId, int actividadId, int numeroPlazas) {
        Transaction transaccion = null;
        Session sesion = null;
        try {
            // La busqueda, el descuento y el alta se hacen con la misma sesion.
            sesion = conectarHibernate();
            transaccion = sesion.beginTransaction();

            Cliente cliente = sesion.get(Cliente.class, (long) clienteId);
            Actividad actividad = sesion.get(Actividad.class, (long) actividadId);

            if (cliente == null) throw new IllegalArgumentException("No existe el cliente seleccionado.");
            if (actividad == null) throw new IllegalArgumentException("No existe la actividad seleccionada.");
            if (numeroPlazas <= 0) throw new IllegalArgumentException("El numero de plazas debe ser positivo.");
            if (actividad.getPlazasDisponibles() < numeroPlazas)
                throw new IllegalArgumentException("No hay plazas suficientes.");
            //restamos de las plazas disponibles, las plazas que reservamos
            actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() - numeroPlazas);
            Reserva reserva = new Reserva(LocalDateTime.now(), numeroPlazas, cliente, actividad);
            sesion.persist(reserva);
            sesion.flush();
            
            //obtenemos el ID de la reserva recien creada.
            long idReserva = reserva.getId();
            int restantes = actividad.getPlazasDisponibles();
            transaccion.commit();

            System.out.println("Reserva insertada correctamente. ID: " + idReserva);
            System.out.println("Plazas disponibles restantes: " + restantes);
        } catch (Exception ex) {
        	//si salta error, realizamos un rollback para que no quede ningun dato a medio guardar.
            transaccion.rollback();
            System.out.println("Reserva no realizada: " + ex.getMessage());
        } finally {
        	//cerramos la sesion.
            cerrarSesion(sesion);
        }
    }

    public static void borrarReserva(int id) {
        Transaction transaccion = null;
        Session sesion = null;
        try {
            // Recuperamos la reserva y la actividad dentro de la misma transaccion.
            sesion = conectarHibernate();
            transaccion = sesion.beginTransaction();

            Reserva reserva = sesion.get(Reserva.class, (long) id);
            if (reserva == null) {
                throw new IllegalArgumentException("No existe una reserva con ese ID.");
            }

            Actividad actividad = reserva.getActividad();
            int recuperadas = reserva.getNumeroPlazas();
            actividad.setPlazasDisponibles(actividad.getPlazasDisponibles() + recuperadas);
            sesion.remove(reserva);
            sesion.flush();

            int disponibles = actividad.getPlazasDisponibles();
            transaccion.commit();

            System.out.println("Reserva cancelada correctamente.");
            System.out.println("Plazas recuperadas: " + recuperadas);
            System.out.println("Plazas disponibles: " + disponibles);
        } catch (Exception ex) {
            transaccion.rollback();
            System.out.println("No se pudo cancelar la reserva: " + ex.getMessage());
        } finally {
            cerrarSesion(sesion);
        }
    }

    public static void mostrarActividades() {
        Session sesion = null;
        try {
            sesion = conectarHibernate();
            List<Actividad> lista = sesion.createQuery(
                    "FROM Actividad a ORDER BY a.fechaHora", Actividad.class).getResultList();

            if (lista.isEmpty()) {
                System.out.println("No hay actividades registradas.");
            } else {
                for (Actividad actividad : lista) {
                    System.out.println("--------------------------------------------");
                    System.out.println(actividad);
                }
                System.out.println("--------------------------------------------");
            }
        } catch (Exception ex) {
            System.out.println("Error al listar actividades: " + ex.getMessage());
        } finally {
            cerrarSesion(sesion);
        }
    }
}
