package modelo;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "actividad")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "localidad", nullable = false, length = 100)
    private String localidad;

    @Column(name = "fecha_hora", nullable = false, columnDefinition = "datetime(6)")
    private LocalDateTime fechaHora;

    @Column(name = "plazas_totales", nullable = false)
    private int plazasTotales;

    @Column(name = "plazas_disponibles", nullable = false)
    private int plazasDisponibles;

    @ManyToOne
    @JoinColumn(name = "monitor_id", nullable = false, referencedColumnName = "id")
    private Monitor monitor;

    public Actividad() {
    }

    public Actividad(String nombre, String localidad, LocalDateTime fechaHora,
                     int plazasTotales, int plazasDisponibles, Monitor monitor) {
        this.nombre = nombre;
        this.localidad = localidad;
        this.fechaHora = fechaHora;
        this.plazasTotales = plazasTotales;
        this.plazasDisponibles = plazasDisponibles;
        this.monitor = monitor;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public int getPlazasTotales() { return plazasTotales; }
    public void setPlazasTotales(int plazasTotales) { this.plazasTotales = plazasTotales; }
    public int getPlazasDisponibles() { return plazasDisponibles; }
    public void setPlazasDisponibles(int plazasDisponibles) { this.plazasDisponibles = plazasDisponibles; }
    public Monitor getMonitor() { return monitor; }
    public void setMonitor(Monitor monitor) { this.monitor = monitor; }

    @Override
    public String toString() {
        return "ID actividad: " + id + "\nNombre: " + nombre + "\nLocalidad: " + localidad
            + "\nFecha y hora: " + fechaHora + "\nMonitor: " + monitor.getNombre()
            + "\nPlazas totales: " + plazasTotales + "\nPlazas disponibles: " + plazasDisponibles;
    }
}
