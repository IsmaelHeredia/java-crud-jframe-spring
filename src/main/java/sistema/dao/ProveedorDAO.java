// Written by Ismael Heredia in the year 2016

package sistema.dao;

import java.util.ArrayList;
import sistema.models.Proveedor;

public interface ProveedorDAO {

    public boolean insert(Proveedor proveedor);

    public boolean update(Proveedor proveedor);

    public boolean delete(Proveedor proveedor);

    public Proveedor findByProveedorId(int id_proveedor_to_load);

    public ArrayList listProveedores(String patron);

    public boolean comprobar_existencia_proveedor_crear(String nombre_empresa);

    public boolean comprobar_existencia_proveedor_editar(int id_proveedor, String nombre_empresa);

}
