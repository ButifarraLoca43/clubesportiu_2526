package es.uji.ei1027.oviaplication.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class Activity {
    private Integer idNumber;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;
    private String location;
    private Integer capacity;
    private Double price;
    private String description;
    private String name;
    private Estado estado;

    public Activity() {}


    public Integer getIdNumber() { return idNumber; }
    public void setIdNumber(Integer idNumber) { this.idNumber = idNumber; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Classificacio{" +
                "idNumber='" + idNumber + '\'' +
                ", date='" + date + '\'' +
                ", time=" + time + '\'' +
                ", location=" + location + '\'' +
                ", capacity='" + capacity + '\'' +
                ", price=" + price + '\'' +
                ", description=" + description + '\'' +
                ", name=" + name + '\'' +
                ", estado=" + estado + '\'' +
                '}';
    }
}