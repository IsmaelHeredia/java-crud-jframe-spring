// Written by Ismael Heredia in the year 2016

package sistema.models;

public class Producto {
    
    int id_producto;
    String nombre_producto;
    String descripcion;
    double precio;
    String fecha_registro;
    int id_proveedor;

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(String fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }
    
    public Producto() {
        
    }

    public Producto(int id_producto, String nombre_producto, String descripcion, double precio,int id_proveedor,String fecha_registro) {
        this.id_producto = id_producto;
        this.nombre_producto = nombre_producto;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fecha_registro = fecha_registro;
        this.id_proveedor = id_proveedor;
    }

    @Override
    public String toString() {
        return "Producto{" + "id_producto=" + id_producto + ", nombre_producto=" + nombre_producto + ", descripcion=" + descripcion + ", precio=" + precio + ", fecha_registro=" + fecha_registro + ", id_proveedor=" + id_proveedor + '}';
    }
 
}
