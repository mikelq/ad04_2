package modelo;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "monitor")
public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "especialidad", nullable = false, length = 100)
    private String especialidad;

    @OneToMany(mappedBy = "monitor")
    private List<Actividad> actividades = new ArrayList<>();

    public Monitor() {
    }

    public Monitor(String nombre, String especialidad) {
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public List<Actividad> getActividades() { return actividades; }

    public void agregarActividad(Actividad actividad) {
        actividades.add(actividad);
        actividad.setMonitor(this);
    }

    @Override
    public String toString() {
        return "ID monitor: " + id + "\nNombre: " + nombre + "\nEspecialidad: " + especialidad;
    }
}
