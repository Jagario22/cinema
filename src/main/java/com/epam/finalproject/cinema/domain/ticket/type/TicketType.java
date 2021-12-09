package com.epam.finalproject.cinema.domain.ticket.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class TicketType implements Serializable {
    private static final long serialVersionUID = 5608844385983949575L;
    private Integer id;
    private String name;
    private BigDecimal price;

    public TicketType(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public TicketType(Integer id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketType that = (TicketType) o;
        return name.equals(that.name) && price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }

    @Override
    public String toString() {
        return "TicketType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
