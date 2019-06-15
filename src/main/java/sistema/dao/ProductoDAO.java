// Written by Ismael Heredia in the year 2016

package sistema.dao;

import java.util.ArrayList;
import sistema.models.Producto;

public interface ProductoDAO {

    public boolean insert(Producto producto);

    public boolean update(Producto producto);

    public boolean delete(Producto producto);

    public Producto findByProductoId(int id_producto_to_load);

    public ArrayList listProductos(String patron);

    public String cargarNombreProveedor(int id_proveedor);

    public boolean comprobar_existencia_producto_crear(String nombre_producto);

    public boolean comprobar_existencia_producto_editar(int id_producto, String nombre_producto);

}
