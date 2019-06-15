// Written by Ismael Heredia in the year 2016

package sistema.models;

public class Proveedor {
   
    int id_proveedor;
    String nombre_empresa;
    String direccion;
    int telefono;
    String fecha_registro;

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(String fecha_registro) {
        this.fecha_registro = fecha_registro;
    }
    
    public Proveedor() {
        
    }

    public Proveedor(int id_proveedor, String nombre_empresa, String direccion, int telefono, String fecha_registro) {
        this.id_proveedor = id_proveedor;
        this.nombre_empresa = nombre_empresa;
        this.direccion = direccion;
        this.telefono = telefono;
        this.fecha_registro = fecha_registro;
    }

    @Override
    public String toString() {
        return "Proveedor{" + "id_proveedor=" + id_proveedor + ", nombre_empresa=" + nombre_empresa + ", direccion=" + direccion + ", telefono=" + telefono + ", fecha_registro=" + fecha_registro + '}';
    }

}
