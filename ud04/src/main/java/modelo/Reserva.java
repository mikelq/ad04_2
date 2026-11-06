package modelo;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "fecha_reserva", nullable = false, columnDefinition = "datetime(6)")
    private LocalDateTime fechaReserva;

    @Column(name = "numero_plazas", nullable = false)
    private int numeroPlazas;

    @ManyToOne
    @JoinColumn(name = "actividad_id", nullable = false, referencedColumnName = "id")
    private Actividad actividad;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false, referencedColumnName = "id")
    private Cliente cliente;

    public Reserva() {
    }

    public Reserva(LocalDateTime fechaReserva, int numeroPlazas, Cliente cliente, Actividad actividad) {
        this.fechaReserva = fechaReserva;
        this.numeroPlazas = numeroPlazas;
        this.cliente = cliente;
        this.actividad = actividad;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public LocalDateTime getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDateTime fechaReserva) { this.fechaReserva = fechaReserva; }
    public int getNumeroPlazas() { return numeroPlazas; }
    public void setNumeroPlazas(int numeroPlazas) { this.numeroPlazas = numeroPlazas; }
    public Actividad getActividad() { return actividad; }
    public void setActividad(Actividad actividad) { this.actividad = actividad; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
